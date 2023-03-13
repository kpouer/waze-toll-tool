/*
 * Copyright 2022 Matthieu Casanova
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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

public class RectangleBuilder implements TSVBuilder {
    private final File path;
    private final int  currentYear;

    public RectangleBuilder(File path) {
        this.path   = path;
        currentYear = new GregorianCalendar().get(Calendar.YEAR);
    }

    @Override
    public void buildFile(String name, Category category, String[] headers, List<String> lines, int skipLine, int column, Collection<Integer> clonedColumns) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(new File(path, currentYear + "_" + name + '-' + category + ".tsv")))) {
            // insert columns header
            writer.write(headers[0]);
            writer.write('\n');
            for (var i = 0; i < headers.length - 1; i++) {
                var header = headers[i + 1];
                var line = lines.get(i + skipLine);
                if (i == 0 && column > 0) {
                    line = line.substring(column);
                }
                line   = line.replaceAll("^.*[^\t^0-9]\\t", ""); // replace beginning as I write my row name myself
                writer.write(header);
                writer.write('\t');
                writer.write(line);
                writer.write('\n');
            }
        }
    }
}
