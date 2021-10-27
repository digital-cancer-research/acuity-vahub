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

import {ColGroupDef} from 'ag-grid/main';

export class SubjectTableColumns {
    public static getColumnGroupDefs(): ColGroupDef[] {
        return [
            {
                headerName: 'Studies, parts and subjects',
                children: [
                    {headerName: 'Study id', field: 'studyId', columnGroupShow: 'open'},
                    {headerName: 'Study part', field: 'studyPart', columnGroupShow: 'open'},
                    {headerName: 'Subject id', field: 'subjectId', columnGroupShow: 'open'},
                ]
            },
            {
                headerName: 'Subject status',
                children: [
                    {headerName: 'Duration on study', field: 'durationOnStudy', columnGroupShow: 'open'},
                    {headerName: 'Randomised', field: 'randomised', columnGroupShow: 'open'},
                    {headerName: 'Date of randomisation', field: 'dateOfRandomisation', columnGroupShow: 'open'},
                    {headerName: 'First treatment date', field: 'firstTreatmentDate', columnGroupShow: 'open'},
                    {headerName: 'Withdrawal/Completion', field: 'withdrawal', columnGroupShow: 'open'},
                    {headerName: 'Date of withdrawal/completion', field: 'dateOfWithdrawal', columnGroupShow: 'open'},
                    {headerName: 'Main reason for withdrawal/completion', field: 'reasonForWithdrawal', columnGroupShow: 'open'},
                    {headerName: 'Death', field: 'deathFlag', columnGroupShow: 'open'},
                    {headerName: 'Date of death', field: 'dateOfDeath', columnGroupShow: 'open'}
                ]
            },
            {
                headerName: 'Arms, cohorts and groups',
                children: [
                    {headerName: 'Planned arm', field: 'plannedArm', columnGroupShow: 'open'},
                    {headerName: 'Actual arm', field: 'actualArm', columnGroupShow: 'open'},
                    {headerName: 'Cohort (other)', field: 'otherCohort', columnGroupShow: 'open'},
                    {headerName: 'Cohort (dose)', field: 'doseCohort', columnGroupShow: 'open'}
                ]
            },
            {
                headerName: 'Study specific filters',
                children: [] // Will get dynamically added
            },
            {
                headerName: 'Dose details',
                children: [] // Will get dynamically added
            },
            {
                headerName: 'Demography',
                children: [
                    {headerName: 'Sex', field: 'sex', columnGroupShow: 'open'},
                    {headerName: 'Race', field: 'race', columnGroupShow: 'open'},
                    {headerName: 'Ethnic group', field: 'ethnicGroup', columnGroupShow: 'open'},
                    {headerName: 'Age', field: 'age', columnGroupShow: 'open'},
                    {headerName: 'Centre', field: 'centerNumber', columnGroupShow: 'open'},
                    {headerName: 'Region', field: 'region', columnGroupShow: 'open'},
                    {headerName: 'Country', field: 'country', columnGroupShow: 'open'}
                ]
            },
            {
                headerName: 'Histories',
                children: [
                    {headerName: 'Medical histories', field: 'medicalHistories', columnGroupShow: 'open'}
                ]
            }
        ];
    }
}
