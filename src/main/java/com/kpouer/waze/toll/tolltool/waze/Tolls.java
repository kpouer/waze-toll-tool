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

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Tolls implements Iterable<Toll> {
    private Toll[] tolls;

    public Tolls() {
    }

    public Tolls(Toll[] tolls) {
        this.tolls = tolls;
    }

    public Toll[] getTolls() {
        return tolls;
    }

    public void setTolls(Toll[] tolls) {
        this.tolls = tolls;
    }

    @Override
    public Iterator<Toll> iterator() {
        return Arrays.stream(tolls).iterator();
    }

    @Override
    public void forEach(Consumer<? super Toll> action) {
        Arrays.stream(tolls).forEach(action);
    }

    @Override
    public Spliterator<Toll> spliterator() {
        return Arrays.spliterator(tolls);
    }
}
