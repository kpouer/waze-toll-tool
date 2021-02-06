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
package com.kpouer.waze.toll.tolltool.waze;

import org.apache.commons.lang3.StringUtils;

public class Section {
    private String    section_id;
    private String    road_local_name;
    private String    section_local_name;
    private float[]   location;
    private Segment[] segments;

    public Section() {
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public void setRoad_local_name(String road_local_name) {
        this.road_local_name = road_local_name;
    }

    public String getSection_local_name() {
        return section_local_name;
    }

    public void setSection_local_name(String section_local_name) {
        this.section_local_name = section_local_name;
    }

    public void setSegments(Segment[] segments) {
        this.segments = segments;
    }

    public String getSection_id() {
        return section_id;
    }

    public String getRoad_local_name() {
        return road_local_name;
    }

    @Override
    public String toString() {
        return "Section{" +
            "section_id='" + section_id + '\'' +
            ", road_local_name='" + road_local_name + '\'' +
            ", section_local_name='" + section_local_name + '\'' +
            '}';
    }

    public float[] getLocation() {
        return location;
    }

    public void setLocation(float[] location) {
        this.location = location;
    }

    public Segment[] getSegments() {
        return segments;
    }

    public void fix() {
        if (location == null || (location.length != 2) || (location[0] == 0 && location[1] == 0)) {
            Segment  segment     = segments[0];
            String   permalink   = segment.getPermalink();
            String   queryString = permalink.substring(permalink.indexOf('?'));
            String[] arguments   = StringUtils.split(queryString, "&");
            float    longitude   = 0;
            float    latitude    = 0;
            for (String argument : arguments) {
                int lonIndex = argument.indexOf("lon=");
                if (lonIndex > -1) {
                    longitude = Float.parseFloat(argument.substring(lonIndex + 4));
                } else {
                    int latIndex = argument.indexOf("lat=");
                    if (latIndex > -1) {
                        latitude = Float.parseFloat(argument.substring(latIndex + 4));
                    }
                }
            }
            setLocation(new float[]{longitude, latitude});
        }
    }
}
