import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class CvotColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId', headerName: 'Study ID', enableRowGroup: true},
        {field: 'studyPart', headerName: 'Study Part', enableRowGroup: true},
        {field: 'subjectId', headerName: 'Subject ID', enableRowGroup: true},
        {field: 'aeNumber', headerName: 'Associated AE No.', enableRowGroup: true},
        {field: 'startDate', headerName: 'Event Start Date', enableRowGroup: true},
        {field: 'term', headerName: 'Event Term', enableRowGroup: true},
        {field: 'category1', headerName: 'Event Category 1', enableRowGroup: true},
        {field: 'category2', headerName: 'Event Category 2', enableRowGroup: true},
        {field: 'category3', headerName: 'Event Category 3', enableRowGroup: true},
        {field: 'description1', headerName: 'Event Description 1', enableRowGroup: true},
        {field: 'description2', headerName: 'Event Description 2', enableRowGroup: true},
        {field: 'description3', headerName: 'Event Description 3', enableRowGroup: true}
    ]);
}
