/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.service.compatibility;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class ColoringService {


    public static final String[] COLORS = {
            "#3CB44C", "#DE606C", "#F9DA00", "#F58231", "#9FDB61", "#006480",
            "#731975", "#CC9933", "#FF9966", "#819999", "#26C0D0", "#E85BD5",
            "#A05100", "#808000", "#911EB4", "#808080", "#FFD052", "#665DD8",
            "#DC76D6", "#FF9900", "#CC9966", "#CC6633", "#660099", "#5E6337",
            "#AAFFC3", "#3A6B99", "#EAAEBE", "#E1E05F", "#663366", "#B0CBAA"};


    public static final String[] COLORS_NO_GREEN;

    static {
        List<String> noGreen = new ArrayList<>();
        Collections.addAll(noGreen, COLORS);
        noGreen.removeAll(Arrays.asList(COLORS[0], COLORS[4], COLORS[13], COLORS[23]));
        COLORS_NO_GREEN = noGreen.toArray(new String[noGreen.size()]);
    }

    // decrease this constant if more bright colors are required
    private static final int MAX_VISIBLE_COLOR_SUM = 700;

    public enum Colors {
        BLACK("#000000"),
        BLUE("#0000FF"),
        DIMGRAY("#696969"),
        GRAY("#808080"),
        GREEN("#00FF00"),
        ORANGE("#FFA500"),
        PINK("#FFC0CB"),
        PURPLE("#800080"),
        RED("#FF0000"),
        SADDLEBROWN("#8B4513"),
        SKYBLUE("#87CEEB"),
        WHITE("#FFFFFF"),
        YELLOW("#FFFF00"),
        LIGHTSEAGREEN("#20B2AA"),
        BRIGHTPURPLE("#D189FF");

        Colors(String code) {
            this.code = code;
        }

        @Getter
        private String code;
    }

    /**
     * Convert an int between 0 and 255 (incl.) to a two-symbol hex string with leading zeros,
     * i.e. 12 -> "0C"
     *
     * @param intColorPart - value between 0 and 255 (incl.)
     * @return two-symbol hex string with leading zeros
     */
    public String getHexColorPart(int intColorPart) {
        return Integer.toHexString(0X100 | intColorPart).substring(1);
    }

    /**
     * Convert int red, green and blue values to a hex RGB string
     *
     * @param r - red value between 0 and 255 (incl.)
     * @param g - green value between 0 and 255 (incl.)
     * @param b - blue value between 0 and 255 (incl.)
     * @return uppercase string starting with # in #RRGGBB format
     */
    public String getHexColor(int r, int g, int b) {
        String red = getHexColorPart(r);
        String green = getHexColorPart(g);
        String blue = getHexColorPart(b);
        return ("#" + red + green + blue).toUpperCase();
    }

    /**
     * Generate three random numbers from 0 to 255 (incl.)
     *
     * @return
     */
    public Triple<Integer, Integer, Integer> generateRGB() {
        Random rand = new Random();
        return Triple.of(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public String generateColor() {
        Triple<Integer, Integer, Integer> rgb = generateRGB();
        while (!isValid(rgb)) {
            rgb = generateRGB();
        }
        return getHexColor(rgb.getLeft(), rgb.getMiddle(), rgb.getRight());
    }

    protected boolean isValid(Triple<Integer, Integer, Integer> rgb) {
        return isNotFaint(rgb);
    }

    public boolean isNotRed(Triple<Integer, Integer, Integer> rgb) {
        int r = rgb.getLeft();
        int g = rgb.getMiddle();
        int b = rgb.getRight();
        return g > 100 || b > 100 || (r < g && r < b);
    }

    public boolean isNotGreen(Triple<Integer, Integer, Integer> rgb) {
        int r = rgb.getLeft();
        int g = rgb.getMiddle();
        int b = rgb.getRight();
        return r > 100 || b > 100 || (g < r && g < b);
    }

    public boolean isNotFaint(Triple<Integer, Integer, Integer> rgb) {
        int r = rgb.getLeft();
        int g = rgb.getMiddle();
        int b = rgb.getRight();
        return r + b + g <= MAX_VISIBLE_COLOR_SUM;
    }
}
