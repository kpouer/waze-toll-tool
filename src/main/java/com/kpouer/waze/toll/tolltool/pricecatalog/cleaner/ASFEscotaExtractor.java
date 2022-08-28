/*
 * Copyright 2021-2022 Matthieu Casanova
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

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ASFEscotaExtractor {

    public static void main(String[] args) throws IOException {
        String filename = args[0];
        File   pdfFile  = new File(filename);
        if (args.length > 1) {
            int page = Integer.parseInt(args[1]);
            extractPage(pdfFile, page);
        } else {
            extractTollFile(pdfFile);
        }
    }

    private static void extractTollFile(File pdfFile) throws IOException {
        if (pdfFile.getName().contains("Escota")) {
            extractEscota(pdfFile);
        }
    }

    private static void extractEscota(File pdfFile) throws IOException {
        int          currentYear = new GregorianCalendar().get(Calendar.YEAR);
        String[]     headers     = getHeaders("Escota_A8_A50_A52_A51_A57");
        TSVBuilder tsvBuilder = new TriangleBuilder(pdfFile.getParentFile());
        tsvBuilder.buildFile("Escota_A8_A50_A52_A51_A57", Category.Car, headers, getPage(pdfFile, 7), 1, List.of(10));
        tsvBuilder.buildFile("Escota_A8_A50_A52_A51_A57", Category.Motorcycle, headers, getPage(pdfFile, 11), 5, List.of(10));
    }

    @NotNull
    private static String[] getHeaders(String tollFileName) throws IOException {
        try (InputStream inputStream = ASFEscotaExtractor.class.getResourceAsStream("/samples/" + tollFileName + ".txt")) {
            return new String(inputStream.readAllBytes()).split("\n");
        }
    }

    private static void extractPage(File pdfFile, int page) throws IOException {
        List<String> lines       = getPage(pdfFile, page);
        String       outfilename = pdfFile.getName() + '-' + page + ".tsv";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outfilename)))) {
            lines.forEach(writer::println);
        }
    }

    private static List<String> getPage(File pdfFile, int page) throws IOException {
        String text = PDFExtractor.extractText(pdfFile, page, page);
        String[] lines = text
            .replaceAll("\r", "")
            .replaceAll("\n?-\n?", "-")
            .replaceAll("([a-zéèàA-Z])\n([a-z])", "$1$2")
            .replaceAll("’\n", "'")
            .replaceAll(" \\. ", " 0 ")
            .replaceAll("-(\\d)", "-\n$1")
            .replaceAll("(\\d)-", "$1\n-")
            .replaceAll("-+", "0 ")
            .replaceAll(" {2,}", " ")
            .replaceAll("^ +", "")
            .replaceAll(" +$", "")
            .replaceAll(" ", "\t")
            .replaceAll("^\\s+", "")
            .split("\n");
        CleanerList cleaner = new CleanerList();
        cleaner.load(ASFEscotaExtractor.class.getResourceAsStream("/cleaner.list"));
        return Arrays.stream(lines)
                     .map(cleaner::clean)
                     .toList();
    }
}
