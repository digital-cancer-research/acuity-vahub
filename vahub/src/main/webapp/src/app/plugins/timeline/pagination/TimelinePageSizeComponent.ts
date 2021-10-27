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
