/*
 * Copyright 2021-2025 Matthieu Casanova
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

import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor.Extractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@Slf4j
public class CofirouteCleaner implements Extractor {
    private static final Pattern EURO = Pattern.compile(" €");
    private static final Pattern MINUS = Pattern.compile("(A\\d+) - ");
    private static final Pattern TABS = Pattern.compile("(\\d) (\\d)");
    private static final Pattern TABS2 = Pattern.compile("(\\d) ([A-Z])");
    private static final Pattern TABS3 = Pattern.compile("([A-Z]) (A\\d)");
    private static final Pattern TABS4 = Pattern.compile("(\\)) (\\d)");
    private static final Pattern TABS5 = Pattern.compile("(\\)) (A\\d)");
    private static final Pattern TABS6 = Pattern.compile("([A-Z]) (\\d)");
    private static final Pattern SPACE_PATTERN = Pattern.compile(" +");

    private final Path pdf;

    @Override
    public void extract() throws IOException {
        log.info("Extracting {}", pdf);
        var outputPath = Path.of(pdf.getParent().toString(), "out");
        var text = PDFExtractor.extractText(pdf.toFile(), 8);
        text = text.replaceAll("\r", "");
        var lines = text.split("\n");
        var currentYear = LocalDate.now().getYear();
        var outfilename = currentYear + "_Cofiroute-3,6,7,11.tsv";
        var cleaner = new CleanerList();
        try (var resourceAsStream = CofirouteCleaner.class.getResourceAsStream("/cleaner.list");
            var writer = new PrintWriter(Files.newBufferedWriter(Paths.get(outfilename), UTF_8));
            var out = new PrintWriter(Files.newBufferedWriter(Path.of(outputPath.toString(), currentYear + "_Cofiroute-3,6,7,11.tsv")))) {
            assert resourceAsStream != null;
            cleaner.load(resourceAsStream);
            out.println("Autoroute entrée\tPéage entrée\tVille entrée\tAutoroute sortie\tPéage sortie\tVille sortie\tClasse 1\tClasse 2\tClasse 3\tClasse 4\tClasse 5");
            var errors = new AtomicInteger();
            Arrays.stream(lines)
                  .filter(line -> line.length() > 10)
                  .filter(line -> line.charAt(0) == 'A')
                  .filter(line -> !line.startsWith("AUTOROUTE"))
                  .filter(line -> !line.contains("Avant de partir"))
                  .filter(line -> !line.contains("Twitter"))
                  .filter(line -> !line.contains("QUELLE VOIE UTILISER"))
                  .filter(line -> !line.contains("Attention"))
                  .filter(line -> !line.contains("chargement"))
                  .map(line -> EURO.matcher(line).replaceAll(""))
                  .map(line -> MINUS.matcher(line).replaceAll(matchResult -> matchResult.group(1) + "\t-\t"))
                  .map(line -> TABS.matcher(line).replaceAll(matchResult -> matchResult.group(1) + '\t' + matchResult.group(2)))
                  .map(line -> TABS2.matcher(line).replaceAll(matchResult -> matchResult.group(1) + '\t' + matchResult.group(2)))
                  .map(line -> TABS3.matcher(line).replaceAll(matchResult -> matchResult.group(1) + '\t' + matchResult.group(2)))
                  .map(line -> TABS4.matcher(line).replaceAll(matchResult -> matchResult.group(1) + '\t' + matchResult.group(2)))
                  .map(line -> TABS5.matcher(line).replaceAll(matchResult -> matchResult.group(1) + '\t' + matchResult.group(2)))
                  .map(line -> TABS6.matcher(line).replaceAll(matchResult -> matchResult.group(1) + '\t' + matchResult.group(2)))
                  .map(line -> SPACE_PATTERN.matcher(line).replaceAll("\t"))
                  .map(cleaner::clean)
                  .map(line -> line.replaceAll("A4\t- ", "A4\t-\t"))
                  .map(line -> line.replaceAll("SAINT MAIXENT\t/\tLUSIGNAN", "SAINT MAIXENT / LUSIGNAN"))
                  .forEach(line -> {
                      var split = line.split("\t");
                      if (split.length != 11) {
                          errors.incrementAndGet();
                          System.err.println(split.length + " " + line);
                      } else {
                          out.println(line);
                      }
                      writer.println(line);
                  });
            System.out.println(errors + " errors");
        }
    }
}
