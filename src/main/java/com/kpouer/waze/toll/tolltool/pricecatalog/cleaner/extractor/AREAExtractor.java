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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.CleanerList;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PDFExtractor;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
public class AREAExtractor implements Extractor {
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
        var text = PDFExtractor.extractText(pdf.toFile());
        text = text.replaceAll("\r", "");
        var lines = text.split("\n");
        var cleaner = new CleanerList();
        try (var resourceAsStream = AREAExtractor.class.getResourceAsStream("/cleaner.list");
             var out = new PrintWriter(Files.newBufferedWriter(Path.of(outputPath.toString(), Year.now() + "_AREA-2,4,6,10.tsv"), UTF_8))) {
            assert resourceAsStream != null;
            cleaner.load(resourceAsStream);
            out.println("Code entrée\tGare d'entrée\tCode Sortie\tGare de sortie\tDistance\tClasse 1\tClasse 2\tClasse 3\tClasse 4\tClasse 5");
            var errors = new AtomicInteger();
            Arrays.stream(lines)
                    .filter(line -> !line.startsWith("Page "))
                    .filter(line -> !line.contains("Tarifs"))
                    .filter(line -> !line.startsWith("en vigueu"))
                    .filter(line -> !line.startsWith("Gare "))
                    .filter(line -> !line.startsWith("Km"))
                    .filter(line -> !line.startsWith("Classe 1 Classe"))
                    .filter(line -> !line.contains("Réseau"))
                    .filter(line -> !line.contains("vigueur"))
                    .filter(line -> !line.contains("tarifaire"))
                    .filter(line -> !line.contains("TARIFS DE PÉAGE"))
                    .filter(line -> !line.contains("Distance"))
                    .filter(line -> line.length() > 10)
                    .map(line -> EURO.matcher(line).replaceAll(""))
                    .map(line -> SPACE.matcher(line).replaceAll("\t"))
                    .map(line -> COMPILE.matcher(line).replaceAll(matchResult -> '\t' + matchResult.group(1)))
                    .map(cleaner::clean)
                    .forEach(line -> {
                        var split = line.split("\t");
                        if (split.length != 10) {
                            errors.incrementAndGet();
                            System.err.println(split.length + " " + line);
                        } else {
                            out.println(line);
                        }
                    });
            System.out.println(errors + " errors");
        }
    }
}
