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

import {Injectable} from '@angular/core';

@Injectable()
export class VitalsTrackUtils {

    public static VITALS_TRACK_NAME = 'Vitals';
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
    public static VITALS_MEASUREMENT_COLOUR = 'rgb(124, 181, 236)';

    // Expansion level
    public static SUMMARY_SUB_TRACK_EXPANSION_LEVEL = 1;
    public static MEASUREMENT_SUB_TRACK_EXPANSION_LEVEL = 2;
    public static DETAIL_SUB_TRACK_EXPANSION_LEVEL = 3;

    // normal lab symbol
    public static VITALS_SYMBOL = 'square';
    public static BASELINE_VITALS_SYMBOL = 'circle';

    private static assignColour(percentChange: number): string {

        if (percentChange === 100) {
            return VitalsTrackUtils.MAXIMAL_COLOUR;
        }

        if (percentChange === -100) {
            return VitalsTrackUtils.MINIMAL_COLOUR;
        }

        let red, green, blue;
        if (percentChange >= 0) {
            red = VitalsTrackUtils.MAXIMAL_RED;
            green = VitalsTrackUtils.MAXIMAL_GREEN;
            blue = VitalsTrackUtils.MAXIMAL_BLUE;
        } else {
            red = VitalsTrackUtils.MINIMAL_RED;
            green = VitalsTrackUtils.MINIMAL_GREEN;
            blue = VitalsTrackUtils.MINIMAL_BLUE;
        }

        const absPercentChange = Math.abs(percentChange);
        const newRed = VitalsTrackUtils.BASELINE_RED + (red - VitalsTrackUtils.BASELINE_RED) * (absPercentChange / 100);
        const newGreen = VitalsTrackUtils.BASELINE_GREEN + (green - VitalsTrackUtils.BASELINE_GREEN) * (absPercentChange / 100);
        const newBlue = VitalsTrackUtils.BASELINE_BLUE + (blue - VitalsTrackUtils.BASELINE_BLUE) * (absPercentChange / 100);
        return 'rgb(' + [Math.round(newRed), Math.round(newGreen), Math.round(newBlue)].join(', ') + ')';
    }

    static assignSummaryTrackSymbol(metadata: any): any {
        return {
            fillColor: this.assignColour(metadata.maxValuePercentChange),
            markerSymbol: VitalsTrackUtils.VITALS_SYMBOL
        };
    }

    static assignMeasurementTrackSymbol(metadata: any): any {
        let symbol, color;

        if (metadata.baselineFlag) {
            symbol = VitalsTrackUtils.BASELINE_VITALS_SYMBOL;
            color = VitalsTrackUtils.BASELINE_COLOUR;
        } else {
            symbol = VitalsTrackUtils.VITALS_SYMBOL;
            color = this.assignColour(metadata.valuePercentChangeFromBaseline);
        }
        return { fillColor: color, markerSymbol: symbol };
    }
}
