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

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PDFExtractor {
    public static String extractText(File pdfFile) throws IOException {
        return extractText(pdfFile, 1, Integer.MAX_VALUE);
    }

    public static String extractText(File pdfFile, int startPage) throws IOException {
        return extractText(pdfFile, startPage, Integer.MAX_VALUE);
    }

    public static String extractText(File pdfFile, int startPage, int endPage) throws IOException {
        try (PDDocument pdDocument = Loader.loadPDF(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(startPage);
            pdfStripper.setEndPage(endPage);
            String text = pdfStripper.getText(pdDocument);
            pdDocument.close();
            return text;
        }
    }

    public static void main(String[] args) throws IOException {
        String file = args[0];

        String text;
        if (args.length > 1) {
            int page = Integer.parseInt(args[1]);
            text = extractText(new File(args[0]), page, page);
        } else {
            text = extractText(new File(args[0]));
        }
        Files.writeString(Path.of(args[0] + ".txt"), text);
        System.out.println(text);
    }
}
