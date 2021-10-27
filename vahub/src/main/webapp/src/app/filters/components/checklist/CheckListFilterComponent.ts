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

import {Component, Input} from '@angular/core';

import {CheckListFilterItemModel} from './CheckListFilterItemModel';
import * as  _ from 'lodash';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

@Component({
    selector: 'checklistfilter',
    templateUrl: 'CheckListFilterComponent.html',
    styleUrls: ['../../filters.css']
})
export class CheckListFilterComponent {

    @Input() model: CheckListFilterItemModel;
    @Input() openedFilterModel: BaseFilterItemModel;

    toggleSelectedItem(item: string): void {
        if (_.includes(this.model.selectedValues, item)) {
            _.remove(this.model.selectedValues, (selectedValue: string) => {
                return selectedValue === item;
            });
        } else {
            this.model.selectedValues.push(item);
        }
    }

    isItemPresentInAvailableList(item: any): boolean {
        return this.model.isInAvaliableList(item);
    }

    isLastAvailableItem(item: any): boolean {
        return this.model.selectedValues.length === 1 && this.model.isInSelectedList(item);
    }

    isItemPresentInSelectedList(item: any): boolean {
        return this.model.isInSelectedList(item);
    }
}
