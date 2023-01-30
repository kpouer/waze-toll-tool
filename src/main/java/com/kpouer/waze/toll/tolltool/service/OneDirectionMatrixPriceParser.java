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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Setter
public class OneDirectionMatrixPriceParser implements PriceParser {
    private final        NameNormalizerService nameNormalizerService;
    private              Category              category;

    @Override
    public PriceItem[] getPriceGrid(Path path) throws IOException {
        String                absolutePath = path.toString();
        List<String>          lines        = Files.readAllLines(path);
        String                filename     = path.getFileName().toString();
        short                 year         = filename.startsWith("202") ? Short.parseShort(filename.substring(0, 4)) : 2019;
        Collection<PriceItem> priceItems   = new ArrayList<>(lines.size() * lines.size());
        String[][] lineTokens = lines
            .stream()
            .map(s -> s.trim().replace(',', '.'))
            .map(s -> StringUtils.split(s, SEPARATOR)).toArray(String[][]::new);

        String[] headerLineTokens = lineTokens[0];

        for (int row = 1; row < lineTokens.length; row++) {
            String[] lineToken = lineTokens[row];
            String   entry     = lineToken[0];
            for (int column = 1; column < lineToken.length; column++) {
                String exit = null;
                try {
                    exit = headerLineTokens[column];
                } catch (ArrayIndexOutOfBoundsException e) {
                    log.error("Wrong column count in line {}", row, e);
                }
                float value = 0;
                try {
                    value = Float.parseFloat(lineToken[column]);
                } catch (NumberFormatException e) {
                    log.error("Unable to parse value in " + path + ' ' + entry + "->" + exit + ' ' + lineToken[row], e);
                }
                String normalizedEntry = nameNormalizerService.normalize(entry);
                String normalizedExit  = nameNormalizerService.normalize(exit);
                // don't share in case of merge
                priceItems.add(new DefaultPriceItem(normalizedEntry, normalizedExit, category, new CategoryPrice(value, year, absolutePath)));
                priceItems.add(new DefaultPriceItem(normalizedExit, normalizedEntry, category, new CategoryPrice(value, year, absolutePath)));
            }
        }

        return priceItems.toArray(PriceItem[]::new);
    }
}
