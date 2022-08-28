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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

public class TriangleBuilder implements TSVBuilder {
    private final File path;
    private final int  currentYear;

    public TriangleBuilder(File path) {
        this.path   = path;
        currentYear = new GregorianCalendar().get(Calendar.YEAR);
    }

    @Override
    public void buildFile(String name, Category category, String[] headers, List<String> lines, int skip, Collection<Integer> clonedColumns) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path, currentYear + "_" + name + '-' + category + ".tsv")))) {
            // insert an empty line
            writer.write(headers[0]);
            writer.write('\n');
            for (int i = 0; i < headers.length - 1; i++) {
                String header = headers[i + 1];
                String[] line   = lines.get(i + skip).trim().split("\t");
                for (int j = 0; j < line.length; j++) {
                    String token = line[j];
                    writer.write(token);
                    writer.write('\t');
                    if (clonedColumns.contains(j + 1)) {
                        writer.write(token);
                        writer.write('\t');
                    }
                }
                if (clonedColumns.contains(i + 1)) {
                    writer.write("0\t");
                }
                writer.write(header);
                if (i < headers.length - 2) {
                    writer.write('\n');
                }
            }
        }
    }
}
