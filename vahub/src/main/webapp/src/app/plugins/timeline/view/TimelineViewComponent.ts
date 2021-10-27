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
