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
export class AesTrackUtils {

    public static AE_TRACK_NAME = 'AE';
    public static AE_SUMMARY_TRACK_NAME = 'Summary';

    // COLOURS
    public static MILD_COLOUR = '#99FF66';
    public static MILD_COLOUR_NO_END_DATE = '#C1FFA3';
    public static MODERATE_COLOUR = '#FFFF59';
    public static MODERATE_COLOUR_NO_END_DATE = '#FFFF9B';
    public static SERVER_COLOUR = '#FF9900';
    public static SERVER_COLOUR_NO_END_DATE = '#FFC166';
    public static LIFE_THEATENING_COLOUR = '#FF3300';
    public static LIFE_THEATENING_COLOUR_NO_END_DATE = '#FF8466';
    public static DEATH_COLOUR = '#1A0000';
    public static EMPTY_COLOUR = '#CCCCCC';

    // Expansion level
    public static SUMMARY_SUB_TRACK_EXPANSION_LEVEL = 1;
    public static DETAIL_SUB_TRACK_EXPANSION_LEVEL = 2;

    static assignColour(severityGrade: number, noEndDate: boolean, onGoing: boolean): string {
        switch (severityGrade) {
            case 1:
                return noEndDate || onGoing ? AesTrackUtils.MILD_COLOUR_NO_END_DATE : AesTrackUtils.MILD_COLOUR;
            case 2:
                return noEndDate || onGoing ? AesTrackUtils.MODERATE_COLOUR_NO_END_DATE : AesTrackUtils.MODERATE_COLOUR;
            case 3:
                return noEndDate || onGoing ? AesTrackUtils.SERVER_COLOUR_NO_END_DATE : AesTrackUtils.SERVER_COLOUR;
            case 4:
                return noEndDate || onGoing ? AesTrackUtils.LIFE_THEATENING_COLOUR_NO_END_DATE : AesTrackUtils.LIFE_THEATENING_COLOUR;
            case 5:
                return AesTrackUtils.DEATH_COLOUR;
            default:
                return AesTrackUtils.EMPTY_COLOUR;
        }
    }
}
