import {
    AfterViewInit,
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    Output,
    SimpleChange
} from '@angular/core';
import {AbstractTrackComponent} from '../AbstractTrackComponent';
import {StatusTrackModel} from './StatusTrackModel';
import {IHighlightedPlotArea, ITrack, IZoom} from '../../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'status-track',
    templateUrl: 'StatusTrackComponent.html',
    styleUrls: ['../TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StatusTrackComponent extends AbstractTrackComponent implements OnChanges {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() zoom: IZoom;
    @Input() subjectHighlighted: boolean;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);
    @Output() cursorXCoordinate = new EventEmitter<number>();

    constructor(private statusTrackModel: StatusTrackModel) {
        super();
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        super.ngOnChanges(changes);
    }

    canExpand(): boolean {
        return this.statusTrackModel.canExpand();
    }

    canCollapse(): boolean {
        return this.statusTrackModel.canCollapse();
    }

    canExpandAll(): boolean {
        return this.canExpand();
    }

    canCollapseAll(): boolean {
        return this.canCollapse();
    }

    protected updatePlotData(): void {
        this.trackPlotDetails = this.statusTrackModel.createTrackPlotDetail(this.subjectId, this.track);
    }
}
