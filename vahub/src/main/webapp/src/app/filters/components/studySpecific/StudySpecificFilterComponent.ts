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

import {Component, Input, Output, EventEmitter} from '@angular/core';

import {StudySpecificFilterModel} from './StudySpecificFilterModel';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

@Component({
    selector: 'studyspecificfilter',
    templateUrl: 'StudySpecificFilterComponent.html',
    styleUrls: ['../../filters.css']
})
export class StudySpecificFilterComponent {

    @Input() ssfmodel: StudySpecificFilterModel;
    @Input() openedFilterModel: BaseFilterItemModel;
    @Output() modelChanged = new EventEmitter();

    onChange(filterName: string, item: string, target: any): void {
        console.log(target);
        this.ssfmodel.change(filterName, item, target.checked);
        this.modelChanged.emit({});
    }

    /**
     * Clears the selected values
     */
    clear(): void {
        this.ssfmodel.clear();
        this.modelChanged.emit({});
    }
}
