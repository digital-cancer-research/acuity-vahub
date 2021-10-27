import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChange} from '@angular/core';
import {ExacerbationsSummaryTrackModel} from './ExacerbationsSummaryTrackModel';
import {AbstractTrackComponent} from '../AbstractTrackComponent';
import {IHighlightedPlotArea, ITrack, IZoom} from '../../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'exacerbation-track',
    templateUrl: 'ExacerbationsTrackComponent.html',
    styleUrls: ['../TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExacerbationsTrackComponent extends AbstractTrackComponent implements OnChanges {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() zoom: IZoom;
    @Input() subjectHighlighted: boolean;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);
    @Output() cursorXCoordinate = new EventEmitter<number>();

    constructor(private exacerbationsSummaryTrackModel: ExacerbationsSummaryTrackModel) {
        super();
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        super.ngOnChanges(changes);
    }

    canExpand(): boolean {
        return this.exacerbationsSummaryTrackModel.canExpand();
    }

    canCollapse(): boolean {
        return this.exacerbationsSummaryTrackModel.canCollapse();
    }

    canExpandAll(): boolean {
        return this.canExpand();
    }

    canCollapseAll(): boolean {
        return this.canCollapse();
    }

    protected updatePlotData(): void {
        this.trackPlotDetails = this.exacerbationsSummaryTrackModel.createTrackPlotDetail(this.subjectId, this.track);
    }
}
