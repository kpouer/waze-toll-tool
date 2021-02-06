/*
 * Copyright 2021 Matthieu Casanova
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.kpouer.waze.toll.tolltool.waze;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kpouer.waze.toll.tolltool.http.Audit;
import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import com.kpouer.waze.toll.tolltool.pricecatalog.PriceCatalog;
import com.kpouer.waze.toll.tolltool.pricecatalog.PriceResult;
import com.kpouer.waze.toll.tolltool.service.NameNormalizerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Toll {
    private static final Logger                logger = LoggerFactory.getLogger(Toll.class);
    private              String                toll_id;
    private              String                road_local_name;
    private              String                currency;
    private              String                currency_code;
    private              String                polyline;
    private              String                type;
    private              String[]              rules;
    private              List<EntryExitMatrix> entry_exit_matrix;
    private              List<Section>         sections;

    public Toll() {
    }

    public void setToll_id(String toll_id) {
        this.toll_id = toll_id;
    }

    public void setRoad_local_name(String road_local_name) {
        this.road_local_name = road_local_name;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRules(String[] rules) {
        this.rules = rules;
    }

    public void setEntry_exit_matrix(List<EntryExitMatrix> entry_exit_matrix) {
        this.entry_exit_matrix = entry_exit_matrix;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public String getToll_id() {
        return toll_id;
    }

    public String getRoad_local_name() {
        return road_local_name;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public String getPolyline() {
        return polyline;
    }

    public String getType() {
        return type;
    }

    public String[] getRules() {
        return rules;
    }

    public boolean hasEntryExitRule() {
        if (rules == null) {
            return false;
        }
        for (String rule : rules) {
            if ("entry_exit_price".equals(rule)) {
                return true;
            }
        }
        return false;
    }

    public List<EntryExitMatrix> getEntry_exit_matrix() {
        return entry_exit_matrix;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Toll(String toll_id, String road_local_name, String currency, String currency_code, String polyline, String type, String[] rules) {
        this.toll_id         = toll_id;
        this.road_local_name = road_local_name;
        this.currency        = currency;
        this.currency_code   = currency_code;
        this.polyline        = polyline;
        this.type            = type;
        this.rules           = rules;
        entry_exit_matrix    = new ArrayList<>();
        sections             = new ArrayList<>();
    }

    public void buildMatrix(NameNormalizerService nameNormalizerService, Audit audit, PriceCatalog priceGrid, Category category) {
        float[][] fullPrices = new float[sections.size()][];
        for (int i1 = 0; i1 < sections.size(); i1++) {
            Section entry  = sections.get(i1);
            float[] prices = new float[sections.size()];
            fullPrices[i1] = prices;
            for (int i = 0; i < sections.size(); i++) {
                Section exit            = sections.get(i);
                String  entrySectionId  = entry.getSection_id();
                String  exitSectionId   = exit.getSection_id();
                String  normalizedEntry = nameNormalizerService.normalize(entrySectionId);
                String  normalizedExit  = nameNormalizerService.normalize(exitSectionId);
                if (normalizedEntry.equals(normalizedExit)) {
                    prices[i] = 0;
                } else {
                    PriceResult priceResult = priceGrid.getPrice(normalizedEntry, normalizedExit);
                    prices[i] = priceResult.getPrice(category, audit);
                }
            }
        }
        if (entry_exit_matrix == null) {
            entry_exit_matrix = new ArrayList<>();
        }
        entry_exit_matrix.add(new EntryExitMatrix(category.toString(), fullPrices, "", category.getVehicles()));
    }

    public void fixSections() {
        sections.forEach(Section::fix);
    }

    public void removePriceMatrices() {
        if (entry_exit_matrix != null) {
            entry_exit_matrix.clear();
        }
    }
}
