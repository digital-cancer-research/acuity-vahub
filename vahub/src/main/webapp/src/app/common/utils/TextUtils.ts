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

import {isEmpty, isNull} from 'lodash';
import {EMPTY, NO_AVAILABLE_PARAMETERS, TimepointConstants, TabId} from '../trellising/store';

export class TextUtils {

    private static symbols = (() => {
        const map = {};
        const put = function (key: string, value?: string): void {
            map[key] = value || key;
        };
        put('ID');
        put('AST');
        put('ALT');
        put('AST/ALT');
        put('PT');
        put('SOC');
        put('HLT');
        put('PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED',
            'Percentage of subjects, 100% stacked');
        return map;
    })();

    static stringOrEmpty(x: any): string {
        // not a mistake: '== null' also checks for undefined
        return x == null ? EMPTY : '' + x;
    }

    static startsWithAbbreviation(text: string): boolean {
        if (text) {
            const words = text.split(' ');
            if (words.length) {
                const word = words[0];
                return TextUtils.symbols[word] === word;
            }
        }
        return false;
    }

    static makeSentence(...parts: string[]): string {
        let result = '';
        parts.forEach((part, index) => {
            if (!part) {
                return;
            }
            part = part.trim();
            if (index === 0 || result === '') {
                // first segment used unchanged
                result = part;
            } else {
                // extract first word of segment
                const first = part.replace(/(\S+).+/, '$1');
                // if it's all uppercase, assume it's an abbreviation
                if (first !== first.toUpperCase()) {
                    // otherwise locase the initial letter
                    part = part.charAt(0).toLowerCase() + part.slice(1);
                }
                result += ' ' + part;
            }
        });
        return result;
    }

    static toSentenceCase(value: string, delimiter = ' '): string {
        if (!value) {
            return value;
        }

        const upperValue = value.toUpperCase();
        const mapped = TextUtils.symbols[upperValue];
        if (mapped) {
            return mapped;
        }

        //split the string by delimiter
        const words = upperValue.split(delimiter);
        words.forEach((word, index) => {
            const wordMapped = TextUtils.symbols[word];
            if (wordMapped) {
                words[index] = wordMapped;
            } else if (index > 0) {
                words[index] = word.toLowerCase();
            } else {
                words[index] = word.charAt(0) + word.slice(1).toLowerCase();
            }
        });

        //Join with spaces
        return words.join(' ');
    }

    /**
     * Checks if any item in collection contains string
     * @param collection - Iterable collection
     * @param query - Search query
     * @returns {boolean}
     */
    static contains(collection: any[], query: string): boolean {
        if (isEmpty(query)) {
            return true;
        }

        return collection.some((value) => {
            return !isNull(value) && value.toString().toLowerCase().indexOf(query.toLowerCase()) > -1;
        });
    }

    private static addDescriptionBeforeLabel(desc: string, label: string): string {
        return `${desc}: ${label}`;
    }

    static addDescriptionBeforeTimepoints(timepointType: string, labels: any): string {
        if (labels === NO_AVAILABLE_PARAMETERS) {
            return EMPTY;
        }
        switch (timepointType) {
            case TimepointConstants.CYCLE_DAY:
                return this.addDescriptionBeforeLabel('Cycle', labels.get(0)) + ' '
                    + this.addDescriptionBeforeLabel('Day', labels.get(1));
            case TimepointConstants.VISIT:
                return this.addDescriptionBeforeLabel('Visit', labels);
            case TimepointConstants.VISIT_NUMBER:
                return this.addDescriptionBeforeLabel('Visit number', labels);
            default:
                return labels;
        }
    }

    static changeWeekToAssessmentWeek(label: string, tabId: TabId): string {
        return tabId === TabId.PK_RESULT_OVERALL_RESPONSE ? label.replace('week', 'RECIST assessment week') : label;
    }
}
