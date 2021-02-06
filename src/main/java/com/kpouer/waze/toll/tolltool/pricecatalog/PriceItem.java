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

import java.time.LocalDate;

public interface PriceItem extends Comparable<PriceItem> {
    short CURRENT_YEAR = (short) LocalDate.now().getYear();

    CategoryPrice getCategoryPrice(Category category);

    CategoryPrice getCarCategoryPrice();

    CategoryPrice getMotorcycleCategoryPrice();

    String getCarPath();

    String getMotorcyclePath();

    String getEntry();

    String getExit();

    float getCarPrice();

    String getPath(Category category);

    float getMotorcyclePrice();

    float getPrice(Category category);

    short getYear(Category category);

    void merge(PriceItem priceItem);

    /**
     * Retourne l'année de création du fichier de péage (pour être sur qu'on a mis à jour).
     *
     * @return une année
     */
    short getCarYear();

    short getMotorcycleYear();
}
