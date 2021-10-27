import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {
    EcgWarnings,
    EcgYAxisValue,
    IHighlightedPlotArea,
    ITrack,
    IZoom,
    LabsYAxisValue,
    SpirometryYAxisValue,
    TrackName,
    VitalsYAxisValue
} from '../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'timeline-track',
    templateUrl: 'TimelineTrackComponent.html',
    styleUrls: ['./TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineTrackComponent {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() subjectHighlighted: boolean;
    @Input() zoom: IZoom;
    @Input() labsYAxisValue: LabsYAxisValue;
    @Input() spirometryYAxisValue: SpirometryYAxisValue;
    @Input() ecgYAxisValue: EcgYAxisValue;
    @Input() ecgWarnings: EcgWarnings;
    @Input() vitalsYAxisValue: VitalsYAxisValue;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);

    trackName = TrackName;
}
