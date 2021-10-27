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

import {Component, Input, Output, EventEmitter, ChangeDetectionStrategy} from '@angular/core';
import {isEmpty, last, head} from 'lodash';

@Component({
    selector: 'trellis-pagination',
    templateUrl: 'TrellisingPaginationComponent.html',
    styleUrls: ['./TrellisingPaginationComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrellisingPaginationComponent {
    private pageTabLimit = 5;
    @Input() currentPage: number;
    @Input() pages: number[];
    @Input() isLoading: boolean;
    @Output() updatePage: EventEmitter<number> = new EventEmitter<number>(false);

    public activePage(page): boolean {
        return page === this.currentPage;
    }

    public displayedPages(): number[] {
        if (isEmpty(this.pages) || this.pages.length <= this.pageTabLimit) {
            return this.pages;
        } else {
            const start: number = this.currentPage < Math.ceil(this.pageTabLimit / 2)
                ? 0 : this.currentPage - Math.ceil(this.pageTabLimit / 2);
            return this.pages.slice(start, start + this.pageTabLimit);
        }
    }

    public moreToLeft(): boolean {
        if (isEmpty(this.pages) || this.pages.length <= this.pageTabLimit) {
            return false;
        } else {
            return this.currentPage > Math.ceil(this.pageTabLimit / 2);
        }
    }

    public moreToRight(): boolean {
        if (isEmpty(this.pages) || this.pages.length <= this.pageTabLimit) {
            return false;
        } else {
            return this.currentPage + Math.floor(this.pageTabLimit / 2) < this.pages.length;
        }
    }

    showPagination(): boolean {
        return !this.isLoading && !isEmpty(this.pages) && this.pages.length !== 1;
    }


    last(): void {
        this.updatePage.emit(last(this.pages));
    }

    first(): void {
        this.updatePage.emit(head(this.pages));
    }
}
