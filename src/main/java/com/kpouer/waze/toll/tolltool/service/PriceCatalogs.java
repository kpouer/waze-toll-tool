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
package com.kpouer.waze.toll.tolltool.service;

import com.kpouer.waze.toll.tolltool.pricecatalog.PriceCatalog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class PriceCatalogs {
    private final        ApplicationContext        applicationContext;
    private final        Map<String, PriceCatalog> priceCatalogs;

    public PriceCatalogs(ApplicationContext applicationContext) throws IOException {
        this.applicationContext = applicationContext;
        priceCatalogs           = new HashMap<>(2);
        load();
    }

    public void load() throws IOException {
        priceCatalogs.clear();
        try (var prices = Files.list(new File("prices").toPath())) {
            prices
                .parallel()
                .filter(path -> path.toFile().isDirectory())
                .forEach(this::loadCatalog);
        }
    }

    private void loadCatalog(Path path) {
        try {
            PriceCatalog priceCatalog = applicationContext.getBean(PriceCatalog.class);
            priceCatalog.load(path);
            priceCatalogs.put(path.toFile().getName().toLowerCase(), priceCatalog);
        } catch (IOException e) {
            log.error("Error while loading catalog {}", path, e);
        }
    }

    public Optional<PriceCatalog> getPriceCatalog(String country) {
        return Optional.of(priceCatalogs.get(country.toLowerCase()));
    }
}
