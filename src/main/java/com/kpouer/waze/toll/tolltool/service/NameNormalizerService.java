/*
 * Copyright 2021 Matthieu Casanova
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Service
public class NameNormalizerService {
    private static final Logger              logger = LoggerFactory.getLogger(NameNormalizerService.class);
    private final        Map<String, String> alias;

    public NameNormalizerService() throws IOException {
        alias = new HashMap<>(1000);
        load();
    }

    public void load() throws IOException {
        alias.clear();
        logger.info("Load alias");
        File file = new File("prices/alias.csv");
        if (file.exists()) {
            Files.readAllLines(file.toPath())
                 .stream()
                 .map(StringUtils::stripAccents)
                 .map(String::toUpperCase)
                 .map(line -> StringUtils.split(line, ","))
                 .forEach(strings -> alias.put(strings[0], strings[1]));
        }
        logger.info("{} alias loaded", alias.size());
    }

    public String normalize(String key) {
        String normalized = StringUtils
            .stripAccents(key)
            .toUpperCase()
            .replace(" - ", " ")
            .replace('-', ' ');
        return alias.getOrDefault(normalized, normalized);
    }

    public String printKey(String key) {
        String normalized = normalize(key);
        if (normalized.equals(key)) {
            return key;
        }
        return key + " (" + normalized + ')';
    }
}
