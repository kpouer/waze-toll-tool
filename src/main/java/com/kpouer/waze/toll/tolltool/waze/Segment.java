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

public class Segment {
    private String  permalink;
    private long    id;
    private boolean forwards;
    private long    fromNode;
    private long    toNode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isForwards() {
        return forwards;
    }

    public void setForwards(boolean forwards) {
        this.forwards = forwards;
    }

    public long getFromNode() {
        return fromNode;
    }

    public void setFromNode(long fromNode) {
        this.fromNode = fromNode;
    }

    public long getToNode() {
        return toNode;
    }

    public void setToNode(long toNode) {
        this.toNode = toNode;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getPermalink() {
        return permalink;
    }

    @Override
    public String toString() {
        return "Segment{id=" + id + ", forwards=" + forwards + ", fromNode=" + fromNode + ", toNode=" + toNode + '}';
    }
}
