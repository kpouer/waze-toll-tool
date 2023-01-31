/*
 * Copyright 2021-2023 Matthieu Casanova
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
package com.kpouer.waze.toll.tolltool.service;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import com.kpouer.waze.toll.tolltool.pricecatalog.CategoryPrice;
import com.kpouer.waze.toll.tolltool.pricecatalog.DefaultPriceItem;
import com.kpouer.waze.toll.tolltool.pricecatalog.PriceItem;
import com.kpouer.waze.toll.tolltool.pricecatalog.parser.PriceParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class TrianglePriceParser implements PriceParser {
    private final        NameNormalizerService nameNormalizerService;
    private              Category              category;

    public TrianglePriceParser(NameNormalizerService nameNormalizerService) {
        this.nameNormalizerService = nameNormalizerService;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public PriceItem[] getPriceGrid(Path path) throws IOException {
        String                absolutePath = path.toString();
        List<String>          lines        = Files.readAllLines(path);
        Collection<PriceItem> priceItems   = new ArrayList<>(lines.size() * lines.size());
        String[][] lineTokens = lines
            .stream()
            .map(s -> s.trim().replace(',', '.'))
            .map(s -> StringUtils.split(s, SEPARATOR)).toArray(String[][]::new);
        int rowCount = lineTokens.length;

        String filename = path.getFileName().toString();
        short  year     = filename.startsWith("202") ? Short.parseShort(filename.substring(0, 4)) : 2019;
        for (int row = 0; row < rowCount; row++) {
            String[] lineToken = lineTokens[row];
            String   entry     = lineToken[lineToken.length - 1];
            for (int column = row + 1; column < rowCount; column++) {
                String[] lineTokens2 = lineTokens[column];
                String   exit        = lineTokens2[lineTokens2.length - 1];
                float    value       = 0;
                try {
                    value = Float.parseFloat(lineTokens2[row]);
                } catch (NumberFormatException e) {
                    log.error("Unable to parse value in {} {}\t{} {}", path, entry, exit, lineTokens2[row]);
                }
                entry = nameNormalizerService.normalize(entry);
                exit  = nameNormalizerService.normalize(exit);
                priceItems.add(new DefaultPriceItem(entry, exit, category, new CategoryPrice(value, year, absolutePath)));
                priceItems.add(new DefaultPriceItem(exit, entry, category, new CategoryPrice(value, year, absolutePath)));
            }
        }

        return priceItems.toArray(PriceItem[]::new);
    }
}
