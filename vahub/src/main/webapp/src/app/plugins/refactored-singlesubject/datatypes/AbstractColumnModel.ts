import {ColDef} from 'ag-grid';
import {List} from 'immutable';
import {capitalize, startCase} from 'lodash';

export class AbstractColumnModel {

    public static readonly WARNING_COLOUR = 'lightcoral';

    protected _columnDefs: List<ColDef>;

    get columnDefs(): List<ColDef> {
        return this._columnDefs.map((columnDef) => {
            columnDef.headerName = columnDef.headerName || capitalize(startCase(columnDef.field).toLowerCase());
            return columnDef;
        }).toList();
    }
}
