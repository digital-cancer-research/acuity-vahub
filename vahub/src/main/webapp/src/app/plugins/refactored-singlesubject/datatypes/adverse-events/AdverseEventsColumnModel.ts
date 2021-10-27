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
export class AdverseEventsColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'preferredTerm', enableRowGroup: true},
        {field: 'highLevelTerm', enableRowGroup: true},
        {field: 'systemOrganClass', enableRowGroup: true},
        {field: 'specialInterestGroup', enableRowGroup: true},
        {field: 'maxSeverity', enableRowGroup: true},
        {field: 'startDate', enableRowGroup: true},
        {field: 'endDate', enableRowGroup: true},
        {field: 'daysOnStudyAtAEStart', enableRowGroup: true},
        {field: 'daysOnStudyAtAEEnd', enableRowGroup: true},
        {field: 'duration'},
        {field: 'daysFromPreviousDoseToAEStart', enableRowGroup: true},
        {field: 'serious', enableRowGroup: true},
        {field: 'actionTaken', enableRowGroup: true},
        {field: 'requiresOrProlongsHospitalisation', enableRowGroup: true},
        {field: 'treatmentEmergent', enableRowGroup: true},
        {field: 'causality', enableRowGroup: true},
        {field: 'description'},
        {field: 'comment'},
        {field: 'outcome'},
        {field: 'requiredTreatment', enableRowGroup: true},
        {field: 'causedSubjectWithdrawal', enableRowGroup: true},
        {field: 'doseLimitingToxicity', enableRowGroup: true},
        {field: 'timePointOfDoseLimitingToxicity'},
        {field: 'immuneMediatedAE', enableRowGroup: true},
        {field: 'infusionReactionAE', enableRowGroup: true},
        {field: 'aeOfSpecialInterest', enableRowGroup: true, headerName: 'Ae of special interest'},
    ]);
}
