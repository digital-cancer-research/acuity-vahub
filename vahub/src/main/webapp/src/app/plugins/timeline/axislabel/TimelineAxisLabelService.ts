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
import {List, fromJS} from 'immutable';
import {DynamicAxis} from '../../../common/trellising/store/ITrellising';
import {AxisLabelService} from '../../../common/trellising/axislabel/AxisLabelService';
import {XAxisLabelService} from '../../../common/trellising/axislabel/xaxis/XAxisLabelService';
import * as _ from 'lodash';

@Injectable()
export class TimelineAxisLabelService {
    generateAdditionalSelection(option: DynamicAxis, options: List<DynamicAxis>): string[] {
        return options.toArray()
            .filter((x: DynamicAxis) => x.get('value') === option.get('value'))
            .reduce((acc: string[], x: DynamicAxis) => {
                const selectableValue = x.get('stringarg');

                if (!_.isNull(selectableValue)) {
                    acc.push(selectableValue);
                }
                return acc;
            }, []);
    }

    /**
     * Get list of options, remove dupes and convert it to iterable array for angular
     * @param options
     * @returns string[]
     */
    generateAvailableValueStrings(options: List<DynamicAxis>): string[] {
        return _.uniqWith(
            options.toArray()
            .map((x: DynamicAxis) => {
                return x.get('value');
            }),
            (current, next) => current === next
        );
    }

    /**
     * Get a single option that matches value selection
     * @param event {Event}
     * @param options {List<DynamicAxis>}
     * @returns {undefined|DynamicAxis}
     */
    optionFromValueSelection(event: any, options: List<DynamicAxis>): DynamicAxis {
        const selectedValue = event.target.value;
        return options.toArray()
            .find((x: DynamicAxis) => x.get('value') === selectedValue);
    }

    optionFromAdditionalSelection(event: any, localOption: DynamicAxis, options: List<DynamicAxis>): DynamicAxis {
        const selectedValue: any = event.target.value;
        return options.toArray()
            .find((x: DynamicAxis) => x.get('value') === localOption.get('value')
                && x.get('stringarg') === selectedValue);
    }
}
