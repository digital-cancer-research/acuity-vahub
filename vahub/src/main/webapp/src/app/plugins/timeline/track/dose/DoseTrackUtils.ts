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
export class DoseTrackUtils {

    public static DOSE_TRACK_NAME = 'Dose';
    public static SUMMARY_SUB_TRACK_NAME = 'Summary';

    // inactive dosing and dosing of 0 are the same.
    public static INACTIVE_DOSING_COLOUR = '#CFCFCF';
    // declare colours for gradient calculations
    // used for theoretical max dose
    public static ACTIVE_DOSING_COLOUR = '#00007A';

    public static DRUG_DETAIL_COLOUR = 'green';

    // Expansion level
    public static SUMMARY_SUB_TRACK_EXPANSION_LEVEL = 1;
    public static DRUG_SUMMARY_SUB_TRACK_EXPANSION_LEVEL = 2;
    public static DRUG_DETAIL_SUB_TRACK_EXPANSION_LEVEL = 3;

    static assignColour(percentChange: number): string {
        if (percentChange >= 0) {
            return DoseTrackUtils.ACTIVE_DOSING_COLOUR;
        } else {
            if (percentChange === -100) {
                return DoseTrackUtils.INACTIVE_DOSING_COLOUR;
            } else {
                const activeDoseHex = DoseTrackUtils.ACTIVE_DOSING_COLOUR.substring(1);
                const inactiveDoseHex = DoseTrackUtils.INACTIVE_DOSING_COLOUR.substring(1);
                const gradientColour = DoseTrackUtils.colourGradient(inactiveDoseHex, activeDoseHex, -percentChange / 100);

                return '#' + gradientColour;
            }
        }
    }

    private static colourGradient(colour1: string, colour2: string, ratio: number): string {
        const r = Math.ceil(parseInt(colour1.substring(0, 2), 16) * ratio + parseInt(colour2.substring(0, 2), 16) * (1 - ratio));
        const g = Math.ceil(parseInt(colour1.substring(2, 4), 16) * ratio + parseInt(colour2.substring(2, 4), 16) * (1 - ratio));
        const b = Math.ceil(parseInt(colour1.substring(4, 6), 16) * ratio + parseInt(colour2.substring(4, 6), 16) * (1 - ratio));

        return DoseTrackUtils.hex(r) + DoseTrackUtils.hex(g) + DoseTrackUtils.hex(b);
    }

    private static hex(x: number): string {
        const s = x.toString(16);
        return (s.length === 1) ? '0' + s : s;
    }
}
