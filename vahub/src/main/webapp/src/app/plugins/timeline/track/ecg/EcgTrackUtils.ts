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
import {EcgWarnings, CheckBoxItem} from '../../store/ITimeline';

@Injectable()
export class EcgTrackUtils {

    public static ECG_TRACK_NAME = 'ECG';
    public static SUMMARY_SUB_TRACK_NAME = 'Summary';

    public static WARNING_COLOUR = '#800000';
    public static MAXIMAL_COLOUR = 'rgb(251,112,2)';
    private static MAXIMAL_RED = 251;
    private static MAXIMAL_GREEN = 112;
    private static MAXIMAL_BLUE = 2;

    public static MINIMAL_COLOUR = 'rgb(2, 145, 205)';
    private static MINIMAL_RED = 2;
    private static MINIMAL_GREEN = 145;
    private static MINIMAL_BLUE = 205;

    private static BASELINE_RED = 165;
    private static BASELINE_GREEN = 165;
    private static BASELINE_BLUE = 165;

    public static BASELINE_COLOUR = '#838B83';
    public static REFERENCE_LINE_COLOUR = '#FFA500';

    public static LOWER_REFERENCE_LINE = 450;
    public static MIDDLE_REFERENCE_LINE = 480;
    public static UPPER_REFERENCE_LINE = 500;

    public static ECG_MEASUREMENT_COLOUR = 'rgb(124, 181, 236)';

    // Expansion level
    public static SUMMARY_SUB_TRACK_EXPANSION_LEVEL = 1;
    public static MEASUREMENT_SUB_TRACK_EXPANSION_LEVEL = 2;
    public static DETAIL_SUB_TRACK_EXPANSION_LEVEL = 3;

    // normal lab symbol
    public static NORMAL_ECG_SYMBOL = 'square';
    public static ABOVE_BASELINE_ECG_SYMBOL = 'triangle';
    public static BELOW_BASELINE_ECG_SYMBOL = 'triangle-down';
    public static BASELINE_ECG_SYMBOL = 'circle';

    private static ecgWarnings = <EcgWarnings> {
        qtcfMale: <CheckBoxItem> {label: 'QTcF male', selected: true},
        qtcfFemale: <CheckBoxItem> {label: 'QTcF female', selected: true},
        qtcfChange: <CheckBoxItem> {label: 'QTcF change from baseline', selected: true},
        abnormal: <CheckBoxItem> {label: 'Abnormal', selected: true},
        significant: <CheckBoxItem> {label: 'Significant', selected: true}
    };

    private static hasWarningColour(metadata: any, qtcfValue: number, qtcfChange: number): boolean {
        const sex = metadata.sex ? metadata.sex.toUpperCase() : null;
        const abnormality = metadata.abnormality && metadata.abnormality.toUpperCase() !== 'NO' && metadata.abnormality.toUpperCase() !== 'N';
        const significant = metadata.significant && metadata.significant.toUpperCase() !== 'NO' && metadata.significant.toUpperCase() !== 'N';
        return (
            (qtcfValue >= 450 && (sex === 'MALE' || sex === 'M') && EcgTrackUtils.ecgWarnings.getIn(['qtcfMale', 'selected'])) ||
            (qtcfValue >= 480 && (sex === 'FEMALE' || sex === 'F') && EcgTrackUtils.ecgWarnings.getIn(['qtcfFemale', 'selected'])) ||
            (Math.abs(qtcfChange) >= 60 && EcgTrackUtils.ecgWarnings.getIn(['qtcfChange', 'selected'])) ||
            (abnormality && EcgTrackUtils.ecgWarnings.getIn(['abnormal', 'selected'])) ||
            (significant && EcgTrackUtils.ecgWarnings.getIn(['significant', 'selected']))
        );
    }

    private static assignColour(maxValuePercentChange: number): string {
        let color;
        if (maxValuePercentChange >= 100) {
            color = EcgTrackUtils.MAXIMAL_COLOUR;
        } else {
            if (maxValuePercentChange <= -100) {
                color = EcgTrackUtils.MINIMAL_COLOUR;
            } else {
                let red, green, blue;
                if (maxValuePercentChange >= 0) {
                    red = EcgTrackUtils.MAXIMAL_RED;
                    green = EcgTrackUtils.MAXIMAL_GREEN;
                    blue = EcgTrackUtils.MAXIMAL_BLUE;
                } else {
                    red = EcgTrackUtils.MINIMAL_RED;
                    green = EcgTrackUtils.MINIMAL_GREEN;
                    blue = EcgTrackUtils.MINIMAL_BLUE;
                }
                maxValuePercentChange = Math.abs(maxValuePercentChange);
                const newRed = EcgTrackUtils.BASELINE_RED + (red - EcgTrackUtils.BASELINE_RED) * (maxValuePercentChange / 100);
                const newGreen = EcgTrackUtils.BASELINE_GREEN + (green - EcgTrackUtils.BASELINE_GREEN) * (maxValuePercentChange / 100);
                const newBlue = EcgTrackUtils.BASELINE_BLUE + (blue - EcgTrackUtils.BASELINE_BLUE) * (maxValuePercentChange / 100);
                color = 'rgb(' + [Math.round(newRed), Math.round(newGreen), Math.round(newBlue)].join(', ') + ')';
            }
        }
        return color;
    }

    static getLegendColors(color: string): string {
        return EcgTrackUtils[color];
    }

    static setWarnings(warnings: EcgWarnings): void {
        EcgTrackUtils.ecgWarnings = warnings;
    }

    static assignSummaryTrackSymbol(metadata: any): any {
        const symbol = EcgTrackUtils.NORMAL_ECG_SYMBOL;
        let color;

        if (EcgTrackUtils.hasWarningColour(metadata, metadata.qtcfValue, metadata.qtcfChange)) {
            color = EcgTrackUtils.WARNING_COLOUR;
        } else {
            color = this.assignColour(metadata.maxValuePercentChange);
        }
        return {fillColor: color, markerSymbol: symbol};
    }

    static assignMeasurementTrackSymbol(metadata: any): any {
        let symbol, color;

        if (metadata.baselineFlag) {
            symbol = EcgTrackUtils.BASELINE_ECG_SYMBOL;
        } else {
            symbol = EcgTrackUtils.NORMAL_ECG_SYMBOL;
        }
        if (metadata.testName.toUpperCase().indexOf('QTCF') !== -1 && EcgTrackUtils.hasWarningColour(metadata, metadata.valueRaw, metadata.valueChangeFromBaseline)) {
            color = EcgTrackUtils.WARNING_COLOUR;
        } else {
            color = this.assignColour(metadata.valuePercentChangeFromBaseline);
        }
        return {fillColor: color, markerSymbol: symbol};
    }

    static assignDetailTrackSymbol(metadata: any): any {
        let symbol, color;
        symbol = EcgTrackUtils.BASELINE_ECG_SYMBOL;
        if (metadata.testName.toUpperCase().indexOf('QTCF') !== -1 && EcgTrackUtils.hasWarningColour(metadata, metadata.valueRaw, metadata.valueChangeFromBaseline)) {
            color = EcgTrackUtils.WARNING_COLOUR;
        }
        return {fillColor: color, markerSymbol: symbol};
    }
}
