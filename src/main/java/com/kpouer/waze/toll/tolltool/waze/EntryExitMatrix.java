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

public class EntryExitMatrix {
    private String friendly_name;
    float[][] matrix_prices;
    private boolean  each_section_is_entry_or_exit;
    private String   permit_id;
    private String[] limit_to_vehicles;

    public EntryExitMatrix() {
    }

    public String getFriendly_name() {
        return friendly_name;
    }

    public void setFriendly_name(String friendly_name) {
        this.friendly_name = friendly_name;
    }

    public float[][] getMatrix_prices() {
        return matrix_prices;
    }

    public void setMatrix_prices(float[][] matrix_prices) {
        this.matrix_prices = matrix_prices;
    }

    public boolean isEach_section_is_entry_or_exit() {
        return each_section_is_entry_or_exit;
    }

    public void setEach_section_is_entry_or_exit(boolean each_section_is_entry_or_exit) {
        this.each_section_is_entry_or_exit = each_section_is_entry_or_exit;
    }

    public String getPermit_id() {
        return permit_id;
    }

    public void setPermit_id(String permit_id) {
        this.permit_id = permit_id;
    }

    public String[] getLimit_to_vehicles() {
        return limit_to_vehicles;
    }

    public void setLimit_to_vehicles(String[] limit_to_vehicles) {
        this.limit_to_vehicles = limit_to_vehicles;
    }

    public EntryExitMatrix(String friendly_name, float[][] matrix_prices, String permit_id, String[] limit_to_vehicles) {
        this.friendly_name     = friendly_name;
        this.matrix_prices     = matrix_prices;
        this.permit_id         = permit_id;
        this.limit_to_vehicles = limit_to_vehicles;
    }
}
