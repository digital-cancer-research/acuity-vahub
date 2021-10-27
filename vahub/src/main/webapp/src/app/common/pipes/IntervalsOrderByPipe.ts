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
import * as  _ from 'lodash';
import {MAX_NUMBER} from '../trellising/store/ITrellising';

@Pipe({name: 'intervalsSort'})
export class IntervalsOrderByPipe implements PipeTransform {
    transform(array: Array<any>, groupName, sortProperty = 'label'): Array<any> {
        if (['WEIGHT', 'HEIGHT', 'TOTAL_DURATION_ON_STUDY', 'DOSE', 'DURATION_ON_STUDY'].indexOf(groupName) === -1 || _.isEmpty(array)) {
            return array;
        } else {
            const compareFunc: Function = (interval: string) => {
                switch (interval.toLowerCase()) {
                    case '(empty)':
                        return MAX_NUMBER - 1;
                    case 'total':
                        return MAX_NUMBER;
                    default:
                        const matchedString = interval.split('-');
                        return parseFloat(matchedString[0]);
                }
            };

            return array.sort((a: any, b: any) => {
                const intervalA = compareFunc(a[sortProperty]);
                const intervalB = compareFunc(b[sortProperty]);
                if (intervalA < intervalB) {
                    return -1;
                } else if (intervalA > intervalB) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }
    }
}
