/*
 * Copyright 2021-2026 Matthieu Casanova
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
import com.kpouer.waze.toll.tolltool.pricecatalog.DefaultPriceItem;
import com.kpouer.waze.toll.tolltool.pricecatalog.PriceItem;
import com.kpouer.waze.toll.tolltool.pricecatalog.parser.PriceParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class FlatPriceParser implements PriceParser {
    private final NameNormalizerService nameNormalizerService;

    @Override
    public PriceItem[] getPriceGrid(Path path) throws IOException {
        log.info("Loading {}", path);
        String absolutePath = path.toString();
        StringUtils.split("-");
        String[]     end             = StringUtils.split(absolutePath, "-");
        String       s               = end[end.length - 1];
        String[]     tokens          = StringUtils.split(s.substring(0, s.length() - 4), ",");
        int          entryIndex      = Integer.parseInt(tokens[0]) - 1;
        int          exitIndex       = Integer.parseInt(tokens[1]) - 1;
        int          carIndex        = Integer.parseInt(tokens[2]) - 1;
        int          motorcycleIndex = Integer.parseInt(tokens[3]) - 1;
        String       filename        = path.getFileName().toString();
        short        year            = filename.startsWith("202") ? Short.parseShort(filename.substring(0, 4)) : 2019;
        List<String> lines           = Files.readAllLines(path);
        // remove header
        lines.removeFirst();

        return lines
            .parallelStream()
            .map(line -> line.trim().replace(',', '.'))
            .map(line -> StringUtils.split(line, SEPARATOR))
            .map(fields -> getDefaultPriceItem(absolutePath, entryIndex, exitIndex, carIndex, motorcycleIndex, year, fields))
            .filter(Objects::nonNull)
            .toArray(DefaultPriceItem[]::new);
    }

    @Nullable
    private DefaultPriceItem getDefaultPriceItem(String absolutePath, int entryIndex, int exitIndex, int carIndex, int motorcycleIndex, short year, String[] fields) {
        try {
            return new DefaultPriceItem(
                absolutePath,
                nameNormalizerService.normalize(fields[entryIndex]),
                nameNormalizerService.normalize(fields[exitIndex]),
                year,
                fields,
                carIndex,
                motorcycleIndex);
        } catch (Exception e) {
            log.error("Error parsing {}", String.join("\t", fields), e);
        }
        return null;
    }

    @Override
    public void setCategory(Category category) {
    }
}
