import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class RenalColumnsModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'measurementName', enableRowGroup: true},
        {field: 'measurementTimePoint', enableRowGroup: true},
        {field: 'daysOnStudy', enableRowGroup: true, enableValue: true, filter: 'number'},
        {field: 'analysisVisit', enableRowGroup: true, enableValue: true, filter: 'number'},
        {field: 'visitNumber', enableRowGroup: true, enableValue: true, filter: 'number'},
        {field: 'resultValue', enableValue: true, filter: 'number'},
        {field: 'resultUnit', enableRowGroup: true},
        {field: 'timesLowerRef', enableRowGroup: true, filter: 'number'},
        {field: 'timesUpperRef', enableRowGroup: true, enableValue: true, filter: 'number'},
        {field: 'upperRefRangeValue', enableRowGroup: true, enableValue: true, filter: 'number'},
        {field: 'ckdStage', enableRowGroup: true}
    ]);
}
