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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PDFExtractor;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder.TriangleBuilder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SanefExtractor implements Extractor {
    private static final Pattern EURO = Pattern.compile(" €");
    private static final Pattern MINUS = Pattern.compile("(A\\d+) - ");
    private static final Pattern TABS = Pattern.compile("(\\d) (\\d)");
    private static final Pattern TABS2 = Pattern.compile("(\\d) ([A-Z])");
    private static final Pattern TABS3 = Pattern.compile("([A-Z]) (A\\d)");
    private static final Pattern TABS4 = Pattern.compile("(\\)) (\\d)");
    private static final Pattern TABS5 = Pattern.compile("(\\)) (A\\d)");
    private static final Pattern TABS6 = Pattern.compile("([A-Z]) (\\d)");
    private static final Pattern SPACE = Pattern.compile(" ");
    private static final Pattern COMPILE = Pattern.compile(" (\\d)");

    private final Path pdf;

    @Override
    public void extract() throws IOException {
        var outputPath = Path.of(pdf.getParent().toString(), "out");
        var triangleOutputPath = Path.of(outputPath.toString(), "triangle");
        var triangleBuilder = new TriangleBuilder(triangleOutputPath);
        extractMatrix(pdf, Category.Car, triangleBuilder, 4);
        extractMatrix(pdf, Category.Motorcycle, triangleBuilder, 4);
    }

    private void extractMatrix(Path pdf, Category category, TriangleBuilder triangleBuilder, int page) throws IOException {
        var lines = extractText(pdf, page);
        var output = Path.of(pdf.getParent().toString(), "out");
        var triangleOutputPath = Path.of(output.toString(), "triangle");
        var outputPath = Path.of(triangleOutputPath.toString(), category.name());
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        if (category == Category.Car) {
            writeFile(outputPath, "Sanef-A16-A29", lines, 1, 23);
            writeFile(outputPath, "Sanef-A1-A2-A26-NORD-A29", lines, 24, 57);
            writeFile(outputPath, "Sanef-A4-A26-SUD", lines, 59, 96);
        } else {
            writeFile(outputPath, "Sanef-A16-A29", lines, 1, 23);
            writeFile(outputPath, "Sanef-A1-A2-A26-NORD-A29", lines, 24, 57);
            writeFile(outputPath, "Sanef-A4-A26-SUD", lines, 59, 96);
        }
    }

    private void writeFile(Path outputPath, String filename, String[] lines, int startLine, int endLine) {
        var currentYear = LocalDate.now().getYear();
        Path path = Path.of(outputPath.toString(), currentYear + "_" + filename + ".tsv");
        try (var writer = Files.newBufferedWriter(path)) {
            for (var i = startLine; i < endLine; i++) {
                writer.write(lines[i]);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] extractText(Path pdf, int page) throws IOException {
        return PDFExtractor.extractText(pdf.toFile(), page, page)
                           .replaceAll("\r", "")
                           .replaceAll(" N°\\d+", "")
                           .replaceAll("(\\d+) (\\d+)", "$1\t$2")
                           .replaceAll("(\\d+) (\\w)", "$1\t$2")
                           .split("\n");
    }
}
