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
export class LiverDiagnosticInvestigationColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'liverDiagInv', headerName: 'Liver diagnostic investigation', enableRowGroup: true},
        {field: 'liverDiagInvSpec', headerName: 'Liver diagnostic investigation specification', enableRowGroup: true},
        {field: 'liverDiagInvDate', headerName: 'Liver diagnostic investigation date', enableRowGroup: true},
        {field: 'studyDayLiverDiagInv', headerName: 'Study day at liver diagnostic investigation', enableValue: true},
        {field: 'liverDiagInvResult', headerName: 'Liver diagnostic investigation results', enableRowGroup: true},
        {field: 'potentialHysLawCaseNum', headerName: 'Potential Hy\'s law case number', enableValue: true}
    ]);
}
