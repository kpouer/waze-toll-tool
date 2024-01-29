/*
 * Copyright 2021-2023 Matthieu Casanova
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.kpouer.waze.toll.tolltool.http;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import com.kpouer.waze.toll.tolltool.pricecatalog.PriceItem;
import com.kpouer.waze.toll.tolltool.service.NameNormalizerService;
import com.kpouer.waze.toll.tolltool.service.PriceCatalogs;
import com.kpouer.waze.toll.tolltool.waze.Toll;
import com.kpouer.waze.toll.tolltool.waze.Tolls;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RestController
@Slf4j
@AllArgsConstructor
public class TollRestController {
    private final PriceCatalogs         priceCatalogs;
    private final NameNormalizerService nameNormalizerService;

    @GetMapping("reload")
    public String getPrices() throws IOException {
        nameNormalizerService.load();
        priceCatalogs.load();
        return "Reloaded";
    }

    @GetMapping(value = "getEntries/{country}", produces = "text/plain")
    public String getEntries(@PathVariable String country) {
        return dumpTolls(country, PriceItem::getEntry);
    }

    @GetMapping(value = "getExits/{country}", produces = "text/plain")
    public String getExits(@PathVariable String country) {
        return dumpTolls(country, PriceItem::getExit);
    }

    private String dumpTolls(@PathVariable String country, Function<PriceItem, String> getter) {
        long         start        = System.currentTimeMillis();
        var priceCatalogOptional = priceCatalogs.getPriceCatalog(country);
        if (priceCatalogOptional.isEmpty()) {
            return "Unsupported country " + country;
        }
        var priceCatalog = priceCatalogOptional.get();
        String[] tolls = priceCatalog
            .getPrices()
            .keySet()
            .stream()
            .map(getter)
            .map(nameNormalizerService::printKey)
            .distinct()
            .toArray(String[]::new);
        long end = System.currentTimeMillis();
        log.info("Dumping entries/exits of {} : {} items in {}ms", country, tolls.length, end - start);
        Arrays.sort(tolls);
        return String.join("\n", tolls);
    }

    @GetMapping(value = "getPrices/{country}", produces = "text/plain")
    public String getPrices(@PathVariable String country) {
        long         start        = System.currentTimeMillis();
        var priceCatalogOptional = priceCatalogs.getPriceCatalog(country);
        if (priceCatalogOptional.isEmpty()) {
            return "Unsupported country " + country;
        }
        var priceCatalog = priceCatalogOptional.get();
        PriceItem[] priceItems = priceCatalog
            .getPrices()
            .keySet()
            .toArray(PriceItem[]::new);
        long end = System.currentTimeMillis();
        log.info("Dumping prices of {} : {} items in {}ms", country, priceItems.length, end - start);
        return dumpPriceItems(priceItems);
    }

    @GetMapping(value = "getPrices/{country}/from/{entry}", produces = "text/plain")
    public String getPricesEntry(@PathVariable String country, @PathVariable String entry) {
        long         start        = System.currentTimeMillis();
        var priceCatalogOptional = priceCatalogs.getPriceCatalog(country);
        if (priceCatalogOptional.isEmpty()) {
            return "Unsupported country " + country;
        }
        var priceCatalog = priceCatalogOptional.get();
        PriceItem[] priceItems = priceCatalog
            .getPrices()
            .keySet()
            .stream()
            .filter(priceItem -> priceItem.getEntry().contains(entry))
            .toArray(PriceItem[]::new);
        long end = System.currentTimeMillis();
        log.info("Dumping prices of {}/{} : {} items in {}ms", country, entry, priceItems.length, end - start);
        return dumpPriceItems(priceItems);
    }

    @GetMapping(value = "getPrices/{country}/to/{exit}", produces = "text/plain")
    public String getPricesExit(@PathVariable String country, @PathVariable String exit) {
        long         start        = System.currentTimeMillis();
        var priceCatalogOptional = priceCatalogs.getPriceCatalog(country);
        if (priceCatalogOptional.isEmpty()) {
            return "Unsupported country " + country;
        }
        var priceCatalog = priceCatalogOptional.get();
        PriceItem[] priceItems = priceCatalog
            .getPrices()
            .keySet()
            .stream()
            .filter(priceItem -> priceItem.getExit().equals(exit))
            .toArray(PriceItem[]::new);
        long end = System.currentTimeMillis();
        log.info("Dumping prices of {}/?/{} : {} items in {}ms", country, exit, priceItems.length, end - start);
        return dumpPriceItems(priceItems);
    }

    private String dumpPriceItems(PriceItem[] priceItems) {
        Arrays.sort(priceItems);
        var builder = new StringBuilder(2000000);
        Arrays.stream(priceItems).forEach(priceItem -> builder.append(nameNormalizerService.printKey(priceItem.getEntry())).append('\t')
                .append(nameNormalizerService.printKey(priceItem.getExit()))
                .append("\tCar : ").append(priceItem.getCarPrice()).append(" (").append(priceItem.getCarYear()).append(") ").append(priceItem.getCarPath())
                .append("\t Moto: ").append(priceItem.getMotorcyclePrice()).append(" (").append(priceItem.getMotorcycleYear()).append(')')
                .append('\n'));
        return builder.toString();
    }

    @GetMapping(value = "getPrice/{country}/{entry}/{exit}/{category}", produces = "text/plain")
    public float getPrice(@PathVariable String country, @PathVariable String entry, @PathVariable String exit, @PathVariable String category) {
        return priceCatalogs.getPriceCatalog(country).orElseThrow()
                            .getPrice(entry, exit)
                            .priceItemForward()
                            .getPrice(Category.fromString(category));
    }

    @PostMapping(value = "updateMatrix/{country}", produces = "text/plain")
    public ResponseEntity<TollResponse> updateMatrixGrids(@RequestBody Tolls tolls, @PathVariable String country) {
        tolls.forEach(Toll::removePriceMatrices);
        var priceCatalogOptional = priceCatalogs.getPriceCatalog(country);
        if (priceCatalogOptional.isEmpty()) {
            throw new IllegalArgumentException("Unsupported country " + country);
        }
        var priceCatalog = priceCatalogOptional.get();
        Audit                  audit                = new Audit();
        for (Toll toll : tolls) {
            toll.fixSections();
            if (toll.hasEntryExitRule()) {
                toll.buildMatrix(nameNormalizerService, audit, priceCatalog, Category.Motorcycle);
                audit.newLine();
                audit.newLine();
                toll.buildMatrix(nameNormalizerService, audit, priceCatalog, Category.Car);
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, "application/json; charset=utf-8");
        return ResponseEntity.ok().headers(headers).body(new TollResponse(tolls, audit.getAudit()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleException(Throwable e) {
        log.error("Unexpected exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExceptionUtils.getStackTrace(e));
    }
}
