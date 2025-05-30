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
public class RectangleBuilder implements TSVBuilder {
    private final Path outputPath;

    @Override
    public void buildFile(String name, Category category, String[] headers, List<String> lines, int skipLine, int column, Collection<Integer> clonedColumns) throws IOException {
        var tsvPath = Path.of(outputPath.toString(), java.time.Year.now() + "_" + name + '-' + category + ".tsv");
        try (var writer = Files.newBufferedWriter(tsvPath)) {
            // insert columns header
            writer.write(headers[0]);
            writer.write('\n');
            for (var i = 0; i < headers.length - 1; i++) {
                var header = headers[i + 1].trim(); // sometimes a \r remains
                var line = lines.get(i + skipLine);
                if (i == 0 && column > 0) {
                    try {
                        line = line.substring(column);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
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
