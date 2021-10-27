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
export class NicotineColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study id'},
        {field: 'studyPart', headerName: 'Study part'},
        {field: 'subjectId', headerName: 'Subject id'},
        {field: 'category', enableRowGroup: true, headerName: 'Substance Category'},
        {field: 'useOccurrence', enableRowGroup: true, headerName: 'Substance Use Occurrence'},
        {field: 'type', enableRowGroup: true, headerName: 'Substance Type'},
        {field: 'otherTypeSpec', enableRowGroup: true, headerName: 'Other Substance Type Specification'},
        {field: 'subTypeUseOccurrence', enableRowGroup: true, headerName: 'Substance Type Use Occurrence'},
        {field: 'currentUseSpec', enableRowGroup: true, headerName: 'Current Substance Use Specification'},
        {field: 'startDate', enableRowGroup: true, headerName: 'Substance Use Start Date'},
        {field: 'endDate', enableRowGroup: true, headerName: 'Substance Use End Date'},
        {field: 'consumption', headerName: 'Substance Consumption', enableValue: true},
        {field: 'frequencyInterval', enableRowGroup: true, headerName: 'Substance Use Frequency Interval'},
        {field: 'numberPackYears', headerName: 'Number of Pack Years', enableValue: true}
    ]);
}
