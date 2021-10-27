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

import {Pipe, PipeTransform} from '@angular/core';
import {AggregationType} from '../trellising/store';

@Pipe({name: 'toSettingsTitle'})
export class SettingsPipe implements PipeTransform {
    transform(value: string, noneIfEmpty = false, settingString?: string): string {
        switch (value) {
            case 'ANALYTE_CONCENTRATION_(NG/ML)':
                return settingString === AggregationType.SUBJECT_CYCLE ? value : 'AMEAN_' + value;
            case 'SUBJECT_CYCLE':
                return !noneIfEmpty ? 'NOT_APPLIED' : '';
            case 'SUBJECT':
                return 'PER_SUBJECT';
            case 'ANALYTE':
                return 'PER_ANALYTE';
            case 'DOSE':
                return 'PER_NOMINAL_DOSE';
            case 'VISIT':
                return 'PER_VISIT';
            case 'DOSE_PER_VISIT':
                return 'PER_NOMINAL_DOSE_PER_VISIT';
            case 'DOSE_PER_CYCLE':
                return 'PER_NOMINAL_DOSE_PER_CYCLE';
            case 'CYCLE':
                return 'PER_CYCLE';
            default:
                return value;
        }
    }

}
