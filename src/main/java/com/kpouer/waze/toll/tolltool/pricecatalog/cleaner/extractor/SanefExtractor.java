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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
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
        extractMatrix(pdf, Category.Car, 2);
        extractMatrix(pdf, Category.Motorcycle, 6);
    }

    private void extractMatrix(Path pdf, Category category, int page) throws IOException {
        var lines = extractText(pdf, page);
        var output = Path.of(pdf.getParent().toString(), "out");
        var triangleOutputPath = Path.of(output.toString(), "triangle");
        var outputPath = Path.of(triangleOutputPath.toString(), category.name());
        var triangleBuilder = new TriangleBuilder(outputPath);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        if (category == Category.Car) {
            writeFile(outputPath, "Sanef-A16-A29", lines, 1, 23);
            writeFile(outputPath, "Sanef-A1-A2-A26-NORD-A29", lines, 24, 57);
            writeFileA4A26Sud(outputPath, "Sanef-A4-A26-SUD", lines, 59, 96);
        } else {
            writeFile(outputPath, "Sanef-A16-A29", lines, 1, 23);
            writeFile(outputPath, "Sanef-A1-A2-A26-NORD-A29", lines, 24, 57);
            writeFileA4A26Sud(outputPath, "Sanef-A4-A26-SUD", lines, 59, 96);
        }
    }

    private void writeFileA4A26Sud(Path outputPath, String filename, String[] lines, int startLine, int endLine) {
        var currentYear = LocalDate.now().getYear();
        var path = Path.of(outputPath.toString(), currentYear + "_" + filename + ".tsv");
        boolean puttelangePassed = false;
        boolean montreuilAuxLionsPassed = false;
        try (var writer = Files.newBufferedWriter(path)) {
            writer.write("PARIS / NOISY-LE-GRAND (peage de Coutevroult)");
            writer.newLine();
            writer.write("0\t");
            for (var i = startLine; i < endLine; i++) {
                var line = lines[i];

                if ("VERDUN".equals(line)) {
                    // verdun is a special entry
                    writer.write("0\t".repeat(22));
                    writer.write(line);
                } else {
                    var tokens = new ArrayList<>(Arrays.stream(line.split("\t")).toList());
                    if (line.endsWith("MEAUX (A140) / CRECY")) {
                        tokens.add(tokens.size() - 1, "0");
                    } else if (line.endsWith("ST-JEAN-LES-DEUX-JUMEAUX")) {
                        tokens.add(tokens.size() - 1, "0");
                    } else if (line.contains("MONTREUIL-AUX-LIONS")) {
                        montreuilAuxLionsPassed = true;
                        tokens.add(tokens.size() - 1, "0\t0");
                    } else if (tokens.size() > 3) {
                        tokens.add(2, tokens.get(2));

                        if (montreuilAuxLionsPassed) {
                            tokens.add(3, tokens.get(3));
                        }
                        if (line.contains("REIMS NORD")) {
                            tokens.add(tokens.size() - 1, "0");
                        }
                        if (puttelangePassed) {
                            tokens.add(30, "0");
                        }
                    }
                    if (line.endsWith("PUTTELANGE")) {
                        puttelangePassed = true;
                    }
                    writer.write(String.join("\t", tokens));
                }

                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(Path outputPath, String filename, String[] lines, int startLine, int endLine) {
        var currentYear = LocalDate.now().getYear();
        var path = Path.of(outputPath.toString(), currentYear + "_" + filename + ".tsv");
        try (var writer = Files.newBufferedWriter(path)) {
            for (var i = startLine; i < endLine; i++) {
                var line = lines[i];
                if ("VERDUN".equals(line)) {
                    // verdun is a special entry
                    writer.write("0\t".repeat(20));
                }
                writer.write(line);
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
