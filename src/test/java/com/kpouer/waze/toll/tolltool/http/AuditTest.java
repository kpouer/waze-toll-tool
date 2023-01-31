package com.kpouer.waze.toll.tolltool.http;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditTest {

    private Audit audit;

    @BeforeEach
    void setUp() {
        audit = new Audit();
    }

    @Test
    void incrementObsoletePrice() {
        audit.incrementObsoletePrice();
        assertEquals(1, audit.getObsoletePrices());
    }

    @Test
    void increment() {
        audit.increment(Category.Car);
        assertEquals(1, audit.getFailCars());
        assertEquals(0, audit.getFailMotorcycles());
        audit.increment(Category.Motorcycle);
        assertEquals(1, audit.getFailCars());
        assertEquals(1, audit.getFailMotorcycles());
    }

    @Test
    void getAudit() {
        assertEquals("""
                         Missing car prices         : 0
                         Missing motorcycles prices : 0
                         Obsolete prices : 0
                         """, audit.getAudit());
    }
}