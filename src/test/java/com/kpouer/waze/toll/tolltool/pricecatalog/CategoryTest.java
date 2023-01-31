package com.kpouer.waze.toll.tolltool.pricecatalog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {
    @Test
    void getVehicles() {
        assertArrayEquals(new String[] {"MOTORCYCLE"}, Category.Motorcycle.getVehicles());
    }

    @Test
    void fromString() {
        assertSame(Category.Car, Category.fromString("Car"));
    }
}