/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
