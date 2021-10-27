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

import {EventDateType} from './IChartEvent';

export class TimelineUtils {
    // chart symbols
    public static ONGOING_SYMBOL = '/assets/images/ongoing.png';
    public static STEP3_ONGOING_SYMBOL = '/assets/images/step_ongoing.png';
    public static INFORMED_CONSENT_SYMBOL = '/assets/images/consent.png';
    public static SCREENING_VISIT_SYMBOL = '/assets/images/visit.png';
    public static LAST_VISIT_SYMBOL = '/assets/images/visit.png';
    public static FIRST_DOSE_SYMBOL = '/assets/images/dose.png';
    public static LAST_DOSE_SYMBOL = '/assets/images/dose.png';
    public static WITHDRAWAL_COMPLETION_SYMBOL = '/assets/images/completion.png';
    public static DEATH_SYMBOL = '/assets/images/death.png';
    public static RANDOMISATION_SYMBOL = '/assets/images/random.png';
    public static ABOVE_REFERENCE_RANGE = '/assets/images/above-ref.png';
    public static BELOW_REFERENCE_RANGE = '/assets/images/below-ref.png';
    public static ABOVE_BELOW_REFERENCE_RANGE = '/assets/images/above-below-ref.png';
    public static ABOVE_SEVERITY_THESHOLD = '/assets/images/above-sev.png';
    public static BELOW_SEVERITY_THESHOLD = '/assets/images/below-sev.png';
    public static ABOVE_BELOW_SEVERITY_THESHOLD = '/assets/images/above-below-sev.png';
    public static LAST_VISIT_END_SYMBOL = '/assets/images/last_visit_end.png';

    // chart heights
    public static CHART_LEFT_MARGIN = 30;
    public static CHART_RIGHT_MARGIN = 10;
    public static CHART_BOTTOM_MARGIN = 15;
    public static CHART_TOP_MARGIN = 15;
    public static BAR_CHART_HEIGHT = 30;
    public static STEPPED_LINE_CHART_HEIGHT = 60;
    public static LINE_CHART_HEIGHT = 100;

    // chart highlight
    public static CHART_HIGHLIGHT_BACKGROUND = '#FCFFC5';
    public static CHART_NONE_HIGHLIGHT_BACKGROUND = 'white';

    // ongoing symbol
    public static ONGOING_MARKER_SYMBOL_HEIGHT = 20;
    public static ONGOING_MARKER_SYMBOL_WIDTH = 20;

    // marker symbol e.g. visit symbol
    public static MARKER_SYMBOL_HEIGHT = 10;
    public static MARKER_SYMBOL_WIDTH = 15;

    public static STEP3_MARKER_SYMBOL_HEIGHT = 14;
    public static STEP3_MARKER_SYMBOL_WIDTH = 10;
}

export function getUrlWrappedMarkerSymbol(type: EventDateType): string {
    return getMarkerSymbol(type);
}

export function getMarkerHeight(type: EventDateType): number {
    switch (type) {
        case EventDateType.ONGOING:
            return TimelineUtils.ONGOING_MARKER_SYMBOL_HEIGHT;
        default:
            return TimelineUtils.MARKER_SYMBOL_HEIGHT;
    }
}

export function getMarkerWidth(type: EventDateType): number {
    switch (type) {
        case EventDateType.ONGOING:
            return TimelineUtils.ONGOING_MARKER_SYMBOL_WIDTH;
        default:
            return TimelineUtils.MARKER_SYMBOL_WIDTH;
    }
}

/**
 * Move the tooltip to the left of the study narrative graph for points, and position downwards if in top half of the screen,
 * otherwise downwards.
 *
 * @param {number} labelWidth
 * @param {number} labelHeight
 * @param {Point} point
 * @param {Chart} chart
 */
export function positionToolTipForPointDependantOnScreenLocation(labelWidth: number, labelHeight: number,
                                                                 point: any, chart: any): any {
    let x = -10, y = 0;

    if (labelHeight > 20) { // large tooltip
        const element = $('#' + chart.container.id)[0];
        const rect = element.getBoundingClientRect();
        const plotX = point.plotX + rect.left;
        const plotY = point.plotY + rect.top;
        if (chart && chart.container && chart.container.id) {

            x = isPositionInLeftHalf(plotX) ? plotX + 35 : plotX - labelWidth + 35;

            y = isPositionInTopHalf(plotY) ? plotY : plotY - labelHeight;
        } else { //default if no chart location specified
            y = plotY - labelHeight;
            x = plotX - labelWidth + 35;
        }
    }
    return {x: x, y: y};
}

/**
 * Format the day hour string
 *
 * NOTE: this is a special case we have to put in the front-end,
 * the backend will not be able to do this as it needs the dates to match up
 *
 * if the day hour string is mid night, e.g. 12d 00:00,
 * then we need to change that to 11d 23:59
 */
export function formatDayHourString(dateHourString: string, dayHour: number, isOngoing: boolean): string {
    const midNightString = '00:00';
    if (!isOngoing && dateHourString && dayHour % 1 === 0) {
        const matches = dateHourString.match(/^(-?\d*)d.+/);
        const dayNum = parseInt(matches[0], 10);
        const newDayHourString = (dayNum - 1) + 'd 23:59';
        return newDayHourString;
    } else {
        return dateHourString;
    }
}

function getMarkerSymbol(type: EventDateType): string {
    switch (type) {
        case EventDateType.INFORMED_CONSENT:
            return TimelineUtils.INFORMED_CONSENT_SYMBOL;
        case EventDateType.SCREENING_VISIT:
            return TimelineUtils.SCREENING_VISIT_SYMBOL;
        case EventDateType.LAST_VISIT:
            return TimelineUtils.LAST_VISIT_SYMBOL;
        case EventDateType.FIRST_DOSE:
            return TimelineUtils.FIRST_DOSE_SYMBOL;
        case EventDateType.LAST_DOSE:
            return TimelineUtils.LAST_DOSE_SYMBOL;
        case EventDateType.WITHDRAWAL_COMPLETION:
            return TimelineUtils.WITHDRAWAL_COMPLETION_SYMBOL;
        case EventDateType.DEATH:
            return TimelineUtils.DEATH_SYMBOL;
        case  EventDateType.RANDOMISATION:
            return TimelineUtils.RANDOMISATION_SYMBOL;
        case EventDateType.ONGOING:
            return TimelineUtils.ONGOING_SYMBOL;
        case EventDateType.STEP3_ONGOING:
            return TimelineUtils.STEP3_ONGOING_SYMBOL;
        case EventDateType.LAST_VISIT_END:
            return TimelineUtils.LAST_VISIT_END_SYMBOL;
        default:
            if (type && type.toString().includes('Visit')) {
                return TimelineUtils.LAST_VISIT_END_SYMBOL;
            } else {
                return '';
            }
    }
}

/**
 * Is the position in top half of the screen
 *
 * @param {number} positionY - y value of position
 * @return {boolean} if position is in the top half of the screen
 */
function isPositionInTopHalf(positionY: number): boolean {
    const middle = window.innerHeight / 2;
    return positionY < middle;
}

/**
 * Is the position in left half of the screen
 *
 * @param {number} positionX - x value of position
 * @returns {boolean} if position is in the left half of the screen
 */
function isPositionInLeftHalf(positionX: number): boolean {
    const middle = window.innerWidth / 2;
    return positionX < middle;
}
