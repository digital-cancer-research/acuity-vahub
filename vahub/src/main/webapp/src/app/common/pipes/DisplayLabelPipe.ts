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

import {TabId} from '../trellising/index';

/**
 * Transform backend values to presentation values
 * Example:
 * {{ NONE | toDisplayLabel }}
 * formats to: ALL
 */

@Pipe({name: 'toDisplayLabel'})
export class DisplayLabelPipe implements PipeTransform {
    transform(value: string, tabId?: TabId): string {
        switch (value) {
            case 'NONE':
                return 'ALL';
            case 'STUDY_CODE':
                return 'STUDY_ID';
            case 'STUDY_DEFINED_WEEK':
                return 'ANALYSIS_VISIT';
            case 'MAX_SEVERITY_GRADE':
                return 'MAXIMUM_EXPERIENCED_SEVERITY_GRADE';
            case 'COUNT_INCLUDING_DURATION':
                switch (tabId) {
                    case (TabId.AES_OVER_TIME):
                        return 'AE_COUNT_(INCL._DURATION)';
                    case (TabId.EXACERBATIONS_OVER_TIME):
                        return 'EXACERBATIONS_COUNT_(INCL._DURATION)';
                    default:
                        return 'EVENT_COUNT_(INCL._DURATION)';
                }
            case 'COUNT_START_DATES_ONLY':
                switch (tabId) {
                    case (TabId.AES_OVER_TIME):
                        return 'AE_COUNT_(START_DATES_ONLY)';
                    case (TabId.CEREBROVASCULAR_EVENTS_OVER_TIME):
                        return 'CEREBROVASCULAR_EVENT_COUNT_(START_DATES_ONLY)';
                    case (TabId.EXACERBATIONS_OVER_TIME):
                        return 'EXACERBATIONS_COUNT_(START_DATES_ONLY)';
                    default:
                        return 'EVENT_COUNT_(START_DATES_ONLY)';
                }
            case 'COUNT_OF_SUBJECTS':
                return 'SUBJECTS_(COUNT)';
            case 'PERCENTAGE_OF_ALL_SUBJECTS':
                return 'SUBJECTS_(PERCENTAGE_OF_TOTAL)';
            case 'PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT':
                return 'SUBJECTS_(PERCENTAGE_WITHIN_PLOT)';
            case 'PERCENTAGE_OF_EVENTS_100_STACKED':
                switch (tabId) {
                    case (TabId.POPULATION_TABLE):
                        return 'PERCENT_OF_SUBJECTS_BY_COLUMN';
                    default:
                        return 'PERCENTAGE_OF_EVENTS_100_STACKED';
                }
            case 'COUNT_OF_EVENTS':
                switch (tabId) {
                    case (TabId.EXACERBATIONS_GROUPED_COUNTS):
                        return 'EXACERBATIONS_(COUNT)';
                    case (TabId.AES_COUNTS_BARCHART):
                        return 'ADVERSE_EVENTS_(COUNT)';
                    case (TabId.CVOT_ENDPOINTS_COUNTS):
                        return 'CVOT_(COUNT)';
                    default:
                        return 'EVENT_COUNT';
                }
            case 'PERCENTAGE_OF_ALL_EVENTS':
                switch (tabId) {
                    case (TabId.EXACERBATIONS_GROUPED_COUNTS):
                        return 'EXACERBATIONS_(PERCENTAGE_OF_TOTAL)';
                    case (TabId.AES_COUNTS_BARCHART):
                        return 'ADVERSE_EVENTS_(PERCENTAGE_OF_TOTAL)';
                    default:
                        return 'EVENTS_(PERCENTAGE_OF_TOTAL)';
                }
            case 'PERCENTAGE_OF_EVENTS_WITHIN_PLOT':
                switch (tabId) {
                    case (TabId.EXACERBATIONS_GROUPED_COUNTS):
                        return 'EXACERBATIONS_(PERCENTAGE_WITHIN_PLOT)';
                    case (TabId.AES_COUNTS_BARCHART):
                        return 'ADVERSE_EVENTS_(PERCENTAGE_WITHIN_PLOT)';
                    default:
                        return 'EVENTS_(PERCENTAGE_WITHIN_PLOT)';
                }
            case 'CUMULATIVE_COUNT_OF_SUBJECTS':
                return 'CUMULATIVE_SUBJECT_COUNT';
            case 'CUMULATIVE_COUNT_OF_EVENTS':
                return 'CUMULATIVE_EVENT_COUNT';
            case 'REF_RANGE_NORM_VALUE':
                return 'REF_RANGE_NORM._VALUE';
            case 'TIMES_UPPER_REF_VALUE':
                return 'TIMES_UPPER_REF._VALUE';
            case 'TIMES_LOWER_REF_VALUE':
                return 'TIMES_LOWER_REF._VALUE';
            case 'ACTUAL_VALUE':
                return 'RESULT_VALUE';
            case 'DATE':
                return 'START_DATE';
            case 'WEEKS_SINCE_FIRST_DOSE':
                switch (tabId) {
                    case (TabId.SINGLE_SUBJECT_LAB_LINEPLOT):
                    case (TabId.SINGLE_SUBJECT_LUNG_LINEPLOT):
                    case (TabId.SINGLE_SUBJECT_VITALS_LINEPLOT):
                    case (TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT):
                    case (TabId.SINGLE_SUBJECT_RENAL_LINEPLOT):
                    case (TabId.LAB_LINEPLOT):
                    case (TabId.LAB_BOXPLOT):
                    case (TabId.AES_OVER_TIME):
                    case (TabId.EXACERBATIONS_OVER_TIME):
                    case (TabId.LUNG_FUNCTION_BOXPLOT):
                    case (TabId.VITALS_BOXPLOT):
                    case (TabId.CARDIAC_BOXPLOT):
                    case (TabId.RENAL_LABS_BOXPLOT):
                    case (TabId.RENAL_CKD_BARCHART):
                        return 'WEEKS_SINCE_FIRST_TREATMENT';
                    default:
                        return 'WEEKS_SINCE_FIRST_DOSE';
                }
            case 'DAYS_SINCE_FIRST_DOSE':
                switch (tabId) {
                    case (TabId.SINGLE_SUBJECT_LAB_LINEPLOT):
                    case (TabId.SINGLE_SUBJECT_LUNG_LINEPLOT):
                    case (TabId.SINGLE_SUBJECT_VITALS_LINEPLOT):
                    case (TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT):
                    case (TabId.SINGLE_SUBJECT_RENAL_LINEPLOT):
                    case (TabId.LAB_LINEPLOT):
                    case (TabId.LAB_BOXPLOT):
                    case (TabId.AES_OVER_TIME):
                    case (TabId.EXACERBATIONS_OVER_TIME):
                    case (TabId.LUNG_FUNCTION_BOXPLOT):
                    case (TabId.VITALS_BOXPLOT):
                    case (TabId.CARDIAC_BOXPLOT):
                    case (TabId.RENAL_LABS_BOXPLOT):
                    case (TabId.RENAL_CKD_BARCHART):
                        return 'DAYS_SINCE_FIRST_TREATMENT';
                    default:
                        return 'DAYS_SINCE_FIRST_DOSE';
                }
            case 'EVENTS':
                switch (tabId) {
                    case (TabId.AES_COUNTS_BARCHART):
                        return 'ADVERSE_EVENTS';
                    default:
                        return 'EVENTS';
                }
            case 'SUM_DIAMETERS':
                return 'SUM_DIAMETERS';
            case 'SUM_PERCENTAGE_CHANGE_FROM_BASELINE':
                return 'SUM_PERCENTAGE_CHANGE_FROM_BASELINE';
            case 'DIAMETER':
                return 'DIAMETER';
            case 'DAYS_ON_STUDY':
                return 'DAYS_ON_STUDY';
            default:
                return value;
        }
    }
}
