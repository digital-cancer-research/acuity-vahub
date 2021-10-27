import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import {TrackLegendItem, TrackLegendType} from './ITrackLegend';
import {drawStar} from '../../../../common/CommonChartUtils';

@Component({
    selector: 'timeline-track-legend-item',
    templateUrl: 'TimelineTrackLegendItemComponent.html',
    styleUrls: ['./TimelineTrackLegendItemComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineTrackLegendItemComponent {
    @Input() trackLegendItem: TrackLegendItem;
    trackLegendType = TrackLegendType;
    drawStar = drawStar;
}
