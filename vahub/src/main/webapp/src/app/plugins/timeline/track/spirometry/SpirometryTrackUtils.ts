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

import { Injectable } from '@angular/core';

@Injectable()
export class SpirometryTrackUtils {

    public static SPIROMETRY_TRACK_NAME = 'Spirometry';
    public static SUMMARY_SUB_TRACK_NAME = 'Summary';

    public static MAXIMAL_COLOUR = 'rgb(251,112,2)';
    private static MAXIMAL_RED = 251;
    private static MAXIMAL_GREEN = 112;
    private static MAXIMAL_BLUE = 2;

    public static MINIMAL_COLOUR = 'rgb(2, 145, 205)';
    private static MINIMAL_RED = 2;
    private static MINIMAL_GREEN = 145;
    private static MINIMAL_BLUE = 205;

    public static BASELINE_COLOUR = 'rgb(165, 165, 165)';
    private static BASELINE_RED = 165;
    private static BASELINE_GREEN = 165;
    private static BASELINE_BLUE = 165;

    public static REFERENCE_LINE_COLOUR = 'rgb(165, 165, 165)';
    public static SPIROMETRY_MEASUREMENT_COLOUR = 'rgb(124, 181, 236)';

    // Expansion level
    public static SUMMARY_SUB_TRACK_EXPANSION_LEVEL = 1;
    public static CATEGORY_SUB_TRACK_EXPANSION_LEVEL = 2;
    public static DETAIL_SUB_TRACK_EXPANSION_LEVEL = 3;

    // normal spirometry symbol
    public static SPIROMETRY_SYMBOL = 'square';
    public static BASELINE_SPIROMETRY_SYMBOL = 'circle';

    private static assignColour(percentChange: number): string {
        if (percentChange >= 100) {
            return SpirometryTrackUtils.MAXIMAL_COLOUR;
        }

        if (percentChange <= -100) {
            return SpirometryTrackUtils.MINIMAL_COLOUR;
        }

        let red, green, blue;
        if (percentChange >= 0) {
            red = SpirometryTrackUtils.MAXIMAL_RED;
            green = SpirometryTrackUtils.MAXIMAL_GREEN;
            blue = SpirometryTrackUtils.MAXIMAL_BLUE;
        } else {
            red = SpirometryTrackUtils.MINIMAL_RED;
            green = SpirometryTrackUtils.MINIMAL_GREEN;
            blue = SpirometryTrackUtils.MINIMAL_BLUE;
        }

        const absPercentChange = Math.abs(percentChange);
        const newRed = SpirometryTrackUtils.BASELINE_RED + (red - SpirometryTrackUtils.BASELINE_RED) * (absPercentChange / 100);
        const newGreen = SpirometryTrackUtils.BASELINE_GREEN + (green - SpirometryTrackUtils.BASELINE_GREEN) * (absPercentChange / 100);
        const newBlue = SpirometryTrackUtils.BASELINE_BLUE + (blue - SpirometryTrackUtils.BASELINE_BLUE) * (absPercentChange / 100);
        return 'rgb(' + [Math.round(newRed), Math.round(newGreen), Math.round(newBlue)].join(', ') + ')';
    }

    static assignSummaryTrackSymbol(metadata: any): any {
        return {
            fillColor: this.assignColour(metadata.maxValuePercentChange),
            markerSymbol: SpirometryTrackUtils.SPIROMETRY_SYMBOL
        };
    }

    static assignCategoryTrackSymbol(metadata: any): any {
        let symbol, color;

        if (metadata.baselineFlag) {
            symbol = SpirometryTrackUtils.BASELINE_SPIROMETRY_SYMBOL;
            color = SpirometryTrackUtils.BASELINE_COLOUR;
        } else {
            symbol = SpirometryTrackUtils.SPIROMETRY_SYMBOL;
            color = this.assignColour(metadata.valuePercentChangeFromBaseline);
        }
        return { fillColor: color, markerSymbol: symbol };
    }
}
