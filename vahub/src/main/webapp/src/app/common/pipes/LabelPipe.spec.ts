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

import {LabelPipe} from './LabelPipe';
import {TabId} from '../trellising/index';

describe('Given SentenceCasePipe', () => {
    let pipe: LabelPipe;
    beforeEach(() => {
        pipe = new LabelPipe();
    });

    it('transforms "STUDY_DEFINED_WEEK" to "ANALYSIS_VISIT"', () => {
        expect(pipe.transform('STUDY_DEFINED_WEEK')).toEqual('ANALYSIS_VISIT');
    });

    it('transforms "VISIT_NUMBER" to "VISIT_NUMBER"', () => {
        expect(pipe.transform('VISIT_NUMBER')).toEqual('VISIT_NUMBER');
    });

    it('transforms "NONE" to "ALL"', () => {
        expect(pipe.transform('NONE')).toEqual('ALL');
    });

    it('transforms "COUNT_INCLUDING_DURATION" to "EVENT_COUNT_(INCL._DURATION)"', () => {
        expect(pipe.transform('COUNT_INCLUDING_DURATION')).toEqual('EVENT_COUNT_(INCL._DURATION)');
    });

    it('transforms "COUNT_START_DATES_ONLY" to "EVENT_COUNT_(START_DATES_ONLY)"', () => {
        expect(pipe.transform('COUNT_START_DATES_ONLY')).toEqual('EVENT_COUNT_(START_DATES_ONLY)');
    });

    it('transforms "COUNT_INCLUDING_DURATION" to "AE_COUNT__(INCL._DURATION)"', () => {
        expect(pipe.transform('COUNT_INCLUDING_DURATION', TabId.AES_OVER_TIME)).toEqual('AE_COUNT_(INCL._DURATION)');
    });

    it('transforms "COUNT_INCLUDING_DURATION" to "EVENT_COUNT_(INCL._DURATION)"', () => {
        expect(pipe.transform('COUNT_INCLUDING_DURATION', TabId.EXACERBATIONS_OVER_TIME)).toEqual('EXACERBATIONS_COUNT_(INCL._DURATION)');
    });

    it('transforms "COUNT_START_DATES_ONLY" to "AE_COUNT_(START_DATES_ONLY)"', () => {
        expect(pipe.transform('COUNT_START_DATES_ONLY', TabId.AES_OVER_TIME)).toEqual('AE_COUNT_(START_DATES_ONLY)');
    });

    it('transforms "COUNT_START_DATES_ONLY" to "EXACERBATIONS_COUNT_(START_DATES_ONLY)"', () => {
        expect(pipe.transform('COUNT_START_DATES_ONLY', TabId.EXACERBATIONS_OVER_TIME)).toEqual('EXACERBATIONS_COUNT_(START_DATES_ONLY)');
    });

    it('transforms event counts in exacerbations to being context specific', () => {
        expect(pipe.transform('COUNT_OF_EVENTS', TabId.EXACERBATIONS_COUNTS)).toEqual('EXACERBATIONS_(COUNT)');
        expect(pipe.transform('PERCENTAGE_OF_ALL_EVENTS', TabId.EXACERBATIONS_COUNTS)).toEqual('EXACERBATIONS_(PERCENTAGE_OF_TOTAL)');
        expect(pipe.transform('PERCENTAGE_OF_EVENTS_WITHIN_PLOT', TabId.EXACERBATIONS_COUNTS))
            .toEqual('EXACERBATIONS_(PERCENTAGE_WITHIN_PLOT)');
        expect(pipe.transform('COUNT_OF_EVENTS', TabId.EXACERBATIONS_GROUPED_COUNTS)).toEqual('EXACERBATIONS_(COUNT)');
        expect(pipe.transform('PERCENTAGE_OF_ALL_EVENTS', TabId.EXACERBATIONS_GROUPED_COUNTS))
            .toEqual('EXACERBATIONS_(PERCENTAGE_OF_TOTAL)');
        expect(pipe.transform('PERCENTAGE_OF_EVENTS_WITHIN_PLOT', TabId.EXACERBATIONS_GROUPED_COUNTS))
            .toEqual('EXACERBATIONS_(PERCENTAGE_WITHIN_PLOT)');
    });

    it('transforms event counts in exacerbations to being context specific', () => {
        expect(pipe.transform('COUNT_OF_EVENTS', TabId.AES_COUNTS_BARCHART)).toEqual('ADVERSE_EVENTS_(COUNT)');
        expect(pipe.transform('PERCENTAGE_OF_ALL_EVENTS', TabId.AES_COUNTS_BARCHART)).toEqual('ADVERSE_EVENTS_(PERCENTAGE_OF_TOTAL)');
        expect(pipe.transform('PERCENTAGE_OF_EVENTS_WITHIN_PLOT', TabId.AES_COUNTS_BARCHART))
            .toEqual('ADVERSE_EVENTS_(PERCENTAGE_WITHIN_PLOT)');
    });
    it('transforms "ACTUAL_VALUE" to "RESULT_VALUE"', () => {
        expect(pipe.transform('ACTUAL_VALUE')).toEqual('RESULT_VALUE');
    });
    it('transforms "EVENTS" to "ADVERSE_EVENTS" for AEs Counts tab', () => {
        expect(pipe.transform('EVENTS', TabId.AES_COUNTS_BARCHART)).toEqual('ADVERSE_EVENTS');
    });
    it('transforms "EVENTS" to "EVENTS" for other tabs', () => {
        expect(pipe.transform('EVENTS')).toEqual('EVENTS');
    });
    it('transforms "WEEKS_SINCE_FIRST_DOSE" to "WEEKS_SINCE_FIRST_TREATMENT"', () => {
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.LAB_BOXPLOT)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.SINGLE_SUBJECT_LAB_LINEPLOT)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.LAB_LINEPLOT)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.AES_OVER_TIME)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.LUNG_FUNCTION_BOXPLOT)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.RENAL_LABS_BOXPLOT)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.RENAL_CKD_BARCHART)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.EXACERBATIONS_OVER_TIME)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.EXACERBATIONS_COUNTS)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.SINGLE_SUBJECT_LUNG_LINEPLOT)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.CARDIAC_BOXPLOT)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE', TabId.SINGLE_SUBJECT_RENAL_LINEPLOT)).toEqual('WEEKS_SINCE_FIRST_TREATMENT');
    });
    it('doesnt transform "WEEKS_SINCE_FIRST_DOSE" to "WEEKS_SINCE_FIRST_TREATMENT" for other tabs', () => {
        expect(pipe.transform('WEEKS_SINCE_FIRST_DOSE')).toEqual('WEEKS_SINCE_FIRST_DOSE');
    });
    it('transforms "DAYS_SINCE_FIRST_DOSE" to "DAYS_SINCE_FIRST_TREATMENT"', () => {
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.LAB_BOXPLOT)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.SINGLE_SUBJECT_LAB_LINEPLOT)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.LAB_LINEPLOT)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.AES_OVER_TIME)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.LUNG_FUNCTION_BOXPLOT)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.RENAL_LABS_BOXPLOT)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.RENAL_CKD_BARCHART)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.EXACERBATIONS_OVER_TIME)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.EXACERBATIONS_COUNTS)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.SINGLE_SUBJECT_LUNG_LINEPLOT)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.CARDIAC_BOXPLOT)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE', TabId.SINGLE_SUBJECT_RENAL_LINEPLOT)).toEqual('DAYS_SINCE_FIRST_TREATMENT');
    });
    it('doesnt transform "DAYS_SINCE_FIRST_DOSE" to "DAYS_SINCE_FIRST_TREATMENT" for other tabs', () => {
        expect(pipe.transform('DAYS_SINCE_FIRST_DOSE')).toEqual('DAYS_SINCE_FIRST_DOSE');
    });
});
