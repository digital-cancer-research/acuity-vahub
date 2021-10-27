import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class ConmedsColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', enableRowGroup: true},
        {field: 'studyPart', enableRowGroup: true},
        {field: 'subjectId', enableRowGroup: true},

        {field: 'medicationName', enableRowGroup: true},
        {field: 'atcCode', enableRowGroup: true},
        {field: 'dose', enableValue: true},
        {field: 'doseUnits', enableRowGroup: true},
        {field: 'startDate', enableRowGroup: true},
        {field: 'endDate', enableRowGroup: true},
        {field: 'duration', enableValue: true},
        {field: 'conmedTreatmentOngoing', enableRowGroup: true},
        {field: 'studyDayAtConmedStart', enableValue: true},
        {field: 'startPriorToRandomisation', enableRowGroup: true},
        {field: 'endPriorToRandomisation', enableRowGroup: true},
        {field: 'treatmentReason', enableRowGroup: true},
        {field: 'studyDayAtConmedEnd', enableValue: true},
        {field: 'doseFrequency', enableRowGroup: true},
        {field: 'aePt', headerName: 'AE PT', enableRowGroup: true},
        {field: 'aeNum', headerName: 'AE Number', enableRowGroup: true}
    ]);
}
