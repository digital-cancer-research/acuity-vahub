import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class DoseDiscontinuationColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study id'},
        {field: 'studyPart', headerName: 'Study part'},
        {field: 'subjectId', headerName: 'Subject id'},
        {field: 'studyDrug', headerName: 'Study drug', enableRowGroup: true},
        {field: 'discDate', headerName: 'Date of IP discontinuation', enableRowGroup: true},
        {field: 'studyDayAtIpDiscontinuation', headerName: 'Study day at IP discontinuation', enableValue: true},
        {field: 'discReason', headerName: 'Main reason for IP discontinuation', enableRowGroup: true},
        {field: 'ipDiscSpec', headerName: 'IP discontinuation specification', enableRowGroup: true},
        {field: 'subjectDecisionSpec', headerName: 'Subject decision specification', enableRowGroup: true},
        {field: 'subjectDecisionSpecOther', headerName: 'Other subject decision specification', enableRowGroup: true},
    ]);
}
