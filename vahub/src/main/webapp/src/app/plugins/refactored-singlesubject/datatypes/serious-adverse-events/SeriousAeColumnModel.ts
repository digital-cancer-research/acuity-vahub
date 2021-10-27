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
export class SeriousAeColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', enableRowGroup: true, headerName: 'Study id'},
        {field: 'studyPart', enableRowGroup: true, headerName: 'Study part'},
        {field: 'subjectId', enableRowGroup: true, headerName: 'Subject Id'},
        {field: 'num', headerName: 'AE number', enableValue: true},
        {field: 'ae', enableRowGroup: true, headerName: 'Adverse event'},
        {field: 'startDate', enableRowGroup: true, headerName: 'AE start date'},
        {field: 'endDate', enableRowGroup: true, headerName: 'AE end date'},
        {field: 'resultInDeath', enableRowGroup: true, headerName: 'Results in death'},
        {field: 'hospitalizationRequired', enableRowGroup: true, headerName: 'Requires or prolongs hospitalization'},
        {field: 'congenitalAnomaly', enableRowGroup: true, headerName: 'Congenital anomaly or birth defect'},
        {field: 'lifeThreatening', enableRowGroup: true, headerName: 'Life threatening'},
        {field: 'disability', enableRowGroup: true, headerName: 'Persist. or sign. disability/incapacity'},
        {field: 'otherSeriousEvent', enableRowGroup: true, headerName: 'Other medically important serious event'},
        {field: 'hospitalizationDate', enableRowGroup: true, headerName: 'Date of hospitalization'},
        {field: 'dischargeDate', enableRowGroup: true, headerName: 'Date of discharge'},
        {field: 'pt', enableRowGroup: true, headerName: 'Preferred term'},
        {field: 'becomeSeriousDate', enableRowGroup: true, headerName: 'Date AE met criteria for serious AE'},
        {field: 'daysFromFirstDoseToCriteria', headerName: 'Days from first dose to AE met criteria', enableValue: true},
        {field: 'findOutDate', enableRowGroup: true, headerName: 'Date investigator aware of serious AE'},
        {field: 'description', enableRowGroup: true, headerName: 'AE description'},
        {field: 'primaryDeathCause', enableRowGroup: true, headerName: 'Primary cause of death'},
        {field: 'secondaryDeathCause', enableRowGroup: true, headerName: 'Secondary cause of death'},
        {field: 'ad', enableRowGroup: true, headerName: 'Additional Drug'},
        {field: 'causedByAD', enableRowGroup: true, headerName: 'AE Caused by Additional Drug'},
        {field: 'ad1', enableRowGroup: true, headerName: 'Additional Drug 1'},
        {field: 'causedByAD1', enableRowGroup: true, headerName: 'AE Caused by Additional Drug 1'},
        {field: 'ad2', enableRowGroup: true, headerName: 'Additional Drug 2'},
        {field: 'causedByAD2', enableRowGroup: true, headerName: 'AE Caused by Additional Drug 2'},
        {field: 'otherMedication', enableRowGroup: true, headerName: 'Other medication'},
        {field: 'causedByOtherMedication', enableRowGroup: true, headerName: 'AE caused by other medication'},
        {field: 'studyProcedure', enableRowGroup: true, headerName: 'Study procedure(s)'},
        {field: 'causedByStudy', enableRowGroup: true, headerName: 'AE caused by study procedure(s)'},
    ]);
}
