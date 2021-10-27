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

import {ListFilterItemModel} from './ListFilterItemModel';
import {includes, isEmpty, last, remove} from 'lodash';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

@Component({
    selector: 'listfilter',
    templateUrl: 'ListFilterComponent.html',
    styleUrls: ['../../filters.css']
})
export class ListFilterComponent {

    @Input() model: ListFilterItemModel;
    @Input() openedFilterModel: BaseFilterItemModel;

    public toggleSelectedItem(item: string, clickEvent: KeyboardEvent): void {
        const currentSelectedItemIndex = this.model.availableValues.indexOf(item);
        if (clickEvent.shiftKey && !isEmpty(this.model.selectedItemsQueue)) {
            const previousSelectedItemIndex = last(this.model.selectedItemsQueue);
            for (let i = Math.min(currentSelectedItemIndex, previousSelectedItemIndex) + 1;
                 i < Math.max(currentSelectedItemIndex, previousSelectedItemIndex);
                 i++) {
                if (!includes(this.model.selectedValues, this.model.availableValues[i])) {
                    this.model.selectedValues.push(this.model.availableValues[i]);
                }
            }
        }
        if (includes(this.model.selectedValues, item)) {
            remove(this.model.selectedItemsQueue, (selectedItemIndex: number) => {
                return selectedItemIndex === currentSelectedItemIndex;
            });
            remove(this.model.selectedValues, (selectedValue: string) => {
                return selectedValue === item;
            });
        } else {
            this.model.selectedItemsQueue.push(currentSelectedItemIndex);
            this.model.selectedValues.push(item);
        }

    }
}
