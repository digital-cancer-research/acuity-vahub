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

import {Component, Input, Output, EventEmitter, SimpleChanges, ChangeDetectionStrategy, OnChanges} from '@angular/core';
import {ITimelinePage, PageRecord} from '../store/ITimeline';
import {TimelinePaginationService} from './TimelinePaginationService';
import * as  _ from 'lodash';

@Component({
    selector: 'timeline-pagination',
    templateUrl: 'TimelinePaginationComponent.html',
    styleUrls: ['./TimelinePaginationComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelinePaginationComponent implements OnChanges {
    @Input() numberOfSubjects: number;
    @Input() page: ITimelinePage;
    @Input() loading: boolean;
    @Output() updatePage: EventEmitter<ITimelinePage> = new EventEmitter<ITimelinePage>(false);
    public pages: number[];
    public currentPage: number;

    constructor() {
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.updatePages();
        this.updateCurrentPage();
    }

    private updatePages(): void {
        if (!_.isNull(this.page)) {
            this.pages = TimelinePaginationService.getPages(this.numberOfSubjects, this.page.limit);
        } else {
            this.pages = [];
        }
    }

    private updateCurrentPage(): void {
        if (!_.isNull(this.page)) {
            this.currentPage = (this.page.offset / this.page.limit) + 1;
        } else {
            this.currentPage = 1;
        }
    }

    setCurrentPage(pageNumber: number): void {
        const offset: number = (pageNumber - 1) * this.page.limit;
        this.updatePage.emit(<ITimelinePage>new PageRecord({
            limit: this.page.limit,
            offset: offset
        }));
    }

    public showPagination(): boolean {
        return this.pages !== undefined && this.pages.length > 1;
    }
}
