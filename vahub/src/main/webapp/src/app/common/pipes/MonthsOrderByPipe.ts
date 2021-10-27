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

const months: any = {
    'JAN': 0,
    'FEB': 1,
    'MAR': 2,
    'APR': 3,
    'MAY': 4,
    'JUN': 5,
    'JUL': 6,
    'AUG': 7,
    'SEP': 8,
    'OCT': 9,
    'NOV': 10,
    'DEC': 11
};
const longDateRegexp = /^(\d{1,2})-(\w{3})-(\d{2})$/;
const shortDateRegexp = /^(\w{3})-(\d{4})$/;

@Pipe({name: 'monthsSort'})
export class MonthsOrderByPipe implements PipeTransform {
    transform(array: Array<any>, groupName, sortProperty = 'label'): Array<any> {
        // sortProperty = args[1] ? args[1] : 'label';
        if (['FIRST_TREATMENT_DATE', 'LAST_TREATMENT_DATE', 'RANDOMISATION_DATE', 'DATE_OF_DEATH'].indexOf(groupName) === -1 || _.isEmpty(array)) {
            return array;
        } else {
            let compareFunc: Function = a => a;
            const arrayElement = array[0][sortProperty].toLowerCase() !== '(empty)' ? array[0] : array[1];

            if (arrayElement[sortProperty].match(shortDateRegexp)) {
                compareFunc = (date: string) => {
                    switch (date.toLowerCase()) {
                        case '(empty)':
                            return new Date(MAX_NUMBER - 1);
                        case 'total':
                            return new Date(MAX_NUMBER);
                        default:
                            const matchedString = date.match(shortDateRegexp);
                            return new Date(parseInt(matchedString[2], 10), months[matchedString[1]]);
                    }
                };
            } else {
                if (arrayElement[sortProperty].split(' ')[0].match(longDateRegexp)) {
                    compareFunc = (date: string) => {
                        switch (date.toLowerCase()) {
                            case '(empty)':
                                return new Date(10000000000000 - 1);
                            case 'total':
                                return new Date(10000000000000);
                            default:
                                const matchedString = date.split(' ')[0].match(longDateRegexp);
                                return new Date(parseInt(matchedString[3], 10), months[matchedString[2]], parseInt(matchedString[1], 10));
                        }
                    };
                }
            }
            return array.sort((a: any, b: any) => {
                const dateA = compareFunc(a[sortProperty]);
                const dateB = compareFunc(b[sortProperty]);
                if (dateA < dateB) {
                    return -1;
                } else if (dateA > dateB) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }
    }
}
