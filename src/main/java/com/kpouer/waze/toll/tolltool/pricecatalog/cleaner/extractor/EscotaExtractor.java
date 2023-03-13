package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.ASFEscotaExtractor.getHeaders;
import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.ASFEscotaExtractor.getPage;

public class EscotaExtractor implements Extractor {
    @Override
    public void extract(File pdfFile) throws IOException {
        var currentYear = new GregorianCalendar().get(Calendar.YEAR);
        var headers     = getHeaders("Escota_A8_A50_A52_A51_A57");
        var outputPath  = new File(pdfFile.getParentFile(), "out");
        outputPath.mkdirs();
        var tsvBuilder = new TriangleBuilder(outputPath);
        tsvBuilder.buildFile("Escota_A8_A50_A52_A51_A57", Category.Car, headers, getPage(pdfFile, 7), 1, List.of(10));
        tsvBuilder.buildFile("Escota_A8_A50_A52_A51_A57", Category.Motorcycle, headers, getPage(pdfFile, 11), 5, List.of(10));
    }
}
