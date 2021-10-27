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
import {isUndefined, capitalize} from 'lodash';
import {TabId} from '../trellising/index';
import {knownAcronyms} from '../utils/Utils';

/**
 * Transform uppercase snake case into sentence case
 * Example:
 * {{ SUBJECT_ID | sentenceCase }}
 * formats to: Subject ID
 */

@Pipe({name: 'sentenceCase'})
export class SentenceCasePipe implements PipeTransform {

    private knownExceptions: Map<string, string> = new Map([
        ['PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED', 'Percentage of subjects, 100% stacked'],
        ['WITHDRAWAL', 'Withdrawal/Completion'],
        ['FINAL_DIAGNOSIS', 'Final Diagnosis'],
        ['ISHEMIC_SYMTOMS', 'Ischemic Symptoms'],
        ['CI_SYMPTOMS_DURATION', 'Duration of CIE Symptoms'],
        ['DID_SYMPTOMS_PROMPT_UNS_HOSP', 'Did the Symptoms Prompt an Uns. Hosp'],
        ['EVENT_SUSP_DUE_TO_STENT_THROMB', 'Event Susp. to be Due to Stent Thromb.'],
        ['PREVIOUS_ECG_AVAILABLE', 'Previous ECG Before Event Available'],
        ['ECG_AT_THE_EVENT_TIME', 'ECG at the Time of the Event'],
        ['WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN', 'Were Local Cardiac Biomarkers Drawn'],
        ['CORONARY_ANGIOGRAPHY', 'Coronary Angiography Performed'],
        ['EVENT_TYPE', 'Type of Event'],
        ['PRIMARY_ISCHEMIC_STROKE', 'If Primary Ischemic Stroke'],
        ['TRAUMATIC', 'If Traumatic'],
        ['INTRA_HEMORRHAGE_LOC', 'Loc. of Primary Intracranial Hemorrhage'],
        ['SYMPTOMS_DURATION', 'Duration of Symptoms'],
        ['MRS_PRIOR_STROKE', 'MRS Prior to Stroke'],
        ['MRS_DURING_STROKE_HOSP', 'MRS During Stroke Hospitalisation'],
        ['MRS_CURR_VISIT_OR_90D_AFTER', 'MRS at Current Visit or 90D After Stroke'],
        ['CATEGORY_1', 'Event Category 1'],
        ['CATEGORY_2', 'Event Category 2'],
        ['CATEGORY_3', 'Event Category 3'],
        ['DESCRIPTION_1', 'Event Description 1'],
        ['DESCRIPTION_2', 'Event Description 2'],
        ['DESCRIPTION_3', 'Event Description 3'],
        ['ARM', 'Actual study arm'],
        ['ABSOLUTE_SUM', 'Sum of target lesion diameters: absolute value (mm)'],
        ['ABSOLUTE_CHANGE', 'Sum of target lesion diameters: absolute change (mm)'],
        ['PERCENTAGE_CHANGE', 'Sum of target lesion diameters: percentage change'],
        ['LESION_PERCENTAGE_CHANGE', '% change in TLD from baseline'],
        ['ASSESSMENT_WEEK_WITH_BASELINE', 'RECIST assessment week'],
        ['DOSE_COHORT', 'Cohort (Dose)'],
        ['OTHER_COHORT', 'Cohort (Other)'],
        ['TIME_FROM_ADMINISTRATION', 'Time from administration (hours)'],
        ['MAX_DOSE_PER_ADMIN_OF_DRUG', 'Max. dose per admin'],
        ['PRIOR_THERAPY', 'Prior therapy description'],
        ['DOSE_PER_VISIT', 'Nominal dose and visit'],
        ['DOSE_PER_CYCLE', 'Nominal dose and cycle'],
        ['DOSE', 'Nominal dose'],
        ['ACTUAL_DOSE', 'Actual administered dose'],
        ['VARIANT_ALLELE_FREQUENCY', 'Variant allele frequency of ctDNA (fraction)'],
        ['VARIANT_ALLELE_FREQUENCY_PERCENT', 'Variant allele frequency of ctDNA (percentage)'],
        ['BEST_RESPONSE', 'Best overall response'],
        ['ASSESSMENT_RESPONSE', 'Overall visit response']

    ]);

    private knownDynamicValues: string[] = ['MAX_DOSE_PER_ADMIN_OF_DRUG'];


    transform(value: string, tabId?: TabId): string {
        if (!value || typeof (value) !== 'string') {
            return;
        }

        // TODO figure out how to get rid of this IF
        if (tabId === TabId.DOSE_PROPORTIONALITY_BOX_PLOT || tabId === TabId.PK_RESULT_OVERALL_RESPONSE) {
            return value;
        }

        if (!isUndefined(this.knownExceptions.get(value))) {
            return this.knownExceptions.get(value);
        }

        //Split the string by underscores
        let stringArray: string[];
        if (value.indexOf('_') > -1) {
            stringArray = value.split('_');
        } else {
            stringArray = value.split(' ');
        }

        // In some plots we have dynamic colorBy options, so they cannot be added to known exceptions or knownAcronyms
        // For example, when dynamic part is a drug, we do not want it to be lowercase
        // This will work for dynamic parts that are in the end of the string. If other cases appear, need to think about better solution
        const dynamicStart = this.knownDynamicValues.find(v => value.startsWith(v));

        if (dynamicStart) {
            const dynamicEnd = value.substr(dynamicStart.length + 1);
            return `${this.knownExceptions.get(dynamicStart)} ${dynamicEnd}`;
        }

        //Uppercase any known acronyms
        return stringArray.map((val, index) => {
            if (knownAcronyms.indexOf(val) > -1
                || (!isUndefined(dynamicStart) && index === stringArray.length - 1)) {
                return val;
            } else {
                return index === 0 ? capitalize(val) : val.toLowerCase();
            }
        }).join(' ');
    }
}
