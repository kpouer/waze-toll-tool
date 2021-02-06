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
package com.kpouer.waze.toll.tolltool.pricecatalog;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PriceGrid {
    private final Path                      path;
    private       PriceItem[]               priceItemList;
    private final Map<PriceItem, PriceItem> prices;

    public PriceGrid(Path path, PriceItem[] priceItemList) {
        this.path          = path;
        this.priceItemList = priceItemList;
        prices             = new HashMap<>(priceItemList.length);
        for (PriceItem priceItem : priceItemList) {
            prices.put(priceItem, priceItem);
        }
    }

    public float getPrice(String entry, String exit, Category category) {
        if (entry.equals(exit)) {
            return 0;
        }

        PriceItem priceItem = prices.get(new DefaultPriceItem(entry, exit));
        if (priceItem == null)
            return -1;
        return priceItem.getPrice(category);
    }

    public Map<PriceItem, PriceItem> getPrices() {
        return prices;
    }

    public Path getPath() {
        return path;
    }
}
