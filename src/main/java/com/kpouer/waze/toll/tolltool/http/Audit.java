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
package com.kpouer.waze.toll.tolltool.http;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Audit {
    private static final Logger        logger = LoggerFactory.getLogger(Audit.class);
    private              int           failCars;
    private              int           failMotorcycles;
    private              int           obsoletePrices;
    private final        StringBuilder builder;

    public Audit() {
        builder = new StringBuilder(500000);
    }

    public void incrementObsoletePrice() {
        obsoletePrices++;
    }

    public void increment(Category category) {
        switch (category) {
            case Car:
                failCars++;
                break;
            case Motorcycle:
                failMotorcycles++;
                break;
        }
    }

    public String getAudit() {
        builder.append("Missing car prices         : ").append(failCars).append('\n');
        builder.append("Missing motorcycles prices : ").append(failMotorcycles).append('\n');
        builder.append("Obsolete prices : ").append(obsoletePrices).append('\n');
        return builder.toString();
    }

    public void append(String auditLine) {
        builder.append(auditLine).append('\n');
        logger.warn(auditLine);
    }

    public void newLine() {
        builder.append('\n');
    }
}
