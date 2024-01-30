/*
 * Copyright 2022-2023 Matthieu Casanova
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
package com.kpouer.waze.toll.tolltool.pricecatalog.cleaner.builder;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface TSVBuilder {
    default void buildFile(String name, Category category, String[] headers, List<String> lines) throws IOException {
        buildFile(name, category, headers, lines, 0, 0, Collections.emptyList());
    }
    default void buildFile(String name, Category category, String[] headers, List<String> lines, int skipLines) throws IOException {
        buildFile(name, category, headers, lines, skipLines, 0, Collections.emptyList());
    }

    default void buildFile(String name, Category category, String[] headers, List<String> lines, int skipLines, int skipOffset) throws IOException {
        buildFile(name, category, headers, lines, skipLines, skipOffset, Collections.emptyList());
    }
    default void buildFile(String name, Category category, String[] headers, List<String> lines, int skipLines, Collection<Integer> clonedColumns) throws IOException {
        buildFile(name, category, headers, lines, skipLines, 0, clonedColumns);
    }

    void buildFile(String name, Category category, String[] headers, List<String> lines, int skipLines, int skipOffset, Collection<Integer> clonedColumns) throws IOException;
}
