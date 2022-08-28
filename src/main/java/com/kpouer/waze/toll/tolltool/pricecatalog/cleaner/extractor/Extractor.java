package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import java.io.File;
import java.io.IOException;

public interface Extractor {
    void extract(File pdfFile) throws IOException;
}
