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
import {Subject} from 'rxjs/Subject';
import {AEsYAxisOptions, MeasureNumberOptions, TimeScaleOptions, UnitOptions} from '../store/ITrellising';
import * as  _ from 'lodash';

/**
 * Service for AxisLabelService events when a user clicks on axis selector other selectors should be closed
 */
@Injectable()
export class AxisLabelService {

    /**
     * sends messages about the opened axis selector
     */
    axisLabelOpened: Subject<any> = new Subject<any>();
    timeScaleOptions: any[] = [
        TimeScaleOptions.STUDY_DEFINED_WEEK,
        TimeScaleOptions.VISIT_NUMBER,
        TimeScaleOptions.WEEKS_SINCE,
        TimeScaleOptions.DAYS_SINCE
    ];
    measureNumberOptions: any[] = [
        MeasureNumberOptions.EVENTS,
        MeasureNumberOptions.SUBJECTS
    ];
    unitOptions: any[] = [
        UnitOptions.COUNT,
        UnitOptions.PERCENT_OF_TOTAL,
        UnitOptions.PERCENT_WITHIN_PLOT
    ];

    constructor() {
    }

    /**
     * Sets the current selected subjects and fires and change event
     */
    closeOtherAxisLabels(): void {
        this.axisLabelOpened.next(null);
    }

    getSelectedTimeScaleOption(option: string): string {
        return _.sample(_.filter(this.timeScaleOptions, (timeScaleOption) => {
            return option.indexOf(timeScaleOption) !== -1;
        }));
    }

    getSelectedEpochTimePointOption(option: string, timeScaleOption: string): string {
        const epochTimePointOption = option.replace(timeScaleOption + '_', '');
        return epochTimePointOption.length > 0 ? epochTimePointOption : null;
    }

    getMeasureNumberOptionByFullOption(option: any): any {
        switch (option) {
            case AEsYAxisOptions.COUNT_OF_SUBJECTS:
            case AEsYAxisOptions.PERCENTAGE_OF_ALL_SUBJECTS:
            case AEsYAxisOptions.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT:
                return MeasureNumberOptions.SUBJECTS;
            case AEsYAxisOptions.COUNT_OF_EVENTS:
            case AEsYAxisOptions.PERCENTAGE_OF_ALL_EVENTS:
            case AEsYAxisOptions.PERCENTAGE_OF_EVENTS_WITHIN_PLOT:
                return MeasureNumberOptions.EVENTS;
            default:
                return MeasureNumberOptions.SUBJECTS;
        }
    }
    getFullOption(option: any): any[] {
        switch (option) {
            case MeasureNumberOptions.SUBJECTS:
                return [
                    AEsYAxisOptions.COUNT_OF_SUBJECTS,
                    AEsYAxisOptions.PERCENTAGE_OF_ALL_SUBJECTS,
                    AEsYAxisOptions.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT
                ];
            case MeasureNumberOptions.EVENTS:
                return [
                    AEsYAxisOptions.COUNT_OF_EVENTS,
                    AEsYAxisOptions.PERCENTAGE_OF_ALL_EVENTS,
                    AEsYAxisOptions.PERCENTAGE_OF_EVENTS_WITHIN_PLOT
                ];
            default:
                return [
                    AEsYAxisOptions.COUNT_OF_SUBJECTS,
                    AEsYAxisOptions.PERCENTAGE_OF_ALL_SUBJECTS,
                    AEsYAxisOptions.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT
                ];
        }
    }
    getAvailableMeasureNumberOptions(options: any): string[] {
        return _.uniq(_.map(options, (option) => {
            return this.getdMeasureNumberOptionByFullOption(option);
        }));
    }

    getAvailableUnitOptions(options: any): string[] {
        return _.uniq(_.map(options, (option) => {
            return this.getUnitOptionByFullOption(option);
        }));
    }

    getdMeasureNumberOptionByFullOption(option: any): any {
        switch (option) {
            case AEsYAxisOptions.COUNT_OF_SUBJECTS:
            case AEsYAxisOptions.PERCENTAGE_OF_ALL_SUBJECTS:
            case AEsYAxisOptions.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT:
                return MeasureNumberOptions.SUBJECTS;
            case AEsYAxisOptions.COUNT_OF_EVENTS:
            case AEsYAxisOptions.PERCENTAGE_OF_ALL_EVENTS:
            case AEsYAxisOptions.PERCENTAGE_OF_EVENTS_WITHIN_PLOT:
                return MeasureNumberOptions.EVENTS;
            default:
                return MeasureNumberOptions.SUBJECTS;
        }
    }

    getUnitOptionByFullOption(option: any): any {
        switch (option) {
            case AEsYAxisOptions.COUNT_OF_SUBJECTS:
            case AEsYAxisOptions.COUNT_OF_EVENTS:
                return UnitOptions.COUNT;
            case AEsYAxisOptions.PERCENTAGE_OF_ALL_SUBJECTS:
            case AEsYAxisOptions.PERCENTAGE_OF_ALL_EVENTS:
                return UnitOptions.PERCENT_OF_TOTAL;
            case AEsYAxisOptions.PERCENTAGE_OF_EVENTS_WITHIN_PLOT:
            case AEsYAxisOptions.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT:
                return UnitOptions.PERCENT_WITHIN_PLOT;
            default:
                return UnitOptions.COUNT;
        }
    }

    constructAxisLabel(measureNumberOption: any, unitOption: any): any {
        switch (measureNumberOption) {
            case MeasureNumberOptions.EVENTS:
                switch (unitOption) {
                    case UnitOptions.COUNT:
                        return AEsYAxisOptions.COUNT_OF_EVENTS;
                    case UnitOptions.PERCENT_WITHIN_PLOT:
                        return AEsYAxisOptions.PERCENTAGE_OF_EVENTS_WITHIN_PLOT;
                    case UnitOptions.PERCENT_OF_TOTAL:
                        return AEsYAxisOptions.PERCENTAGE_OF_ALL_EVENTS;
                    default:
                        return AEsYAxisOptions.COUNT_OF_EVENTS;
                }
            case MeasureNumberOptions.SUBJECTS:
                switch (unitOption) {
                    case UnitOptions.COUNT:
                        return AEsYAxisOptions.COUNT_OF_SUBJECTS;
                    case UnitOptions.PERCENT_WITHIN_PLOT:
                        return AEsYAxisOptions.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT;
                    case UnitOptions.PERCENT_OF_TOTAL:
                        return AEsYAxisOptions.PERCENTAGE_OF_ALL_SUBJECTS;
                    default:
                        return AEsYAxisOptions.COUNT_OF_SUBJECTS;
                }
            default:
                return AEsYAxisOptions.COUNT_OF_SUBJECTS;
        }
    }

    fromDynamicAxisToString(option: any): string {
        if (option) {
            let string = option.get('value', false) ? option.get('value') : undefined;
            if (string && option.get('stringarg', false)) {
                string += ' ' + option.get('stringarg');
            }
            return string;
        } else {
            return;
        }
    }
}
