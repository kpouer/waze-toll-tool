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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor.*;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PriceExtractor {
    public static void main(String[] args) throws IOException {
        var downloadFolder = Path.of("download", String.valueOf(LocalDate.now().getYear()));
        try (var filestream = Files.list(downloadFolder)) {
            filestream
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".pdf"))
                    .map(PriceExtractor::getExtractorForFile)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(extractor -> {
                        try {
                            extractor.extract();
                        } catch (IOException e) {
                            log.error("Error while extracting toll file {}", extractor, e);
                        }
                    });
        }
    }

    private static Optional<Extractor> getExtractorForFile(Path path) {
        var filename = path.getFileName().toString().toUpperCase();
        if (filename.contains("ESCOTA")) {
            return Optional.of(new EscotaExtractor(path));
        } else if (filename.startsWith("C1-TARIFS")) {
            return Optional.of(new ASFExtractor(path, Category.Car));
        } else if (filename.startsWith("C5-TARIFS")) {
            return Optional.of(new ASFExtractor(path, Category.Motorcycle));
        } else if (filename.startsWith("TARIF_AREA") || filename.startsWith("TARIFS_AREA") || filename.startsWith("TARIFS_INTERNES_AREA")) {
            return Optional.of(new AREAExtractor(path));
        } else if (filename.startsWith("TARIFS_APRR") || filename.startsWith("TARIFS_INTERNES_APRR")) {
            return Optional.of(new APRRExtractor(path));
        } else if (filename.toUpperCase().startsWith("TARIFS_") || filename.toUpperCase().startsWith("TARIFS ")) {
            return Optional.of(new ALIAEExtractor(path));
        } else if (filename.contains("SANEF")) {
            return Optional.of(new SanefExtractor(path));
        } else if (filename.contains("COFIROUTE")) {
            return Optional.of(new CofirouteCleaner(path));
        }
        return Optional.empty();
    }

    @NonNull
    public static String[] getHeaders(String tollFileName) throws IOException {
        try (var inputStream = PriceExtractor.class.getResourceAsStream("/samples/" + tollFileName + ".txt")) {
            return new String(inputStream.readAllBytes()).split("\n");
        }
    }

    public static List<String> getPage(File pdfFile, int page) throws IOException {
        var text = PDFExtractor.extractText(pdfFile, page, page);
        var lines = text
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
        var cleaner = new CleanerList();
        cleaner.load(PriceExtractor.class.getResourceAsStream("/cleaner.list"));
        return Arrays.stream(lines)
                     .map(cleaner::clean)
                     .toList();
    }
}
