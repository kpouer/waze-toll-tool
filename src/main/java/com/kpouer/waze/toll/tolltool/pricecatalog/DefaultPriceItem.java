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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class DefaultPriceItem implements PriceItem {
    private final String entry;
    private final String exit;

    private CategoryPrice carPrice;
    private CategoryPrice motorcyclePrice;

    public DefaultPriceItem(String path, String entry, String exit, short year, String[] fields, int carIndex, int motorCycleIndex) {
        this(entry, exit);
        if (carIndex != -1) {
            carPrice = new CategoryPrice(Float.parseFloat(fields[carIndex]), year, path);
        }
        if (motorCycleIndex != -1) {
            motorcyclePrice = new CategoryPrice(Float.parseFloat(fields[motorCycleIndex]), year, path);
        }
    }

    public DefaultPriceItem(String entry, String exit, Category category, CategoryPrice categoryPrice) {
        this(entry, exit);
        switch (category) {
            case Car -> carPrice = categoryPrice;
            case Motorcycle -> motorcyclePrice = categoryPrice;
        }
    }

    @Override
    public String getCarPath() {
        return carPrice == null ? null : carPrice.getPath();
    }

    @Override
    public String getMotorcyclePath() {
        return motorcyclePrice == null ? null : motorcyclePrice.getPath();
    }

    @Override
    public String getEntry() {
        return entry;
    }

    @Override
    public String getExit() {
        return exit;
    }

    @Override
    public float getCarPrice() {
        return carPrice == null ? -1 : carPrice.getPrice();
    }

    @Override
    public short getYear(Category category) {
        return switch (category) {
            case Car -> getCarYear();
            case Motorcycle -> getMotorcycleYear();
        };
    }

    @Override
    public String getPath(Category category) {
        return switch (category) {
            case Car -> getCarPath();
            case Motorcycle -> getMotorcyclePath();
        };
    }

    @Override
    public float getMotorcyclePrice() {
        return motorcyclePrice == null ? -1 : motorcyclePrice.getPrice();
    }

    @Override
    public short getCarYear() {
        return carPrice == null ? -1 : carPrice.getYear();
    }

    @Override
    public short getMotorcycleYear() {
        return motorcyclePrice == null ? 0 : motorcyclePrice.getYear();
    }

    @Override
    public float getPrice(Category category) {
        return switch (category) {
            case Car -> getCarPrice();
            case Motorcycle -> getMotorcyclePrice();
        };
    }

    @Override
    public CategoryPrice getCarCategoryPrice() {
        return carPrice;
    }

    @Override
    public CategoryPrice getMotorcycleCategoryPrice() {
        return motorcyclePrice;
    }

    @Override
    public void merge(PriceItem priceItem) {
        if (carPrice != null) {
            carPrice.merge(priceItem.getCarCategoryPrice());
        } else {
            carPrice = priceItem.getCarCategoryPrice();
        }
        if (motorcyclePrice != null) {
            motorcyclePrice.merge(priceItem.getMotorcycleCategoryPrice());
        } else {
            motorcyclePrice = priceItem.getMotorcycleCategoryPrice();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultPriceItem that = (DefaultPriceItem) o;
        return entry.equals(that.entry) &&
            exit.equals(that.exit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry, exit);
    }

    @Override
    public int compareTo(PriceItem o) {
        if (entry.equals(o.getEntry())) {
            return exit.compareTo(o.getExit());
        }
        return entry.compareTo(o.getEntry());
    }

    @Override
    public CategoryPrice getCategoryPrice(Category category) {
        return switch (category) {
            case Car -> carPrice;
            case Motorcycle -> motorcyclePrice;
        };
    }

    @Override
    public String toString() {
        return "DefaultPriceItem{" +
            "entry='" + entry + '\'' +
            ", exit='" + exit + '\'' +
            ", carPrice=" + carPrice +
            ", motorcyclePrice=" + motorcyclePrice +
            '}';
    }
}
