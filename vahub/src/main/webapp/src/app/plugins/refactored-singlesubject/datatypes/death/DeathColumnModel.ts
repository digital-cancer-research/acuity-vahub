import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class DeathColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'studyId'},
        {field: 'studyPart'},
        {field: 'subjectId'},
        {field: 'deathCause', headerName: 'Cause of death', enableRowGroup: true},
        {field: 'dateOfDeath', enableRowGroup: true},
        {field: 'daysFromFirstDoseToDeath', headerName: 'Days from first dose to death', enableValue: true},
        {field: 'autopsyPerformed', enableRowGroup: true},
        {field: 'designation', enableRowGroup: true},
        {field: 'diseaseUnderInvestigationDeath', headerName: 'Death related to disease under investigation', enableRowGroup: true},
        {field: 'hlt', headerName: 'MedDRA HLT', enableRowGroup: true},
        {field: 'llt', headerName: 'MedDRA LLT', enableRowGroup: true},
        {field: 'preferredTerm', headerName: 'MedDRA PT', enableRowGroup: true},
        {field: 'soc', headerName: 'MedDRA SOC', enableRowGroup: true}
    ]);
}
