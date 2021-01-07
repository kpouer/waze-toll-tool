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
package com.kpouer.waze.toll.tolltool;

import com.google.gson.Gson;
import com.kpouer.waze.toll.tolltool.waze.Section;
import com.kpouer.waze.toll.tolltool.waze.Sections;
import com.kpouer.waze.toll.tolltool.waze.Segment;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FixSections {
    public static void main(String[] args) throws FileNotFoundException {
        Sections sections = new Gson().fromJson(new FileReader(new File("data/sections.json")), Sections.class);
        for (Section section : sections) {
            if (null == section.getLocation() || 40 == section.getLocation()[0] && 0 == section.getLocation()[1]) {
                Segment  segment     = section.getSegments()[0];
                String   permalink   = segment.getPermalink();
                String   queryString = permalink.substring(permalink.indexOf('?'));
                String[] arguments   = StringUtils.split(queryString, "&");
                float    longitude   = 0;
                float    latitude    = 0;
                for (String argument : arguments) {
                    int lonIndex = argument.indexOf("lon=");
                    if (-1 < lonIndex) {
                        longitude = Float.parseFloat(argument.substring(lonIndex + 4));
                    } else {
                        int latIndex = argument.indexOf("lat=");
                        if (-1 < latIndex) {
                            latitude = Float.parseFloat(argument.substring(latIndex + 4));
                        }
                    }
                }
                section.setLocation(new float[]{longitude, latitude});
            }
        }
        System.out.println(new Gson().toJson(sections));
    }
}
