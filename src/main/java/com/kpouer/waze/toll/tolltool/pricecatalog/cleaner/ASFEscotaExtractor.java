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
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor.ASFExtractor;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor.EscotaExtractor;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor.Extractor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ASFEscotaExtractor {

    public static void main(String[] args) throws IOException {
        var currentYear = java.time.Year.now();
        var downloadFolder = Path.of("download", "2022");
        try (var filestream = Files.list(downloadFolder)) {
            filestream
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".pdf"))
                .filter(path -> path.getFileName().toString().contains("Escota") || path.getFileName().toString().startsWith("C1-TARIFS") || path.getFileName().toString().startsWith("C5-TARIFS"))
                .map(Path::toFile)
                .forEach(file -> {
                    try {
                        extractTollFile(file);
                    } catch (IOException e) {
                        log.error("Error while extracting toll file {}", file, e);
                    }
                });
        }
    }

    private static void extractTollFile(File pdfFile) throws IOException {
        String filename = pdfFile.getName();
        if (filename.contains("Escota")) {
            Extractor extractor = new EscotaExtractor();
            extractor.extract(pdfFile);
        } else if (filename.startsWith("C1-TARIFS")) {
            Extractor extractor = new ASFExtractor(Category.Car);
            extractor.extract(pdfFile);
        } else if (filename.startsWith("C5-TARIFS")) {
            Extractor extractor = new ASFExtractor(Category.Motorcycle);
            extractor.extract(pdfFile);
        }
    }

    @NotNull
    public static String[] getHeaders(String tollFileName) throws IOException {
        try (InputStream inputStream = ASFEscotaExtractor.class.getResourceAsStream("/samples/" + tollFileName + ".txt")) {
            return new String(inputStream.readAllBytes()).split("\n");
        }
    }

    private static void extractPage(File pdfFile, int page) throws IOException {
        List<String> lines       = getPage(pdfFile, page);
        File       outfilename = new File(pdfFile.getParentFile(), pdfFile.getName() + '-' + page + ".tsv");
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outfilename)))) {
            lines.forEach(writer::println);
        }
    }

    public static List<String> getPage(File pdfFile, int page) throws IOException {
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
