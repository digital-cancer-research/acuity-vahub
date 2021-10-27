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
import {List} from 'immutable';
import {ColDef} from 'ag-grid';

@Injectable()
export class QTProlongationColumnModel {

    get columnDefs(): List<ColDef> {
        return this._columnDefs;
    }

    protected _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'measurementCategory', enableRowGroup: true},
        {field: 'measurementName', enableRowGroup: true},
        {field: 'measurementTimePoint', enableRowGroup: true},
        {field: 'daysOnStudy', enableValue: true},
        {field: 'visitNumber', enableValue: true},
        {field: 'resultValue', enableValue: true},
        {field: 'alertLevel', enableValue: true},
        {
            headerName: 'Device',
            children: [
                {field: 'sourceName', columnGroupShow: 'open', enableValue: true},
                {field: 'sourceVersion', columnGroupShow: 'open', enableValue: true},
                {field: 'sourceType', columnGroupShow: 'open', enableValue: true}
            ]
        }
    ]);
}
