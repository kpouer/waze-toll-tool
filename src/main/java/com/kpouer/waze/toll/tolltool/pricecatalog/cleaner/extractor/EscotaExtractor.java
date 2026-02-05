package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder.TriangleBuilder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PriceExtractor.getHeaders;
import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PriceExtractor.getPage;

@RequiredArgsConstructor
public class EscotaExtractor implements Extractor {
    private final Path pdf;

    @Override
    public void extract() throws IOException {
        var headers = getHeaders("Escota_A8_A50_A52_A51_A57");
        var parent = pdf.getParent();
        var outputPath  = Path.of(parent.toString(), "out");
        var triangleOutputPath = Path.of(outputPath.toString(), "triangle");
        if (!Files.isDirectory(triangleOutputPath)) {
            Files.createDirectory(triangleOutputPath);
        }
        var currentYear = java.time.Year.now().getValue();
        var pdfFile = pdf.toFile();
        var tsvBuilder = new TriangleBuilder(triangleOutputPath);
        var pageCars = getPage(pdfFile, 7);
        var pageMotorcycle = getPage(pdfFile, 11);
        switch (currentYear) {
            case 2026 -> {
                tsvBuilder.buildFile("Escota_A8_A50_A52_A51_A57", Category.Car, headers, pageCars, 123, List.of(10));
                tsvBuilder.buildFile("Escota_A8_A50_A52_A51_A57", Category.Motorcycle, headers, pageMotorcycle, 120, List.of(10));
            }
            default -> {
                tsvBuilder.buildFile("Escota_A8_A50_A52_A51_A57", Category.Car, headers, pageCars, 0, List.of(10));
                tsvBuilder.buildFile("Escota_A8_A50_A52_A51_A57", Category.Motorcycle, headers, pageMotorcycle, 120, List.of(10));
            }
        }
    }
}
