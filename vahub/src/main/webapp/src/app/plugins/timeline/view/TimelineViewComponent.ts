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

import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    Output,
    ElementRef,
    AfterViewInit
} from '@angular/core';
import {ISubject} from '../store/ITimeline';
import {TimelineObservables} from '../store/observable/TimelineObservables';

@Component({
    selector: 'timeline-view',
    templateUrl: 'TimelineViewComponent.html',
    styleUrls: ['./TimelineViewComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineViewComponent implements AfterViewInit {
    @Input() subjects: ISubject[];
    @Input() loading: boolean;
    @Input() maxHeight: string;
    @Output() expandOrCollapseTrack = new EventEmitter(false);

    constructor(public timelineObservables: TimelineObservables, private elementRef: ElementRef) {
    }

    ngAfterViewInit() {
        this.elementRef.nativeElement.addEventListener('mousedown', (e: MouseEvent) => e.preventDefault());
    }

    trackBySubjectId(index, item) {
        return item ? item.subjectId : undefined;
    }

    get isEmpty() {
        // @ts-ignore
        return !this.subjects.size;
    }
}
