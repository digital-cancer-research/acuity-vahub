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
export class LiverRiskFactorsColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'potentialHysLawCaseNum', enableValue: true, headerName: 'Potential Hy\'s law case number'},
        {field: 'value', enableRowGroup: true, headerName: 'Liver risk factor'},
        {field: 'occurrence', enableRowGroup: true, headerName: 'Liver risk factor occurrence'},
        {field: 'referencePeriod', enableRowGroup: true, headerName: 'Liver risk factor reference period'},
        {field: 'details', enableRowGroup: true, headerName: 'Liver risk factor details'},
        {field: 'startDate', enableRowGroup: true, headerName: 'Start date'},
        {field: 'stopDate', enableRowGroup: true, headerName: 'Stop date'},
        {field: 'studyDayAtStart', enableValue: true, headerName: 'Study day at liver risk factor start'},
        {field: 'studyDayAtStop', enableValue: true, headerName: 'Study day at liver risk factor stop'},
        {field: 'comment', enableRowGroup: true, headerName: 'Liver risk factor comment'},
    ]);
}
