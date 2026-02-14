package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.PriceExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFExtract {
    private final List<List<String>> lines;

    public PDFExtract(File pdfFile, int pageCount) throws IOException {
        lines = new ArrayList<>(pageCount);
        for (int i = 1; i <= pageCount; i++) {
            lines.add(PriceExtractor.getPage(pdfFile, i));
        }
    }

    public List<String> getPage(int pageIndex) {
        return lines.get(pageIndex - 1);
    }
}
