import {Component, Input, ChangeDetectionStrategy} from '@angular/core';
import {TrackLegendConfig} from './ITrackLegend';

@Component({
    selector: 'timeline-track-legend',
    templateUrl: 'TimelineTrackLegendComponent.html',
    styleUrls: ['./TimelineTrackLegendComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineTrackLegendComponent {
    @Input() trackLegendConfig: TrackLegendConfig;
    @Input() yAxisOption: any;
    @Input() ecgWarningsAvailable: boolean;
}
