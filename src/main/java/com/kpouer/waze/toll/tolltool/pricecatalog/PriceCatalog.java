/*
 * Copyright 2021-2024 Matthieu Casanova
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
package com.kpouer.waze.toll.tolltool.pricecatalog;

import com.kpouer.waze.toll.tolltool.pricecatalog.parser.PriceParser;
import com.kpouer.waze.toll.tolltool.service.FlatPriceParser;
import com.kpouer.waze.toll.tolltool.service.OneDirectionMatrixPriceParser;
import com.kpouer.waze.toll.tolltool.service.TrianglePriceParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Component
@Scope("prototype")
@Slf4j
public class PriceCatalog {
    private final FlatPriceParser flatPriceParser;
    private final TrianglePriceParser trianglePriceParser;
    private final OneDirectionMatrixPriceParser oneDirectionMatrixPriceParser;
    @Getter
    private final Map<PriceItem, PriceItem> prices;

    public PriceCatalog(FlatPriceParser flatPriceParser, TrianglePriceParser trianglePriceParser, OneDirectionMatrixPriceParser oneDirectionMatrixPriceParser) {
        this.flatPriceParser = flatPriceParser;
        this.trianglePriceParser = trianglePriceParser;
        this.oneDirectionMatrixPriceParser = oneDirectionMatrixPriceParser;
        prices = new HashMap<>();
    }

    public void load(Path rootPath) throws IOException {
        log.info("load {}", rootPath);
        Collection<FolderDefinition> folderDefinitions = new ArrayList<>();
        folderDefinitions.add(new FolderDefinition("flat", "tsv", flatPriceParser));
        folderDefinitions.add(new FolderDefinition("triangle/car", "tsv", trianglePriceParser, Category.Car));
        folderDefinitions.add(new FolderDefinition("triangle/motorcycle", "tsv", trianglePriceParser, Category.Motorcycle));
        folderDefinitions.add(new FolderDefinition("onedirmatrix/car", "tsv", oneDirectionMatrixPriceParser, Category.Car));
        folderDefinitions.add(new FolderDefinition("onedirmatrix/motorcycle", "tsv", oneDirectionMatrixPriceParser, Category.Motorcycle));

        Collection<PriceItem[]> priceGridList = new ArrayList<>();
        for (FolderDefinition folderDefinition : folderDefinitions) {
            Path dir = Path.of(rootPath.toString(), folderDefinition.getFolder());
            if (!Files.isDirectory(dir)) {
                log.warn("{} is not a directory", dir);
                continue;
            }
            try (Stream<Path> files = Files.list(dir)) {
                files
                    .filter(path -> path.toString().toLowerCase().endsWith(folderDefinition.getExtension()))
                    .forEach(path -> loadFolder(priceGridList, folderDefinition, path));
            }
        }

        mergePrices(priceGridList);
    }

    private void mergePrices(Iterable<PriceItem[]> priceGridList) {
        for (PriceItem[] priceGrid : priceGridList) {
            for (PriceItem priceItem : priceGrid) {
                var existingPrice = prices.get(priceItem);
                if (existingPrice != null) {
                    existingPrice.merge(priceItem);
                } else {
                    prices.put(priceItem, priceItem);
                }
            }
        }
    }

    private static void loadFolder(Collection<PriceItem[]> priceGridList, FolderDefinition folderDefinition, Path path) {
        try {
            log.info("load {}", path);
            PriceParser priceParser = folderDefinition.getPriceParser();
            priceParser.setCategory(folderDefinition.getCategory());
            priceGridList.add(priceParser.getPriceGrid(path));
        } catch (Exception e) {
            log.error("Error while loading prices of {}", path, e);
        }
    }

    @NonNull
    public PriceResult getPrice(String entry, String exit) {
        PriceItem priceItemForward = prices.get(new DefaultPriceItem(entry, exit));
        PriceItem priceItemBackward = prices.get(new DefaultPriceItem(exit, entry));
        if (priceItemForward == null || priceItemBackward == null) {
            return new PriceResult(entry, exit);
        }
        return new PriceResult(entry, exit, priceItemForward, priceItemBackward);
    }

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    private static class FolderDefinition {
        private final String folder;
        private final String extension;
        private final PriceParser priceParser;
        /**
         * The category is not mandatory depending on the file type
         */
        private Category category;
    }
}
