import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class LiverDiagnosticInvestigationColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'liverDiagInv', headerName: 'Liver diagnostic investigation', enableRowGroup: true},
        {field: 'liverDiagInvSpec', headerName: 'Liver diagnostic investigation specification', enableRowGroup: true},
        {field: 'liverDiagInvDate', headerName: 'Liver diagnostic investigation date', enableRowGroup: true},
        {field: 'studyDayLiverDiagInv', headerName: 'Study day at liver diagnostic investigation', enableValue: true},
        {field: 'liverDiagInvResult', headerName: 'Liver diagnostic investigation results', enableRowGroup: true},
        {field: 'potentialHysLawCaseNum', headerName: 'Potential Hy\'s law case number', enableValue: true}
    ]);
}
