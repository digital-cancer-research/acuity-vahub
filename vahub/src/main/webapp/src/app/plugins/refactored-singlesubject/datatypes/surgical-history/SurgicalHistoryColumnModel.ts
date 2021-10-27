import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class SurgicalHistoryColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId',  headerName: 'Study id'},
        {field: 'studyPart',  headerName: 'Study part'},
        {field: 'subjectId',  headerName: 'Subject id'},
        {field: 'surgicalProcedure',  headerName: 'Medical history term', enableRowGroup: true},
        {field: 'currentMedication',  headerName: 'Current medication', enableRowGroup: true},
        {field: 'start',  headerName: 'Start date', enableRowGroup: true},
        {field: 'preferredTerm',  headerName: 'PT name', enableRowGroup: true},
        {field: 'hlt',  headerName: 'HLT name', enableRowGroup: true},
        {field: 'soc',  headerName: 'SOC name', enableRowGroup: true},
    ]);
}
