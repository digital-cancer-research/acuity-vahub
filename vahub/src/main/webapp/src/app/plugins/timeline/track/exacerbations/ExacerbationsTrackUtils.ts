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
export class ExacerbationsTrackUtils {

    public static EXACERBATIONS_TRACK_NAME = 'Exacerbations';
    public static EXACERBATIONS_SUMMARY_SUB_TRACK_NAME = 'Summary';

    // COLOURS
    public static UNKNOWN_GRADE_EXACERBATIONS_COLOUR = '#B1C8ED';
    public static GRADE1_END_DATE_EXACERBATIONS_COLOUR = '#FE8C01';
    public static GRADE1_NO_END_DATE_EXACERBATIONS_COLOUR = '#FEAF4D';
    public static GRADE2_END_DATE_EXACERBATIONS_COLOUR = '#F7D533';
    public static GRADE2_NO_END_DATE_EXACERBATIONS_COLOUR = '#FAE47D';
    public static GRADE3_END_DATE_EXACERBATIONS_COLOUR = '#B4DA50';
    public static GRADE3_NO_END_DATE_EXACERBATIONS_COLOUR = '#CFE78F';

    // Expansion level
    public static EXACERBATIONS_SUMMARY_TRACK_EXPANSION_LEVEL = 1;

    static assignColour(severityGrade: any, hasEndDate: boolean): string {
        switch (severityGrade) {
            case 'SEVERE':
            case '(C) Severe':
            case 1:
                return hasEndDate
                    ? ExacerbationsTrackUtils.GRADE1_END_DATE_EXACERBATIONS_COLOUR
                    : ExacerbationsTrackUtils.GRADE1_NO_END_DATE_EXACERBATIONS_COLOUR;
            case 'MODERATE':
            case '(B) Moderate':
            case 2:
                return hasEndDate
                    ? ExacerbationsTrackUtils.GRADE2_END_DATE_EXACERBATIONS_COLOUR
                    : ExacerbationsTrackUtils.GRADE2_NO_END_DATE_EXACERBATIONS_COLOUR;
            case 'MILD':
            case '(A) Mild':
            case 3:
                return hasEndDate
                    ? ExacerbationsTrackUtils.GRADE3_END_DATE_EXACERBATIONS_COLOUR
                    : ExacerbationsTrackUtils.GRADE3_NO_END_DATE_EXACERBATIONS_COLOUR;
            default:
                return ExacerbationsTrackUtils.UNKNOWN_GRADE_EXACERBATIONS_COLOUR;
        }
    }
}
