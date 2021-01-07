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

import java.util.*;
import java.util.function.Consumer;

public class Sections implements Iterable<Section> {
    private Section[]            sections;
    private Map<String, Section> sectionMap;

    public Section get(String sectionId) {
        if (null == sectionMap) {
            sectionMap = new HashMap<>(1000);
            Arrays.stream(sections).forEach(section -> sectionMap.put(section.getSection_id(), section));
        }
        Section section = sectionMap.get(sectionId);
        if (null == section) {
            throw new IllegalArgumentException("Section " + sectionId + " doesn't exist");
        }
        return section;
    }


    @Override
    public Iterator<Section> iterator() {
        return Arrays.stream(sections).iterator();
    }

    @Override
    public void forEach(Consumer<? super Section> action) {
        Arrays.stream(sections).forEach(action);
    }

    @Override
    public Spliterator<Section> spliterator() {
        return Arrays.stream(sections).spliterator();
    }
}
