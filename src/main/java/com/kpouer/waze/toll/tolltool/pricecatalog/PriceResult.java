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
package com.kpouer.waze.toll.tolltool.pricecatalog;

import com.kpouer.waze.toll.tolltool.http.Audit;

import static com.kpouer.waze.toll.tolltool.pricecatalog.PriceItem.CURRENT_YEAR;

public class PriceResult {
    private final PriceItem priceItemForward;
    private final PriceItem priceItemBackward;
    private final String    entry;
    private final String    exit;

    public PriceResult(String entry, String exit, PriceItem priceItemForward, PriceItem priceItemBackward) {
        this.entry             = entry;
        this.exit              = exit;
        this.priceItemForward  = priceItemForward;
        this.priceItemBackward = priceItemBackward;
    }

    public PriceResult(String entry, String exit) {
        this(entry, exit, null, null);
    }

    public PriceItem getPriceItemForward() {
        return priceItemForward;
    }

    public PriceItem getPriceItemBackward() {
        return priceItemBackward;
    }

    public String getEntry() {
        return entry;
    }

    public String getExit() {
        return exit;
    }

    public float getPrice(Category category, Audit audit) {
        float         price;
        CategoryPrice forwardCategoryPrice  = priceItemForward == null ? null : priceItemForward.getCategoryPrice(category);
        CategoryPrice backwardCategoryPrice = priceItemBackward == null ? null : priceItemBackward.getCategoryPrice(category);

        assert forwardCategoryPrice != null || backwardCategoryPrice != null;

        if (forwardCategoryPrice != null) {
            if (backwardCategoryPrice != null &&
                (backwardCategoryPrice.getYear() > forwardCategoryPrice.getYear() ||
                    (backwardCategoryPrice.getYear() == forwardCategoryPrice.getYear() && backwardCategoryPrice.getPrice() > forwardCategoryPrice.getPrice()))) {
                price = backwardCategoryPrice.getPrice();
                audit.append(String.format("Backward price is more up to date %s %s\t%s", category, entry, exit));
            } else {
                if (CURRENT_YEAR != forwardCategoryPrice.getYear()) {
                    audit.append(String.format("Price is obsolete %s %s\t%s, from file %s", category, entry, exit, forwardCategoryPrice.getPath()));
                    audit.incrementObsoletePrice();
                }
                price = forwardCategoryPrice.getPrice();
            }
        } else {
            if (backwardCategoryPrice != null) {
                if (CURRENT_YEAR != backwardCategoryPrice.getYear()) {
                    audit.append(String.format("Backward price is obsolete %s %s\t%s, from file %s", category, entry, exit, backwardCategoryPrice.getPath()));
                    audit.incrementObsoletePrice();
                }
                price = backwardCategoryPrice.getPrice();
            } else {
                price = 0;
                audit.increment(category);
                audit.append(String.format("Unknown price for %s %s\t%s", category, entry, exit));
            }
        }
        return price;
    }
}
