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
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class CIEventsColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study ID'},
        {field: 'studyPart', headerName: 'Study Part'},
        {field: 'subjectId', headerName: 'Subject ID'},
        {field: 'finalDiagnosis', enableRowGroup: true, headerName: 'Final Diagnosis'},
        {field: 'otherDiagnosis', enableRowGroup: true, headerName: 'Specify Other Diagnosis'},
        {field: 'startDate', enableRowGroup: true, headerName: 'Event Start Date'},
        {field: 'startTime', enableRowGroup: true, headerName: 'Event Start Time'},
        {field: 'term', enableRowGroup: true, headerName: 'Event Term'},
        {field: 'aeNumber', enableRowGroup: true, enableValue: true, headerName: 'Associated AE No.'},
        {field: 'ischemicSymptoms', enableRowGroup: true, headerName: 'Ischemic Symptoms'},
        {field: 'cieSymptomsDuration', enableRowGroup: true, enableValue: true, headerName: 'Duration of CIE symptoms'},
        {field: 'symptPromptUnschedHospit', enableRowGroup: true, headerName: 'Did the Symptoms Prompt an Uns. Hosp.'},
        {field: 'eventSuspDueToStentThromb', enableRowGroup: true, headerName: 'Event Susp. to be Due to Stent Thromb.'},
        {field: 'previousEcgAvailable', enableRowGroup: true, headerName: 'Previous ECG Before Event Available'},
        {field: 'previousEcgDate', enableRowGroup: true, headerName: 'Date of Previous ECG'},
        {field: 'ecgAtTheEventTime', enableRowGroup: true, headerName: 'ECG at the Time of the Event'},
        {field: 'noEcgAtTheEventTime', enableRowGroup: true, headerName: 'If no ECG at the Time of Event, Specify'},
        {field: 'localCardiacBiomarkersDrawn', enableRowGroup: true, headerName: 'Were Local Cardiac Biomarkers Drawn'},
        {field: 'coronaryAngiography', enableRowGroup: true, headerName: 'Coronary Angiography Performed'},
        {field: 'angiographyDate', enableRowGroup: true, headerName: 'Date of Angiography'},
        {field: 'description1', enableRowGroup: true, headerName: 'Event description 1'},
        {field: 'description2', enableRowGroup: true, headerName: 'Event description 2'},
        {field: 'description3', enableRowGroup: true, headerName: 'Event description 3'},
        {field: 'description4', enableRowGroup: true, headerName: 'Event description 4'},
        {field: 'description5', enableRowGroup: true, headerName: 'Event description 5'}
    ]);
}
