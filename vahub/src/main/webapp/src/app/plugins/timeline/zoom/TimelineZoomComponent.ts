import {Component, ChangeDetectionStrategy, Input, Output, EventEmitter} from '@angular/core';
import {IZoom} from '../store/ITimeline';

@Component({
    selector: 'timeline-zoom',
    templateUrl: 'TimelineZoomComponent.html',
    styleUrls: ['./TimelineZoomComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineZoomComponent {
    @Input() zoom: IZoom;
    @Output() updateZoom = new EventEmitter(false);
}
