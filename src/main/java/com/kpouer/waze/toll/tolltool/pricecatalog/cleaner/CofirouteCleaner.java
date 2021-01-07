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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class CofirouteCleaner {
    private static final Pattern EURO  = Pattern.compile(" €");
    private static final Pattern MINUS = Pattern.compile("(A\\d+) - ");
    private static final Pattern TABS  = Pattern.compile("(\\d) (\\d)");
    private static final Pattern TABS2 = Pattern.compile("(\\d) ([A-Z])");
    private static final Pattern TABS3 = Pattern.compile("([A-Z]) (A\\d)");
    private static final Pattern TABS4 = Pattern.compile("(\\)) (\\d)");
    private static final Pattern TABS5 = Pattern.compile("(\\)) (A\\d)");
    private static final Pattern TABS6 = Pattern.compile("([A-Z]) (\\d)");

    public static void main(String[] args) throws IOException {
        String filename    = args[0];
        String outfilename = filename + ".out";
        Writer out;
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outfilename)))) {
            Files
                .lines(Path.of(filename))
                .filter(line -> !line.isEmpty() && 'A' == line.charAt(0) && !line.startsWith("AUTOROUTE"))
                .map(line -> EURO.matcher(line).replaceAll(""))
                .map(line -> MINUS.matcher(line).replaceAll(matchResult -> matchResult.group(1) + "\t-\t"))
                .map(line -> TABS.matcher(line).replaceAll(matchResult -> matchResult.group(1) + "\t" + matchResult.group(2)))
                .map(line -> TABS2.matcher(line).replaceAll(matchResult -> matchResult.group(1) + "\t" + matchResult.group(2)))
                .map(line -> TABS3.matcher(line).replaceAll(matchResult -> matchResult.group(1) + "\t" + matchResult.group(2)))
                .map(line -> TABS4.matcher(line).replaceAll(matchResult -> matchResult.group(1) + "\t" + matchResult.group(2)))
                .map(line -> TABS5.matcher(line).replaceAll(matchResult -> matchResult.group(1) + "\t" + matchResult.group(2)))
                .map(line -> TABS6.matcher(line).replaceAll(matchResult -> matchResult.group(1) + "\t" + matchResult.group(2)))
                .forEach(x -> {
                    String[] split = x.split("\t");
                    if (11 != split.length) {
                        System.err.println(split.length + " " + x);
                    }
                    writer.println(x);
                });
        }
    }
}
