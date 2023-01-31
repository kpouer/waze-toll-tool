package com.kpouer.waze.toll.tolltool.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class NameNormalizerServiceTest {

    private NameNormalizerService nameNormalizerService;

    @BeforeEach
    void setUp() throws IOException {
        nameNormalizerService = new NameNormalizerService();
    }

    @Test
    void printKey() {
        assertEquals("test (TEST)", nameNormalizerService.printKey("test"));
        assertEquals("TEST", nameNormalizerService.printKey("TEST"));
    }
}