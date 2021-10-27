import {Component, Output, EventEmitter, ChangeDetectionStrategy, Input, OnChanges, SimpleChanges} from '@angular/core';

import {ITimelinePage, PageRecord} from '../store/ITimeline';
import {DropdownItem} from '../../../common/dropdown/DropdownItem';

@Component({
    selector: 'timeline-page-size',
    templateUrl: 'TimelinePageSizeComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelinePageSizeComponent implements OnChanges {
    @Output() updatePage: EventEmitter<ITimelinePage> = new EventEmitter<ITimelinePage>(false);

    pageSizeOptions: Array<DropdownItem> = [
        {displayName: '20', serverName: '20'},
        {displayName: '30', serverName: '30'},
        {displayName: '40', serverName: '40'},
        {displayName: '50', serverName: '50'}
    ];

    @Input()
    page: ITimelinePage;

    currentPageSize: DropdownItem;

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['page']) {
            if (changes['page'].currentValue) {
                const pageSizeOption = this.pageSizeOptions.find(item => +item.serverName === this.page.limit);
                this.currentPageSize = pageSizeOption;
            } else {
                this.currentPageSize = this.pageSizeOptions[0];
            }
        }
    }

    setPageSize(newSize: DropdownItem): void {
        if (newSize.serverName !== this.currentPageSize.serverName) {
            this.currentPageSize = newSize;
            this.updatePage.emit(<ITimelinePage>new PageRecord({
                limit: +newSize.serverName,
                offset: 0
            }));
        }
    }

}
