/*
 * Copyright 2023 Matthieu Casanova
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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.official;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A parser for APRR price catalog kindly provided
 */
public class APRRParser {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: APRRParser <fichier gares> <fichier tarifs>");
            System.exit(1);
        }
        var gares = parseGares(args[0]);
        var file  = Path.of(args[1]);
        try (var output = Files.newBufferedWriter(Path.of(file.getFileName().toString().substring(0, 4) + "_APRR-1,2,3,4.tsv"), Charset.forName("WINDOWS-1252"))) {
            processTarifs(output, args[1], gares);
        }
    }

    private static void processTarifs(BufferedWriter output, String tarifsFile, Map<String, String> gares) throws IOException {
        List<Tarif> tarifs = Files.readAllLines(Path.of(tarifsFile), StandardCharsets.UTF_8)
                                  .stream()
                                  .map(line -> new Tarif(line.substring(0, 5),
                                                         line.substring(5, 10),
                                                         Double.parseDouble(line.substring(12, 16)) / 100.0,
                                                         Double.parseDouble(line.substring(36)) / 100.0
                                       )
                                  )
                                  .toList();
        tarifs.stream()
              .map(tarif -> tarif.entry)
              .filter(entry -> !gares.containsKey(entry))
              .distinct()
              .forEach(entry -> System.err.println("Gare inconnue : " + entry));
        tarifs.stream()
              .map(tarif -> gares.get(tarif.entry) + '\t' + gares.get(tarif.exit) + '\t' + tarif.cars + '\t' + tarif.motorcycle)
              .forEach(line -> {
                  try {
                      output.write(line);
                      output.newLine();
                  } catch (IOException e) {
                      throw new RuntimeException(e);
                  }
              });
    }

    private static Map<String, String> parseGares(String gareFile) throws IOException {
        return Files.readAllLines(Path.of(gareFile), Charset.forName("WINDOWS-1252"))
                    .stream()
                    .map(line -> new Gare(line.substring(0, 5), line.substring(5)))
                    .collect(Collectors.toMap(Gare::code, Gare::name));
    }

    private record Gare(String code, String name) {
    }

    private record Tarif(String entry, String exit, double cars, double motorcycle) {
    }
}
