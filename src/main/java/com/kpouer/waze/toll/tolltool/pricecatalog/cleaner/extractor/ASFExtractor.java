package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder.RectangleBuilder;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder.TriangleBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PriceExtractor.getHeaders;
import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PriceExtractor.getPage;

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
        triangleBuilder.buildFile("ASF_A7_A8_A9_A46_A54", category, getHeaders("ASF_A7_A8_A9_A46_A54"), getPage(pdfFile, 1));
        rectangleBuilder.buildFile("ASF-A7-A9-A46-A54-A61-A62-A66-A75-page2", category, getHeaders("ASF-A7-A9-A46-A54-A61-A62-A66-A75-page2"), getPage(pdfFile, 2));
        //strange but the PDF is different for category 5 and 1
        var currentYear = java.time.Year.now().getValue();
        var page3 = getPage(pdfFile, 3);
        var page8 = getPage(pdfFile, 8);
        if (category == Category.Car) {
            {
                var name = "ASF-A9-A61-A62-A66-A75-A709-page3";
                var headers = getHeaders(name);
                switch (currentYear) {
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page3, 2);
                    default -> triangleBuilder.buildFile(name, category, headers, page3);
                }
            }
            {
                var name = "ASF-A61-A62-A20-A64-A65-page4";
                var headers = getHeaders(name);
                var page4 = getPage(pdfFile, 4);
                switch (currentYear) {
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page4, 158);
                    default -> triangleBuilder.buildFile(name, category, headers, page4);
                }
            }
            {
                var name = "ASF-A10-A83-A837-A87-page-6";
                var headers = getHeaders(name);
                var page6 = getPage(pdfFile, 6);
                switch (currentYear) {
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page6, 187);
                    default -> triangleBuilder.buildFile(name, category, headers, page6);
                }
            }
            {
                var name = "ASF-A11-A28-A81-A85-page-7";
                var headers = getHeaders(name);
                var page7 = getPage(pdfFile, 7);
                switch (currentYear) {
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page7, 124);
                    default -> triangleBuilder.buildFile(name, category, headers, page7);
                }
            }
            {
                var name = "ASF-A89-CLERMONT-LYON-page8";
                var headers = getHeaders(name);
                switch (currentYear) {
                    case 2025 -> triangleBuilder.buildFile(name, category, headers, page8, 0);
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page8, 131);
                    default -> triangleBuilder.buildFile(name, category, headers, page8, 34);
                }
            }
            {
                var name = "ASF-A89-MANZAT-ST GERMAIN LES VERGNES-page-8";
                var headers = getHeaders(name);
                switch (currentYear) {
                    case 2025 -> triangleBuilder.buildFile(name, category, headers, page8, 22);
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page8, 153);
                    default -> triangleBuilder.buildFile(name, category, headers, page8, 56);
                }
            }
            {
                var name = "ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9";
                var headers = getHeaders(name);
                var page9 = getPage(pdfFile, 9);
                switch (currentYear) {
                    case 2026 -> rectangleBuilder.buildFile(name, category, headers, page9, 292, 19);
                    default -> rectangleBuilder.buildFile(name, category, headers, page9);
                }
            }
        } else {
            {
                var name = "ASF-A9-A61-A62-A66-A75-A709-page3";
                var headers = getHeaders(name);
                switch (currentYear) {
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page3, 16);
                    default -> triangleBuilder.buildFile(name, category, headers, page3);
                }
            }
            {
                var name = "ASF-A10-A83-A837-A87-page-6";
                var headers = getHeaders(name);
                var page6 = getPage(pdfFile, 6);
                switch (currentYear) {
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page6, 200);
                    default -> triangleBuilder.buildFile(name, category, headers, page6);
                }
            }
            {
                var name = "ASF-A11-A28-A81-A85-page-7";
                var headers = getHeaders(name);
                var page7 = getPage(pdfFile, 7);
                switch (currentYear) {
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page7, 134);
                    default -> triangleBuilder.buildFile(name, category, headers, page7);
                }
            }
            {
                var name = "ASF-A89-CLERMONT-LYON-page8";
                var headers = getHeaders(name);
                switch (currentYear) {
                    case 2025 -> triangleBuilder.buildFile(name, category, headers, page8, 15);
                    default -> triangleBuilder.buildFile(name, category, headers, page8, 49);
                }
            }
            {
                var name = "ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9";
                var headers = getHeaders(name);
                var text = getPage(pdfFile, 9);
                switch (currentYear) {
                    case 2023 -> rectangleBuilder.buildFile(name, category, headers, text, 289, 25);
                    case 2025 -> rectangleBuilder.buildFile(name, category, headers, text, 32, 25);
                    case 2026 -> rectangleBuilder.buildFile(name, category, headers, text, 3, 0);
                    default -> rectangleBuilder.buildFile(name, category, headers, text, 290, 26);
                }
            }
            {
                var name = "ASF-A61-A62-A20-A64-A65-page4";
                var headers = getHeaders(name);
                var page4 = getPage(pdfFile, 4);
                switch (currentYear) {
                    case 2026 -> triangleBuilder.buildFile(name, category, headers, page4, 167);
                    default -> triangleBuilder.buildFile(name, category, headers, page4);
                }
            }
            extractASF_A89_MANZAT_ST_GERMAIN_LES_VERGNES_page_8(pdfFile, triangleBuilder, page8);
        }
    }

    private void extractASF_A89_MANZAT_ST_GERMAIN_LES_VERGNES_page_8(File pdfFile, TriangleBuilder triangleBuilder, List<String> page8) throws IOException {
        var name = "ASF-A89-MANZAT-ST GERMAIN LES VERGNES-page-8";
        var headers = getHeaders(name);
        var index = page8.indexOf("Manzat") + 2;
        triangleBuilder.buildFile(name, category, headers, page8, index);
    }
}
