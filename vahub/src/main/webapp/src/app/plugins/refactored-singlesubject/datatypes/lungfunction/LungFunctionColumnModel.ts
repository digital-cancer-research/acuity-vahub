import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class LungFunctionColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'measurementName', enableRowGroup: true},
        {field: 'measurementTimePoint', enableRowGroup: true},
        {field: 'daysOnStudy', enableValue: true},
        {field: 'protocolScheduleTimepoint', enableRowGroup: true},
        {field: 'visitNumber', enableRowGroup: true, filter: 'number'},
        {field: 'plannedVisit', enableRowGroup: true},
        {field: 'resultValue', enableValue: true},
        {field: 'resultUnit', enableRowGroup: true},
        {field: 'baselineValue', enableValue: true},
        {field: 'changeFromBaseline', enableValue: true},
        {field: 'percentChangeFromBaseline', enableValue: true},
        {field: 'baselineFlag', enableRowGroup: true}
    ]);
}
