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

import {find, intersection, isEmpty, omit} from 'lodash';
import {fromJS} from 'immutable';

import {DynamicAxis, NEW_APPROACH_TAB_LIST, NO_AVAILABLE_PARAMETERS, TabId, TimepointConstants} from '../ITrellising';
import {GroupBySetting, XAxisOptions} from '../actions/TrellisingActionCreator';
import {
    generateAvailableOptions,
    generateAvailableOptionsWithTrellising,
    generateAvailableTimepointOptions
} from '../../axislabel/newxaxis/XAxisLabelService';


export class DefaultAxisService {
    static initialX(tabId: TabId, isOngoing: boolean, axisOptions: DynamicAxis[] | XAxisOptions): DynamicAxis | GroupBySetting {
        const newApproachTab = NEW_APPROACH_TAB_LIST.contains(<string>tabId);
        switch (tabId) {
            case TabId.POPULATION_BARCHART:
            case TabId.POPULATION_TABLE:
                if (isOngoing) {
                    return this.getDefaultXIfNotExists(axisOptions, ['WITHDRAWAL', 'STUDY_ID'], newApproachTab, tabId);
                } else {
                    return this.getDefaultXIfNotExists(axisOptions, ['ACTUAL_TREATMENT_ARM', 'STUDY_ID'], newApproachTab, tabId);
                }
            case TabId.EXACERBATIONS_GROUPED_COUNTS:
                if (isOngoing) {
                    return this.getDefaultXIfNotExists(
                        axisOptions, ['WITHDRAWAL', 'COUNTRY', 'CENTER_NUMBER'], newApproachTab, tabId);
                } else {
                    return this.getDefaultXIfNotExists(
                        axisOptions, ['PLANNED_TREATMENT_ARM', 'COUNTRY', 'CENTER_NUMBER', 'STUDY_ID'], newApproachTab, tabId);
                }
            case TabId.CONMEDS_BARCHART:
                return this.getDefaultXIfNotExists(axisOptions, ['MEDICATION_NAME'], newApproachTab, tabId);
            case TabId.CI_EVENT_COUNTS:
                return this.getDefaultXIfNotExists(axisOptions, ['FINAL_DIAGNOSIS'], newApproachTab, tabId);
            case TabId.AES_OVER_TIME:
                return this.getDefaultXIfNotExists(axisOptions, ['WEEKS_SINCE_FIRST_DOSE'], newApproachTab, tabId);
            case TabId.EXACERBATIONS_OVER_TIME:
                return this.getDefaultXIfNotExists(axisOptions, ['WEEKS_SINCE_FIRST_TREATMENT'], newApproachTab, tabId);
            case TabId.CVOT_ENDPOINTS_OVER_TIME:
            case TabId.CI_EVENT_OVERTIME:
            case TabId.CEREBROVASCULAR_EVENTS_OVER_TIME:
                return this.getDefaultXIfNotExists(axisOptions, ['WEEKS_SINCE_FIRST_DOSE'], newApproachTab, tabId);
            case TabId.CEREBROVASCULAR_COUNTS:
                return this.getDefaultXIfNotExists(axisOptions, ['EVENT_TYPE'], newApproachTab, tabId);
            case TabId.CVOT_ENDPOINTS_COUNTS:
                return this.getDefaultXIfNotExists(axisOptions, ['CATEGORY_1'], newApproachTab, tabId);
            case TabId.CTDNA_PLOT:
                return this.getDefaultXIfNotExists(axisOptions, ['DAYS_SINCE_FIRST_TREATMENT'], newApproachTab, tabId);
            case TabId.EXACERBATIONS_COUNTS:
                if (isOngoing) {
                    return this.getDefaultXIfNotExists(axisOptions, ['WEEKS_SINCE_FIRST_TREATMENT'], newApproachTab, tabId);
                } else {
                    return this.getDefaultXIfNotExists(axisOptions, ['WEEKS_SINCE_FIRST_TREATMENT'], newApproachTab, tabId);
                }
            case TabId.QT_PROLONGATION:
                return this.getDefaultXIfNotExists(axisOptions, ['ALERT_LEVEL'], newApproachTab, tabId);
            case TabId.PK_RESULT_OVERALL_RESPONSE:
                return this.getDefaultXIfNotExists(axisOptions, ['BEST_RESPONSE'], newApproachTab, tabId);
            default:
                if (!isOngoing) {
                    return this.getDefaultXIfNotExists(axisOptions, ['STUDY_DEFINED_WEEK'], newApproachTab, tabId);
                } else {
                    return this.getDefaultXIfNotExists(axisOptions, ['WEEKS_SINCE_FIRST_TREATMENT'], newApproachTab, tabId);
                }
        }
    }

    static initialY(tabId: TabId, axisOptions: any): string | GroupBySetting {
        const newApproachTab = NEW_APPROACH_TAB_LIST.contains(<string>tabId);
        switch (tabId) {
            case TabId.CARDIAC_BOXPLOT:
            case TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT:
            case TabId.LAB_LINEPLOT:
            case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
            case TabId.LUNG_FUNCTION_BOXPLOT:
            case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
            case TabId.SINGLE_SUBJECT_VITALS_LINEPLOT:
            case TabId.LAB_BOXPLOT:
            case TabId.RENAL_LABS_BOXPLOT:
            case TabId.SINGLE_SUBJECT_LUNG_LINEPLOT:
            // case TabId.POPULATION_BARCHART:
            //     return <GroupBySetting>this.getDefaultXIfNotExists(axisOptions, ['COUNT_OF_SUBJECTS'], true, tabId);
            case TabId.VITALS_BOXPLOT:
                if (newApproachTab) {
                    return <GroupBySetting>this.getDefaultXIfNotExists(axisOptions, ['ACTUAL_VALUE'], true, tabId);
                } else {
                    return this.getDefaultYIfNotExists(<string[]>axisOptions, ['ACTUAL_VALUE']);
                }
            case TabId.AES_COUNTS_BARCHART:
            case TabId.CEREBROVASCULAR_COUNTS:
            case TabId.CVOT_ENDPOINTS_COUNTS:
            case TabId.CI_EVENT_COUNTS:
                if (newApproachTab) {
                    return <GroupBySetting>this.getDefaultXIfNotExists(axisOptions, ['COUNT_OF_SUBJECTS'], true, tabId);
                } else {
                    return this.getDefaultYIfNotExists(<string[]>axisOptions, ['COUNT_OF_SUBJECTS']);
                }
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
                return <GroupBySetting>this.getDefaultXIfNotExists(axisOptions, ['BEST_CHANGE'], true, tabId);
            case TabId.TL_DIAMETERS_PLOT:
            case TabId.TL_DIAMETERS_PER_SUBJECT_PLOT:
                return <GroupBySetting>this.getDefaultXIfNotExists(axisOptions, ['PERCENTAGE_CHANGE'], true, tabId);
            case TabId.DOSE_PROPORTIONALITY_BOX_PLOT:
            case TabId.PK_RESULT_OVERALL_RESPONSE:
                return <GroupBySetting>this.getDefaultYWithTrellisingParameter(axisOptions);
            default:
                if (newApproachTab) {
                    return <any>omit(generateAvailableOptions(fromJS(axisOptions), tabId)[0], 'displayedOption');
                } else {
                    return axisOptions[0];
                }
        }
    }

    private static getDefaultYIfNotExists(axisOptions: string[] = [], defaults: string[]): string {
        if (axisOptions.length) {
            const defaultOption: string[] = intersection(axisOptions, defaults);
            return defaultOption[0] ? defaultOption[0] : axisOptions[0];
        }
        return defaults[0];
    }

    private static getDefaultYWithTrellisingParameter(axisOptions: any): any {
        const availableOptions = generateAvailableOptionsWithTrellising(fromJS(axisOptions.options));
        const defaultOption = availableOptions.first()
            ? availableOptions.first().displayedOption
            : NO_AVAILABLE_PARAMETERS;

        let defaultTimepoints: any = NO_AVAILABLE_PARAMETERS;
        const timepoints = generateAvailableTimepointOptions(fromJS(axisOptions.options), defaultOption);
        if (timepoints) {
            if (timepoints.keys().next().value === TimepointConstants.CYCLE_DAY) {
                //for instance, {'CYCLE_DAY': {Cycle 1: List(1, 5, 44), Cylce 2: List(5, 7, 10)}}
                //get first cycle
                const key = timepoints.first().keys().next().value;
                //create default array [first cycle: firts day]
                defaultTimepoints = [key, timepoints.first().get(key).first()];
            } else {
                //for instance, {'VISIT': List(1, 5, 44)}
                //get List of visits or visit numbers then get the first one
                defaultTimepoints = timepoints.first().first();
            }
        }
        const trellisingParams = {
            [axisOptions.options[0].groupByOption.trellisedBy]: defaultOption,
            [axisOptions.options[0].groupByOption.timepointTrellisedBy]: defaultTimepoints
        };
        return {groupByOption: defaultOption, params: {trellisingParams}};
    }

    private static getDefaultXIfNotExists(axisOptions: any[] | XAxisOptions,
                                          defaults: string[],
                                          newApproachTab = false,
                                          tabId?: TabId): DynamicAxis | GroupBySetting {
        const defaultOptions: any[] = [];
        if (isEmpty(axisOptions)) {
            if (!newApproachTab) {
                return <DynamicAxis>{'value': defaults[0], intarg: null, stringarg: null};
            } else {
                return <GroupBySetting>{groupByOption: defaults[0], params: null};
            }
        } else {
            if (!newApproachTab) {
                defaults.forEach((option) => {
                    const xAxisOption = find(<any>axisOptions, (axisoption: any) => {
                        return axisoption.value === option;
                    });
                    if (xAxisOption) {
                        defaultOptions.push(xAxisOption);
                    }
                });
                return defaultOptions[0] ? defaultOptions[0] : axisOptions[0];
            } else {
                if (defaults.indexOf('WEEKS_SINCE_FIRST_TREATMENT') !== -1) {
                    defaults[defaults.indexOf('WEEKS_SINCE_FIRST_TREATMENT')] = 'WEEKS_SINCE_FIRST_DOSE';
                }

                const availableOptions = generateAvailableOptions(fromJS(axisOptions), tabId);
                const defaultOption = find(availableOptions, (option) => {
                    return defaults.indexOf(option.displayedOption) !== -1;
                });
                return defaultOption
                    ? <any>omit(defaultOption, 'displayedOption')
                    : <any>omit(availableOptions[0], 'displayedOption');
            }
        }
    }
}
