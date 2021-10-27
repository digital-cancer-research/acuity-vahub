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
export class DeathColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'deathCause', headerName: 'Cause of death', enableRowGroup: true},
        {field: 'dateOfDeath', enableRowGroup: true},
        {field: 'daysFromFirstDoseToDeath', headerName: 'Days from first dose to death', enableValue: true},
        {field: 'autopsyPerformed', enableRowGroup: true},
        {field: 'designation', enableRowGroup: true},
        {field: 'diseaseUnderInvestigationDeath', headerName: 'Death related to disease under investigation', enableRowGroup: true},
        {field: 'hlt', headerName: 'MedDRA HLT', enableRowGroup: true},
        {field: 'llt', headerName: 'MedDRA LLT', enableRowGroup: true},
        {field: 'preferredTerm', headerName: 'MedDRA PT', enableRowGroup: true},
        {field: 'soc', headerName: 'MedDRA SOC', enableRowGroup: true}
    ]);
}
