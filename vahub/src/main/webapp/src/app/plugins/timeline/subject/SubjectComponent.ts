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
