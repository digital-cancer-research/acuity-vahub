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
export class MedicalHistoryColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study id'},
        {field: 'studyPart', headerName: 'Study part'},
        {field: 'subjectId', headerName: 'Subject id'},
        {field: 'category', enableRowGroup: true, headerName: 'Medical history category'},
        {field: 'term', enableRowGroup: true, headerName: 'Medical history term'},
        {field: 'conditionStatus', enableRowGroup: true, headerName: 'Condition status'},
        {field: 'currentMedication', enableRowGroup: true, headerName: 'Current medication'},
        {field: 'start', enableRowGroup: true, headerName: 'Start date'},
        {field: 'end', enableRowGroup: true, headerName: 'End date'},
        {field: 'preferredTerm', enableRowGroup: true, headerName: 'PT name'},
        {field: 'hlt', enableRowGroup: true, headerName: 'HLT name'},
        {field: 'soc', enableRowGroup: true, headerName: 'SOC name'}
    ]);
}
