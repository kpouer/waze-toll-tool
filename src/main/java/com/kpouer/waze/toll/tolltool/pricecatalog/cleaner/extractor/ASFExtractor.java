package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder.RectangleBuilder;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder.TriangleBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PriceExtractor.getHeaders;

@RequiredArgsConstructor
@Slf4j
public class ASFExtractor implements Extractor {
    private final Path pdf;
    private final Category category;

    @Override
    public void extract() throws IOException {
        log.info("Extracting {}", pdf);
        var pdfFile = pdf.toFile();
        var directory = pdf.getParent();
        var outputPath = Path.of(directory.toString(), "out");
        var triangleOutputPath = Path.of(outputPath.toString(), "triangle");
        var oneDirMatrixOutputPath = Path.of(outputPath.toString(), "onedirmatrix");
        if (!Files.exists(triangleOutputPath)) {
            Files.createDirectories(triangleOutputPath);
        }
        if (!Files.exists(oneDirMatrixOutputPath)) {
            Files.createDirectories(oneDirMatrixOutputPath);
        }
        var triangleBuilder = new TriangleBuilder(triangleOutputPath);
        var rectangleBuilder = new RectangleBuilder(oneDirMatrixOutputPath);
        //strange but the PDF is different for category 5 and 1
        var currentYear = java.time.Year.now().getValue();
        var pdfExtract = new PDFExtract(pdfFile, 9);
        if (category == Category.Car) {
            extractCars(currentYear, rectangleBuilder, triangleBuilder, pdfExtract);
        } else {
            extractMotorcycles(currentYear, rectangleBuilder, triangleBuilder, pdfExtract);
        }
    }

    private void extractCars(int currentYear,
                             RectangleBuilder rectangleBuilder,
                             TriangleBuilder triangleBuilder,
                             PDFExtract pdfExtract) throws IOException {
        {
            var name = "ASF_A7_A8_A9_A46_A54";
            var headers = getHeaders(name);
            var page1 = pdfExtract.getPage(1);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page1, 1);
                default -> triangleBuilder.buildFile(name, category, headers, page1);
            }
        }
        {
            var name = "ASF-A7-A9-A46-A54-A61-A62-A66-A75-page2";
            var headers = getHeaders(name);
            var page2 = pdfExtract.getPage(2);
            switch (currentYear) {
                case 2026 -> rectangleBuilder.buildFile(name, category, headers, page2, 1);
                default -> rectangleBuilder.buildFile(name, category, headers, page2);
            }
        }
        {
            var name = "ASF-A9-A61-A62-A66-A75-A709-page3";
            var headers = getHeaders(name);
            var page3 = pdfExtract.getPage(3);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page3, 2);
                default -> triangleBuilder.buildFile(name, category, headers, page3);
            }
        }
        {
            var name = "ASF-A61-A62-A20-A64-A65-page4";
            var headers = getHeaders(name);
            var page4 = pdfExtract.getPage(4);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page4, 158);
                default -> triangleBuilder.buildFile(name, category, headers, page4);
            }
        }
        {
            var name = "ASF-A10-A83-A837-A87-page-6";
            var headers = getHeaders(name);
            var page6 = pdfExtract.getPage(6);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page6, 187);
                default -> triangleBuilder.buildFile(name, category, headers, page6);
            }
        }
        {
            var name = "ASF-A11-A28-A81-A85-page-7";
            var headers = getHeaders(name);
            var page7 = pdfExtract.getPage(7);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page7, 124);
                default -> triangleBuilder.buildFile(name, category, headers, page7);
            }
        }
        {
            var name = "ASF-A89-CLERMONT-LYON-page8";
            var headers = getHeaders(name);
            var page8 = pdfExtract.getPage(8);
            switch (currentYear) {
                case 2025 -> triangleBuilder.buildFile(name, category, headers, page8, 0);
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page8, 131);
                default -> triangleBuilder.buildFile(name, category, headers, page8, 34);
            }
        }
        {
            var name = "ASF-A89-MANZAT-ST GERMAIN LES VERGNES-page-8";
            var headers = getHeaders(name);
            var page8 = pdfExtract.getPage(8);
            switch (currentYear) {
                case 2025 -> triangleBuilder.buildFile(name, category, headers, page8, 22);
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page8, 153);
                default -> triangleBuilder.buildFile(name, category, headers, page8, 56);
            }
        }
        {
            var name = "ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9";
            var headers = getHeaders(name);
            var page9 = pdfExtract.getPage(9);
            switch (currentYear) {
                case 2026 -> rectangleBuilder.buildFile(name, category, headers, page9, 292, 19);
                default -> rectangleBuilder.buildFile(name, category, headers, page9);
            }
        }
    }

    private void extractMotorcycles(int currentYear,
                                    RectangleBuilder rectangleBuilder,
                                    TriangleBuilder triangleBuilder,
                                    PDFExtract pdfExtract) throws IOException {
        {
            var name = "ASF_A7_A8_A9_A46_A54";
            var headers = getHeaders(name);
            var page1 = pdfExtract.getPage(1);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page1, 1);
                default -> triangleBuilder.buildFile(name, category, headers, page1);
            }
        }
        {
            var name = "ASF-A7-A9-A46-A54-A61-A62-A66-A75-page2";
            var headers = getHeaders(name);
            var page2 = pdfExtract.getPage(2);
            switch (currentYear) {
                case 2026 -> rectangleBuilder.buildFile(name, category, headers, page2, 1);
                default -> rectangleBuilder.buildFile(name, category, headers, page2);
            }
        }
        {
            var name = "ASF-A9-A61-A62-A66-A75-A709-page3";
            var headers = getHeaders(name);
            var page3 = pdfExtract.getPage(3);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page3, 16);
                default -> triangleBuilder.buildFile(name, category, headers, page3);
            }
        }
        {
            var name = "ASF-A61-A62-A20-A64-A65-page4";
            var headers = getHeaders(name);
            var page4 = pdfExtract.getPage(4);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page4, 167);
                default -> triangleBuilder.buildFile(name, category, headers, page4);
            }
        }
        {
            var name = "ASF-A10-A83-A837-A87-page-6";
            var headers = getHeaders(name);
            var page6 = pdfExtract.getPage(6);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page6, 200);
                default -> triangleBuilder.buildFile(name, category, headers, page6);
            }
        }
        {
            var name = "ASF-A11-A28-A81-A85-page-7";
            var headers = getHeaders(name);
            var page7 = pdfExtract.getPage(7);
            switch (currentYear) {
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page7, 134);
                default -> triangleBuilder.buildFile(name, category, headers, page7);
            }
        }
        {
            var name = "ASF-A89-CLERMONT-LYON-page8";
            var headers = getHeaders(name);
            var page8 = pdfExtract.getPage(8);
            switch (currentYear) {
                case 2025 -> triangleBuilder.buildFile(name, category, headers, page8, 15);
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page8, 90);
                default -> triangleBuilder.buildFile(name, category, headers, page8, 49);
            }
        }
        {
            var name = "ASF-A89-MANZAT-ST GERMAIN LES VERGNES-page-8";
            var headers = getHeaders(name);
            var page8 = pdfExtract.getPage(8);
            switch (currentYear) {
                case 2025 -> triangleBuilder.buildFile(name, category, headers, page8, 15);
                case 2026 -> triangleBuilder.buildFile(name, category, headers, page8, 112);
                default -> triangleBuilder.buildFile(name, category, headers, page8, 49);
            }
        }
        {
            var name = "ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9";
            var headers = getHeaders(name);
            var text = pdfExtract.getPage(9);
            switch (currentYear) {
                case 2023 -> rectangleBuilder.buildFile(name, category, headers, text, 289, 25);
                case 2025 -> rectangleBuilder.buildFile(name, category, headers, text, 32, 25);
                case 2026 -> rectangleBuilder.buildFile(name, category, headers, text, 3, 0);
                default -> rectangleBuilder.buildFile(name, category, headers, text, 290, 26);
            }
        }
    }
}
