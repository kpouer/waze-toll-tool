package com.kpouer.waze.toll.tolltool.pricecatalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryPriceTest {

    private CategoryPrice categoryPrice;

    @BeforeEach
    void setUp() {
        categoryPrice = new CategoryPrice(1.0f, (short) 2022, "test");
    }

    @Test
    void merge() {
        var newPrices = new CategoryPrice(2.0f, (short) 2023, "testNew");
        assertEquals(1.0f, categoryPrice.getPrice());
        assertEquals(2022, categoryPrice.getYear());
        assertEquals("test", categoryPrice.getPath());
        categoryPrice.merge(newPrices);
        assertEquals(2.0f, categoryPrice.getPrice());
        assertEquals(2023, categoryPrice.getYear());
        assertEquals("testNew", categoryPrice.getPath());
    }
}