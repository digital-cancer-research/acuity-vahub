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
