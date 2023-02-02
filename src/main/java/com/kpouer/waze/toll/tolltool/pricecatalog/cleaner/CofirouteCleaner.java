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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class CofirouteCleaner {
    private static final Pattern EURO = Pattern.compile(" €");
    private static final Pattern MINUS = Pattern.compile("(A\\d+) - ");
    private static final Pattern TABS = Pattern.compile("(\\d) (\\d)");
    private static final Pattern TABS2 = Pattern.compile("(\\d) ([A-Z])");
    private static final Pattern TABS3 = Pattern.compile("([A-Z]) (A\\d)");
    private static final Pattern TABS4 = Pattern.compile("(\\)) (\\d)");
    private static final Pattern TABS5 = Pattern.compile("(\\)) (A\\d)");
    private static final Pattern TABS6 = Pattern.compile("([A-Z]) (\\d)");
    private static final Pattern SPACE_PATTERN = Pattern.compile(" +");

    public static void main(String[] args) throws IOException {
        var filename = args[0];
        var pdfFile = new File(filename);
        var text = PDFExtractor.extractText(pdfFile, 8);
        text = text.replaceAll("\r", "");
        var lines = text.split("\n");
        var outfilename = filename + ".tsv";
        var cleaner = new CleanerList();
        cleaner.load(CofirouteCleaner.class.getResourceAsStream("/cleaner.list"));
        try (var writer = new PrintWriter(new BufferedWriter(new FileWriter(outfilename)));
             var out = new PrintWriter(new BufferedWriter(new FileWriter("Cofiroute-3,6,7,11.tsv")))) {
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
