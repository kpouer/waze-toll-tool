/*
 * Copyright 2022-2023 Matthieu Casanova
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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class TriangleBuilder implements TSVBuilder {
    private final Path outputPath;

    @Override
    public void doBuildFile(String name, Category category, String[] headers, List<String> lines, Collection<Integer> clonedColumns) throws IOException {
        var tsvFile = Path.of(outputPath.toString(), java.time.Year.now() + "_" + name + '-' + category + ".tsv");
        try (var writer = Files.newBufferedWriter(tsvFile)) {
            writer.write(headers[0]);
            writer.write('\n');
            for (var i = 0; i < headers.length - 1; i++) {
                var header = headers[i + 1];
                var ln = lines.get(i);
                var line = ln.trim().split("\t");
                for (var j = 0; j < line.length; j++) {
                    var token = line[j];
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
        System.out.println("File " + tsvFile + " created");
    }
}
