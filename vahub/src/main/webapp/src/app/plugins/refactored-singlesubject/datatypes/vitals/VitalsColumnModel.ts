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
