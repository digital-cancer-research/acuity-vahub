import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class ExacerbationsColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', enableRowGroup: true},
        {field: 'studyPart', enableRowGroup: true},
        {field: 'subjectId', enableRowGroup: true},
        {field: 'exacerbationClassification', enableRowGroup: true},
        {field: 'startDate', enableRowGroup: true},
        {field: 'endDate', enableRowGroup: true},
        {field: 'daysOnStudyAtStart', enableValue: true, filter: 'number'},
        {field: 'daysOnStudyAtEnd', enableValue: true, filter: 'number'},
        {field: 'duration', enableValue: true, filter: 'number'},
        {field: 'startPriorToRandomisation', enableRowGroup: true},
        {field: 'endPriorToRandomisation', enableRowGroup: true},
        {field: 'hospitalisation', enableRowGroup: true},
        {field: 'emergencyRoomVisit', enableRowGroup: true},
        {field: 'antibioticsTreatment', enableRowGroup: true},
        {field: 'depotCorticosteroidTreatment', enableRowGroup: true},
        {field: 'systemicCorticosteroidTreatment', enableRowGroup: true},
        {field: 'increasedInhaledCorticosteroidTreatment', enableRowGroup: true}
    ]);
}
