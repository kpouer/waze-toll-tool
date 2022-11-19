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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class AREACleaner {
    private static final Pattern EURO    = Pattern.compile(" €");
    private static final Pattern MINUS   = Pattern.compile("(A\\d+) - ");
    private static final Pattern TABS    = Pattern.compile("(\\d) (\\d)");
    private static final Pattern TABS2   = Pattern.compile("(\\d) ([A-Z])");
    private static final Pattern TABS3   = Pattern.compile("([A-Z]) (A\\d)");
    private static final Pattern TABS4   = Pattern.compile("(\\)) (\\d)");
    private static final Pattern TABS5   = Pattern.compile("(\\)) (A\\d)");
    private static final Pattern TABS6   = Pattern.compile("([A-Z]) (\\d)");
    private static final Pattern SPACE   = Pattern.compile(" ");
    private static final Pattern COMPILE = Pattern.compile(" (\\d)");

    public static void main(String[] args) throws IOException {
        String filename = args[0];
        File   pdfFile  = new File(filename);
        String text     = PDFExtractor.extractText(pdfFile);
        text = text.replaceAll("\r", "");
        String[]    lines       = text.split("\n");
        String      outfilename = filename + ".tsv";
        CleanerList cleaner     = new CleanerList();
        cleaner.load(CofirouteCleaner.class.getResourceAsStream("/cleaner.list"));
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outfilename)))) {
            writer.println("Code entrée\tGare d'entrée\tCode Sortie\tGare de sortie\tDistance\tClasse 1\tClasse 2\tClasse 3\tClasse 4\tClasse 5");
            AtomicInteger errors = new AtomicInteger();
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
                  .map(line -> cleaner.clean(line))
                  .forEach(x -> {
                      String[] split = x.split("\t");
                      if (split.length != 10) {
                          errors.incrementAndGet();
                          System.err.println(split.length + " " + x);
                      }
                      writer.println(x);
                  });
            System.out.println(errors);
        }
    }
}
