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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class ASFCleaner {

    public static void main(String[] args) throws IOException {
        String filename = args[0];
        int page = Integer.parseInt(args[1]);
        File   pdfFile  = new File(filename);
        String text     = PDFExtractor.extractText(pdfFile, page, page);
        Files.writeString(Path.of("1.txt"),text);
        String[]    lines       = text
            .replaceAll("\r", "")
            .replaceAll("\n?-\n?", "-")
            .replaceAll("([a-zéèàA-Z])\n([a-z])", "$1$2")
            .replaceAll("’\n", "'")
            .replaceAll(" \\. ", " 0 ")
            .replaceAll(" {2,}", " ")
            .replaceAll("^ +", "")
            .replaceAll(" +$", "")
            .replaceAll(" ", "\t")
            .split("\n");
        String      outfilename = filename + ".tsv";
        CleanerList cleaner     = new CleanerList();
        cleaner.load(CofirouteCleaner.class.getResourceAsStream("/cleaner.list"));
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outfilename)))) {
            Arrays.stream(lines)
                  .map(line -> line.replaceAll(" {4}. {2}", "\t0\t"))
                  .map(line -> line.replaceAll(" ", "\t"))
                  .map(cleaner::clean)
                  .forEach(writer::println);
        }
    }
}
