import {Injectable} from '@angular/core';
import {ColDef} from 'ag-grid/main';
import {List} from 'immutable';
import {AbstractColumnModel} from '../AbstractColumnModel';

@Injectable()
export class DemographyColumnModel extends AbstractColumnModel {
    _columnDefs: List<ColDef> = List([
        {field: 'sex'},
        {field: 'race'},
        {field: 'age'},
        {field: 'weight'},
        {field: 'height'}
    ]);
}
