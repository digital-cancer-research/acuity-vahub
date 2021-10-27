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
export class VitalsColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'measurementName', enableRowGroup: true},
        {field: 'measurementTimePoint'},
        {field: 'daysOnStudy', enableValue: true},
        {field: 'analysisVisit', enableValue: true},
        {field: 'visitNumber', enableValue: true},
        {field: 'scheduleTimepoint', enableRowGroup: true},
        {field: 'resultValue', enableValue: true},
        {field: 'resultUnit', enableRowGroup: true},
        {field: 'baselineValue', enableValue: true},
        {field: 'changeFromBaseline', enableValue: true},
        {field: 'percentChangeFromBaseline', enableValue: true},
        {field: 'baselineFlag', enableRowGroup: true},
        {field: 'lastDoseDate', enableRowGroup: true},
        {field: 'lastDoseAmount', enableValue: true},
        {field: 'anatomicalLocation', enableRowGroup: true},
        {field: 'sideOfInterest', enableRowGroup: true},
        {field: 'physicalPosition', enableRowGroup: true},
        {field: 'clinicallySignificant', enableRowGroup: true}
    ]);
}
