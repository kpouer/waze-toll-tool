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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class SanefCleaner {
    private static final Pattern EURO    = Pattern.compile(" €");
    private static final Pattern MINUS   = Pattern.compile("(A\\d+) - ");
    private static final Pattern TABS    = Pattern.compile("(\\d) (\\d)");
    private static final Pattern TABS2   = Pattern.compile("(\\d) ([A-Z])");
    private static final Pattern TABS3   = Pattern.compile("([A-Z]) (A\\d)");
    private static final Pattern TABS4   = Pattern.compile("(\\)) (\\d)");
    private static final Pattern TABS5   = Pattern.compile("(\\)) (A\\d)");
    private static final Pattern TABS6   = Pattern.compile("([A-Z]) (\\d)");
    private static final Pattern SPACE   = Pattern.compile(" ");
    private static final Pattern COMPILE = Pattern.compile(" (\\d)");

    public static void main(String[] args) throws IOException {
        String filename = args[0];
        File   pdfFile  = new File(filename);
        String text     = PDFExtractor.extractText(pdfFile);
        text = text.replaceAll("\r", "");
        String[] lines       = text.split("\n");
        String   outfilename = filename + ".tsv";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outfilename)))) {
            AtomicInteger errors = new AtomicInteger();
            Arrays.stream(lines)
                  .filter(line -> !line.contains("VEHICULES"))
                  .filter(line -> !line.contains("tarifs"))
                  .filter(line -> !line.contains("véhicules"))
                  .filter(line -> !line.contains("Hauteur"))
                  .filter(line -> !line.contains("inférieur"))
                  .filter(line -> !line.contains("CLASSE"))
                  .filter(line -> !line.contains("égal"))
                  .map(line -> EURO.matcher(line).replaceAll(""))
                  .map(line -> SPACE.matcher(line).replaceAll("\t"))
                  .map(line -> line.replaceAll("\tN°", " N°"))
                  .map(line -> line.replaceAll("([A-Z])\t([A-Z])", "$1 $2"))
                  .map(line -> line.replaceAll("19\tA4", "19 A4"))
                  .map(line -> line.replaceAll("13\tA29", "13 A29"))
                  .map(line -> line.replaceAll("e\t", "e "))
                  .map(line -> line.replaceAll("33\tA", "33 A"))
                  .map(line -> line.replaceAll("\t\\(", " ("))
                  .map(line -> line.replaceAll("/\t", "/ "))
                  .map(line -> line.replaceAll("\t/", " /"))
                  .map(line -> line.replaceAll(" ST\t", " ST "))
                  .map(line -> line.replaceAll("\tST\t", "\tST "))
                  .map(line -> line.replaceAll(" STE\t", " STE "))
                  .map(line -> line.replaceAll("\tSTE\t", "\tSTE "))
                  .map(line -> line.replaceAll("SAINT\t", "SAINT "))
                  .map(line -> line.replaceAll("\tSUR\t", " SUR "))
                  .map(line -> line.replaceAll("\tLA\t", "\tLA "))
                  .map(line -> line.replaceAll(" LA\t", " LA "))
                  .map(line -> line.replaceAll("^LA\t", "LA "))
                  .map(line -> line.replaceAll("\tDE\t", " DE "))
                  .map(line -> line.replaceAll("\tEN\t", " EN "))
                  .map(line -> line.replaceAll("\tNORD", " NORD"))
                  .map(line -> line.replaceAll("\tSUD", " SUD"))
                  .map(line -> line.replaceAll("\tOUEST", " OUEST"))
                  .map(line -> line.replaceAll("\tEST", " EST"))
                  .map(line -> line.replaceAll("L\tISLE", "L ISLE"))
                  .map(line -> line.replaceAll("DOLE LA", "DOLE\tLA"))
                  .map(line -> line.replaceAll("\tS/", " S/"))
                  .map(line -> line.replaceAll("S/\t", "S/ "))
                  .map(line -> line.replaceAll("GONDREVILLE\tA77", "GONDREVILLE A77"))
                  .map(line -> line.replaceAll("VOIE\tSACREE", "VOIE SACREE"))
                  .map(line -> line.replaceAll("VULCANIA\tBROMONT", "VULCANIA BROMONT"))
                  .map(line -> line.replaceAll("ST ETIENNE\tAU\tTEMPLE", "ST ETIENNE AU TEMPLE"))
                  .map(line -> line.replaceAll("TIL\tCHATEL", "TIL CHATEL"))
                  .map(line -> line.replaceAll("ST JULIEN\tSANCY", "ST JULIEN SANCY"))
                  .map(line -> line.replaceAll("\tD\t", " D "))
                  .map(line -> line.replaceAll("\tSOUS\t", " SOUS "))
                  .map(line -> line.replaceAll("\tAUX\t", " AUX "))
                  .map(line -> line.replaceAll("\tLES\t", "\tLES "))
                  .map(line -> line.replaceAll("MACON\tCENTRE", "MACON CENTRE"))
                  .map(line -> line.replaceAll("CRIMOLOIS ", "CRIMOLOIS\t"))
                  .map(line -> line.replaceAll("LE\tMIROIR", "LE MIROIR"))
                  .map(line -> line.replaceAll("DIJON SUD ", "DIJON SUD\t"))
                  .map(line -> line.replaceAll("CHALONS\t", "CHALONS "))
                  .map(line -> line.replaceAll("\t-\t", " - "))
                  .map(line -> line.replaceAll("AMBOISE\tCH.RENAULT", "AMBOISE CH.RENAULT"))
                  .map(line -> line.replaceAll("MONT\tCHOISY", "MONT CHOISY"))
                  .map(line -> line.replaceAll("MONT\tCHOISY", "MONT CHOISY"))
                  .map(line -> line.replaceAll("ST GERMAIN\tLES VERGNE", "ST GERMAIN LES VERGNE"))
                  .map(line -> line.replaceAll("Système\tOuvert", "Système Ouvert"))
                  .map(line -> line.replaceAll("STE\tMENEHOULD", "STE MENEHOULD"))
                  .map(line -> line.replaceAll("ST\tROMAIN SUR CHER", "ST ROMAIN SUR CHER"))
                  .map(line -> line.replaceAll("ST\tMARTIN", "ST MARTIN"))
                  .map(line -> line.replaceAll("ST\tJULIEN\tSANCY", "ST JULIEN SANCY"))
                  .map(line -> line.replaceAll("ST\tHILAIRE", "ST HILAIRE"))
                  .map(line -> line.replaceAll("ST\tGIBRIEN", "ST GIBRIEN"))
                  .map(line -> line.replaceAll("^ST\t", "ST "))
                  .map(line -> line.replaceAll("VIERZON-EST ", "VIERZON-EST\t"))
                  .map(line -> line.replaceAll("USSEL OUEST ", "USSEL OUEST\t"))
                  .map(line -> line.replaceAll("USSEL EST ", "USSEL EST\t"))
                  .map(line -> line.replaceAll("BESANCON OUEST ", "BESANCON OUEST\t"))
                  .map(line -> line.replaceAll("BEYNOST ", "BEYNOST\t"))
                  .map(line -> line.replaceAll("TULLE EST ", "TULLE EST\t"))
                  .map(line -> line.replaceAll("LES\tEPRUNES", "LES EPRUNES"))
                  .map(line -> line.replaceAll("LA\tCOTIERE ", "LA COTIERE\t"))
                  .map(line -> line.replaceAll("LA\tBOISSE ", "LA BOISSE\t"))
                  .map(line -> line.replaceAll("GERMAIN\tLES VERGNE", "GERMAIN LES VERGNE"))
                  .map(line -> line.replaceAll("ETIENNE\tAU\tTEMPLE", "ETIENNE AU TEMPLE"))
                  .map(line -> line.replaceAll("LE\tTOURNEAU", "LE TOURNEAU"))
                  .map(line -> line.replaceAll("QUINCIEUX\tBARRIERE\tSystème Ouvert", "QUINCIEUX BARRIERE\tSystème Ouvert"))
                  .map(line -> line.replaceAll("REIMS NORD (péage d'Ormes)", "REIMS NORD (ORMES)"))
                  .map(line -> line.replaceAll("REIMS OUEST (péage de Thillois)", "REIMS NORD (THILLOIS)"))
                  .map(line -> COMPILE.matcher(line).replaceAll(matchResult -> "\t" + matchResult.group(1)))
                  .forEach(x -> {
                      String[] split = x.split("\t");
                      if (split.length != 8) {
                          errors.incrementAndGet();
                          System.err.println(split.length + " " + x);
                      }
                      writer.println(x);
                  });
            System.out.println(errors);
        }
    }
}
