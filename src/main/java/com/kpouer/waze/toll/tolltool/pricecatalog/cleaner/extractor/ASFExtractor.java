package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder.RectangleBuilder;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder.TriangleBuilder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PriceExtractor.getHeaders;
import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PriceExtractor.getPage;

@RequiredArgsConstructor
public class ASFExtractor implements Extractor {
    private final Path pdf;
    private final Category category;

    @Override
    public void extract() throws IOException {
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
        var triangleBuilder  = new TriangleBuilder(triangleOutputPath);
        var rectangleBuilder = new RectangleBuilder(oneDirMatrixOutputPath);
        triangleBuilder.buildFile("ASF_A7_A8_A9_A46_A54", category, getHeaders("ASF_A7_A8_A9_A46_A54"), getPage(pdfFile, 1));
        rectangleBuilder.buildFile("ASF-A7-A9-A46-A54-A61-A62-A66-A75-page2", category, getHeaders("ASF-A7-A9-A46-A54-A61-A62-A66-A75-page2"), getPage(pdfFile, 2));
        //strange but the PDF is different for category 5 and 1
        if (category == Category.Car) {
            triangleBuilder.buildFile("ASF-A9-A61-A62-A66-A75-A709-page3", category, getHeaders("ASF-A9-A61-A62-A66-A75-A709-page3"), getPage(pdfFile, 3));
            triangleBuilder.buildFile("ASF-A89-CLERMONT-LYON-page8", category, getHeaders("ASF-A89-CLERMONT-LYON-page8"), getPage(pdfFile, 8), 34);
            rectangleBuilder.buildFile("ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9", category, getHeaders("ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9"), getPage(pdfFile, 9));
            triangleBuilder.buildFile("ASF-A89-MANZAT-ST GERMAIN LES VERGNES-page-8", category, getHeaders("ASF-A89-MANZAT-ST GERMAIN LES VERGNES-page-8"), getPage(pdfFile, 8), 55);
        } else {
            triangleBuilder.buildFile("ASF-A9-A61-A62-A66-A75-A709-page3", category, getHeaders("ASF-A9-A61-A62-A66-A75-A709-page3"), getPage(pdfFile, 3), 14, List.of());
            triangleBuilder.buildFile("ASF-A89-CLERMONT-LYON-page8", category, getHeaders("ASF-A89-CLERMONT-LYON-page8"), getPage(pdfFile, 8), 49);
            if (java.time.Year.now().getValue() == 2023) {
                rectangleBuilder.buildFile("ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9", category, getHeaders("ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9"), getPage(pdfFile, 9), 289, 25);
            } else {
                rectangleBuilder.buildFile("ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9", category, getHeaders("ASF-A89-A10-A19-A6-A77-A5-A26-A4-A31-A36-A39-A40-A42_page9"), getPage(pdfFile, 9), 290, 26);
            }
            triangleBuilder.buildFile("ASF-A89-MANZAT-ST GERMAIN LES VERGNES-page-8", category, getHeaders("ASF-A89-MANZAT-ST GERMAIN LES VERGNES-page-8"), getPage(pdfFile, 8), 143);
        }
        triangleBuilder.buildFile("ASF-A61-A62-A20-A64-A65-page4", category, getHeaders("ASF-A61-A62-A20-A64-A65-page4"), getPage(pdfFile, 4));
        triangleBuilder.buildFile("ASF-A10-A83-A837-A87-page-6", category, getHeaders("ASF-A10-A83-A837-A87-page-6"), getPage(pdfFile, 6));
        triangleBuilder.buildFile("ASF-A11-A28-A81-A85-page-7", category, getHeaders("ASF-A11-A28-A81-A85-page-7"), getPage(pdfFile, 7));
    }
}
