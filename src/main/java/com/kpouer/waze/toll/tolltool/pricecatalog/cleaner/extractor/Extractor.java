package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.extractor;

import java.io.IOException;

@FunctionalInterface
public interface Extractor {
    void extract() throws IOException;
}
