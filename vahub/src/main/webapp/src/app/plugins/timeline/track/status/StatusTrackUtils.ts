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
import {EventDateType} from '../../chart/IChartEvent';

@Injectable()
export class StatusTrackUtils {

    public static STATUS_TRACK_NAME = 'Status';
    public static STATUS_SUMMARY_TRACK_NAME = 'Summary';

    // COLOURS
    public static RUN_IN_EVENT_COLOUR = '#4EDC5F';
    public static RANDOMISED_EVENT_COLOUR = '#FF8C5B';
    public static ON_STUDY_DRUG = '#B507AA';

    // Expansion level
    public static SUMMARY_TRACK_EXPANSION_LEVEL = 1;

    static assignColour(type: EventDateType): string {
        if (type === EventDateType.RUN_IN) {
            return StatusTrackUtils.RUN_IN_EVENT_COLOUR;
        } else if (type === EventDateType.RANDOMIZED_DRUG) {
            return StatusTrackUtils.RANDOMISED_EVENT_COLOUR;
        } else if (type === EventDateType.ON_STUDY_DRUG) {
            return StatusTrackUtils.ON_STUDY_DRUG;
        }
    }
}
