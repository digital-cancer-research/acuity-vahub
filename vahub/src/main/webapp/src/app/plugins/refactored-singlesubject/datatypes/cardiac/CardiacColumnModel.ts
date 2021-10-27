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

import {AbstractColumnModel} from '../AbstractColumnModel';
import {ColDef} from 'ag-grid';
import {List} from 'immutable';
import {Injectable} from '@angular/core';

@Injectable()
export class CardiacColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'measurementCategory', enableRowGroup: true},
        {field: 'measurementName', enableRowGroup: true},
        {field: 'measurementTimePoint', enableRowGroup: true},
        {field: 'daysOnStudy', enableRowGroup: true, enableValue: true},
        {field: 'analysisVisit', enableRowGroup: true, enableValue: true},
        {field: 'visitNumber', enableRowGroup: true, enableValue: true},
        {field: 'protocolScheduleTimepoint', enableRowGroup: true},
        {field: 'method', enableRowGroup: true},
        {field: 'resultValue', enableRowGroup: true, enableValue: true},
        {field: 'resultUnit', enableRowGroup: true},
        {field: 'baselineValue', enableRowGroup: true, enableValue: true},
        {field: 'changeFromBaseline', enableRowGroup: true, enableValue: true},
        {field: 'percentChangeFromBaseline', enableRowGroup: true, enableValue: true},
        {field: 'baselineFlag', enableRowGroup: true},
        {field: 'clinicallySignificant', enableRowGroup: true},
        {field: 'dateOfLastDose', enableRowGroup: true, headerName: 'Date of last drug dose'},
        {field: 'lastDoseAmount', enableRowGroup: true, headerName: 'Last drug dose amount'},
        {field: 'atrialFibrillation', enableRowGroup: true, headerName: ''},
        {field: 'sinusRhythm', enableRowGroup: true, headerName: ''},
        {field: 'reasonNoSinusRhythm', enableRowGroup: true, headerName: 'Reason, no sinus rhythm'},
        {field: 'heartRhythm', enableRowGroup: true, headerName: ''},
        {field: 'heartRhythmOther', enableRowGroup: true, headerName: 'Heart rhythm, other'},
        {field: 'extraSystoles', enableRowGroup: true, headerName: ''},
        {field: 'specifyExtraSystoles', enableRowGroup: true, headerName: ''},
        {field: 'typeOfConduction', enableRowGroup: true, headerName: ''},
        {field: 'conduction', enableRowGroup: true, headerName: ''},
        {field: 'reasonAbnormalConduction', enableRowGroup: true, headerName: 'Reason, abnormal conduction'},
        {field: 'sttChanges', enableRowGroup: true, headerName: 'ST-T changes'},
        {field: 'stSegment', enableRowGroup: true, headerName: 'ST segment'},
        {field: 'wave', enableRowGroup: true, headerName: 'T-wave'},
        {field: 'beatGroupNumber', enableRowGroup: true, headerName: 'Beat group number', enableValue: true},
        {field: 'beatNumberWithinBeatGroup', enableRowGroup: true, headerName: 'Beat number within beat group', enableValue: true},
        {field: 'numberOfBeatsInAverageBeat', enableRowGroup: true, headerName: 'Number of beats in average beat', enableValue: true},
        {field: 'beatGroupLengthInSec', enableRowGroup: true, headerName: 'Beat group length (sec)', enableValue: true},
        {field: 'comment', enableRowGroup: true, headerName: 'Cardiologist comment'}
    ]);
}
