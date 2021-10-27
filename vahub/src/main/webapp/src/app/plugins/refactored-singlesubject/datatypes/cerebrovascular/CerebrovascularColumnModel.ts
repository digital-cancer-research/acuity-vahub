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
export class CerebrovascularColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study ID', enableRowGroup: true},
        {field: 'studyPart', headerName: 'Study part', enableRowGroup: true},
        {field: 'subjectId', headerName: 'Subject ID', enableRowGroup: true},
        {field: 'eventType', headerName: 'Type of Event', enableRowGroup: true},
        {field: 'aeNumber', headerName: 'Associated AE No.', enableRowGroup: true},
        {field: 'startDate', headerName: 'Event Start Date', enableRowGroup: true},
        {field: 'term', headerName: 'Event Term', enableRowGroup: true},
        {field: 'primaryIschemicStroke', headerName: 'If Primary Ischemic Stroke', enableRowGroup: true},
        {field: 'traumatic', headerName: 'If Traumatic', enableRowGroup: true},
        {field: 'intraHemorrhageLoc', headerName: 'Loc. of Primary Intracranial Hemorrhage', enableRowGroup: true},
        {field: 'intraHemorrhageOtherLoc', headerName: 'Primary Intra. Hemorrhage Other, Specify', enableRowGroup: true},
        {field: 'symptomsDuration', headerName: 'Duration of Symptoms', enableRowGroup: true},
        {field: 'mrsPriorToStroke', headerName: 'MRS Prior to Stroke', enableRowGroup: true},
        {field: 'mrsDuringStrokeHosp', headerName: 'MRS During Stroke Hospitalisation', enableRowGroup: true},
        {field: 'mrsCurrVisitOr90dAfter', headerName: 'MRS at Current Visit or 90D After Stroke', enableRowGroup: true},
        {field: 'comment', headerName: 'Comment', enableRowGroup: true}
    ]);
}
