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

import {AfterContentInit, Component, ElementRef, Input} from '@angular/core';
import {includes} from 'lodash';

import {RangeDateFilterItemModel} from './RangeDateFilterItemModel';
import {BaseRangeComponent} from '../BaseRangeComponent';
import {DateUtilsService} from '../../../common/utils/DateUtilsService';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

@Component({
    selector: 'rangedatefilter',
    templateUrl: 'RangeDateFilterComponent.html',
    styleUrls: ['./RangeDateFilterComponent.css']
})
export class RangeDateFilterComponent extends BaseRangeComponent implements AfterContentInit {

    @Input() model: RangeDateFilterItemModel;
    @Input() openedFilterModel: BaseFilterItemModel;

    constructor(
        private elementRef: ElementRef,
        private dateUtilsService: DateUtilsService) {
        super();
    }

    ngAfterContentInit(): void {
        const element = this.elementRef.nativeElement.querySelector('[data-provide=datepicker]');
        (<any> $(element)).on('changeDate', (e) => {
            const isFromDate = this.isEventTargetFromDate(e.target);
            const datesChanged = this.haveDatesChanged(e.target.value, isFromDate);
            if (datesChanged) {
                this.model.haveMadeChange = true;
                const newDate = e.target.value;
                const oldFromDate = this.dateUtilsService.toShortDate(this.model.originalValues.from);
                const oldToDate = this.dateUtilsService.toShortDate(this.model.originalValues.to);
                if (isFromDate) {
                    this.model.selectedValues.from = this.nullIfUnchanged(newDate, oldFromDate);
                    this.model.selectedValues.to = this.nullIfUnchanged(this.model.textBoxSelectedValues.to, oldToDate);
                    this.model.textBoxSelectedValues.from = newDate;
                } else {
                    this.model.selectedValues.to = this.nullIfUnchanged(newDate, oldToDate);
                    this.model.selectedValues.from = this.nullIfUnchanged(this.model.textBoxSelectedValues.from, oldFromDate);
                    this.model.textBoxSelectedValues.to = newDate;
                }
            }
        });
    }

    public onIncludeEmptyValuesChanged(event): void {
        this.model.haveMadeChange = true;
    }

    private isEventTargetFromDate(target: HTMLElement): boolean {
        return includes(target.classList, 'date-from');
    }

    private haveDatesChanged(inputTextDate: string, isFromDate: boolean): boolean {
        if (isFromDate) {
            return this.model.selectedValues.from !== inputTextDate;
        } else {
            return this.model.selectedValues.to !== inputTextDate;
        }
    }
}
