package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.ASFEscotaExtractor.getHeaders;
import static com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.ASFEscotaExtractor.getPage;

public class ASFExtractor implements Extractor {
    private final Category category;

    public ASFExtractor(Category category) {

        this.category = category;
    }

    @Override
    public void extract(File pdfFile) throws IOException {
        int  currentYear            = new GregorianCalendar().get(Calendar.YEAR);
        File outputPath             = new File(pdfFile.getParentFile(), "out");
        File triangleOutputPath     = new File(outputPath, "triangle");
        File oneDirMatrixOutputPath = new File(outputPath, "onedirmatrix");
        triangleOutputPath.mkdirs();
        oneDirMatrixOutputPath.mkdirs();
        TSVBuilder triangleBuilder  = new TriangleBuilder(triangleOutputPath);
        TSVBuilder rectangleBuilder = new RectangleBuilder(oneDirMatrixOutputPath);
        triangleBuilder.buildFile("ASF_A7_A8_A9_A46_A54", category, getHeaders("ASF_A7_A8_A9_A46_A54"), getPage(pdfFile, 1), 0, List.of());
        rectangleBuilder.buildFile("ASF-A7-A9-A46-A54-A61-A62-A66-A75-page2", category, getHeaders("ASF-A7-A9-A46-A54-A61-A62-A66-A75-page2"), getPage(pdfFile, 2), 0, List.of());
        //strange but the PDF is different for category 5 and 1
        if (category == Category.Car) {
            triangleBuilder.buildFile("ASF-A9-A61-A62-A66-A75-A709-page3", category, getHeaders("ASF-A9-A61-A62-A66-A75-A709-page3"), getPage(pdfFile, 3), 0, List.of());
        } else {
            triangleBuilder.buildFile("ASF-A9-A61-A62-A66-A75-A709-page3", category, getHeaders("ASF-A9-A61-A62-A66-A75-A709-page3"), getPage(pdfFile, 3), 14, List.of());
        }
    }
}
