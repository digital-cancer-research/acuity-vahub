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

import {SentenceCasePipe} from './SentenceCasePipe';

describe('Given SentenceCasePipe', () => {
    let pipe: SentenceCasePipe;
    beforeEach(() => {
        pipe = new SentenceCasePipe();
    });

    it('transforms "SUBJECT_ID" to "Subject ID"', () => {
        expect(pipe.transform('SUBJECT_ID')).toEqual('Subject ID');
    });

    it('transforms "RACE" to "Race"', () => {
        expect(pipe.transform('RACE')).toEqual('Race');
    });

    it('transforms "AE_COUNT" to "AE count"', () => {
        expect(pipe.transform('AE_COUNT')).toEqual('AE count');
    });

    it('transforms "ACTUAL_ARM" to "Actual arm"', () => {
        expect(pipe.transform('ACTUAL_ARM')).toEqual('Actual arm');
    });

    it('transforms "PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED" to "Percentage of subjects, 100% stacked"', () => {
        expect(pipe.transform('PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED')).toEqual('Percentage of subjects, 100% stacked');
    });

    it('transforms "WITHDRAWAL" to "Withdrawal/Completion"', () => {
        expect(pipe.transform('WITHDRAWAL')).toEqual('Withdrawal/Completion');
    });

    it('transforms "MAX_DOSE_PER_ADMIN_OF_DRUG_STDY4321" to "Max. dose per admin STDY4321"', () => {
        expect(pipe.transform('MAX_DOSE_PER_ADMIN_OF_DRUG_STDY4321')).toEqual('Max. dose per admin STDY4321');
    });
});
