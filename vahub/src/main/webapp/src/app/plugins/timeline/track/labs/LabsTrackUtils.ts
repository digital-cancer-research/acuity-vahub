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
import {TimelineUtils} from '../../chart/TimelineUtils';
import * as  _ from 'lodash';

@Injectable()
export class LabsTrackUtils {

    public static LABS_TRACK_NAME = 'Lab';
    public static SUMMARY_SUB_TRACK_NAME = 'Summary';

    // colour for events within reference range
    public static CHANGE_FROM_BASE_LAB_COLOUR = '#7CB5EC ';
    // colour for events within reference range
    public static NORMAL_LAB_COLOUR = '#838B83';
    // colour for events out of reference range
    public static OUT_OF_REFERENCE_RANGE_COLOUR = '#FFA500';
    // colour for events out of severity threshold
    public static OUT_OF_SEVERITY_THRESHOLD_COLOUR = '#FF0000';

    // Expansion level
    public static SUMMARY_SUB_TRACK_EXPANSION_LEVEL = 1;
    public static CATEGORY_SUB_TRACK_EXPANSION_LEVEL = 2;
    public static DETAIL_SUB_TRACK_EXPANSION_LEVEL = 3;
    public static DETAIL_WITH_THRESHOLD_SUB_TRACK_EXPANSION_LEVEL = 4;

    // normal lab symbol
    public static NORMAL_LAB_SYMBOL = {
        markerSymbol: 'square',
        fillColor: '#D3D3D3'
    };

    static assignColour(value: number,
                        lowerRefLimit: number,
                        higherRefLimit: number,
                        lowerSevLimit?: number,
                        higherSevLimit?: number): string {
        if ((lowerSevLimit && value < lowerSevLimit) || (higherSevLimit && value > higherSevLimit)) {
            return LabsTrackUtils.OUT_OF_SEVERITY_THRESHOLD_COLOUR;
        } else if ((lowerRefLimit && value < lowerRefLimit) || (higherRefLimit && value > higherRefLimit)) {
            return LabsTrackUtils.OUT_OF_REFERENCE_RANGE_COLOUR;
        } else {
            return LabsTrackUtils.NORMAL_LAB_COLOUR;
        }
    }

    static assignSymbol(numAboveReferenceRange: number,
        numBelowReferenceRange: number,
        numAboveSeverityThreshold: number,
        numBelowSeverityThreshold: number): any {

        const numberOfEventsOutOfRange: number[] = [
            numBelowReferenceRange,
            numAboveReferenceRange,
            numBelowSeverityThreshold,
            numAboveSeverityThreshold];

        const convertedinaryInt: number = parseInt(_.map(numberOfEventsOutOfRange, function (num): number {
            return num > 0 ? 1 : 0;
        }).join(''), 2);

        switch (convertedinaryInt) {
            case 0:
                return LabsTrackUtils.NORMAL_LAB_SYMBOL;
            case 1:
                return LabsTrackUtils.buildImageSymbol(TimelineUtils.BELOW_SEVERITY_THESHOLD);
            case 2:
                return LabsTrackUtils.buildImageSymbol(TimelineUtils.ABOVE_SEVERITY_THESHOLD);
            case 3:
                return LabsTrackUtils.buildImageSymbol(TimelineUtils.ABOVE_BELOW_SEVERITY_THESHOLD);
            case 4:
                return LabsTrackUtils.buildImageSymbol(TimelineUtils.ABOVE_REFERENCE_RANGE);
            case 8:
                return LabsTrackUtils.buildImageSymbol(TimelineUtils.BELOW_REFERENCE_RANGE);
            case 12:
                return LabsTrackUtils.buildImageSymbol(TimelineUtils.ABOVE_BELOW_REFERENCE_RANGE);
            default:
                return LabsTrackUtils.NORMAL_LAB_SYMBOL;
        }
    }

    private static buildImageSymbol(iconUrl: string): any {
        return {
            markerSymbol: iconUrl,
            height: TimelineUtils.MARKER_SYMBOL_HEIGHT,
            width: TimelineUtils.MARKER_SYMBOL_WIDTH
        };
    }
}
