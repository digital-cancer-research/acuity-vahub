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

import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {ISubject, ITrack} from '../store/ITimeline';
import {List} from 'immutable';
import {TrackUtils} from '../track/TrackUtils';
import {TimelineUtils} from '../chart/TimelineUtils';
import {TimelineObservables} from '../store/observable/TimelineObservables';

@Component({
    selector: 'timeline-subject',
    styleUrls: [
        './SubjectComponent.css',
        '../track/TimelineTrackComponent.css'
    ],
    templateUrl: 'SubjectComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SubjectComponent implements OnChanges {
    @Input() subject: ISubject;
    @Input() showSubjectId: boolean;
    @Output() expandOrCollapseTrack = new EventEmitter(false);
    @Output() cursorXCoordinate = new EventEmitter<number>();
    maxHeight: number = TimelineUtils.BAR_CHART_HEIGHT;
    highlighted = false;

    constructor(public timelineObservables: TimelineObservables) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.updateMaxHeight();
    }

    private updateMaxHeight(): void {
        this.maxHeight = TrackUtils.getHeightOfTracks(this.subject.tracks);
    }

    setHighlight(highlighted: boolean): void {
        this.highlighted = highlighted;
    }

    getGapBetweenTracks(trackIndex: number): number {
        const toShowNextTrack = trackIndex + 1 < this.subject.tracks.size;
        return toShowNextTrack ? TrackUtils.GAP_BETWEEN_TRACKS : 0;
    }

    trackByTrackName(index, item) {
        return item ? item.name : undefined;
    }
}
