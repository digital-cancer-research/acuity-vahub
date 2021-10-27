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
