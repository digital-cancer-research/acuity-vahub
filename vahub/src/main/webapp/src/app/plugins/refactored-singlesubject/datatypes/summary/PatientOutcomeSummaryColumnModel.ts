import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class PatientOutcomeSummaryColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'date'},
        {field: 'event'},
        {field: 'outcome'},
        {field: 'reason'}
    ]);
}
