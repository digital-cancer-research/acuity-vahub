import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';

import {PatientDataSummaryTrackModel} from './PatientDataSummaryTrackModel';
import {AbstractTrackComponent} from '../AbstractTrackComponent';
import {PatientDataTrackUtils} from './PatientDataTrackUtils';
import {IHighlightedPlotArea, ITrack, IZoom} from '../../store/ITimeline';
import {List} from 'immutable';
import {PatientDataDetailTrackModel} from './PatientDataDetailTrackModel';

@Component({
    selector: 'patientdata-track',
    templateUrl: 'PatientDataTrackComponent.html',
    styleUrls: ['../TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PatientDataTrackComponent extends AbstractTrackComponent implements OnChanges {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() subjectHighlighted: boolean;
    @Input() zoom: IZoom;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);
    @Output() cursorXCoordinate = new EventEmitter<number>();

    constructor(private patientDataSummaryTrackModel: PatientDataSummaryTrackModel,
                private patientDataDetailTrackModel: PatientDataDetailTrackModel) {
        super();
    }

    canExpand(): boolean {
        if (this.track.expansionLevel === PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL) {
            return this.patientDataSummaryTrackModel.canExpand();
        } else {
            return this.patientDataDetailTrackModel.canExpand();
        }
    }

    canCollapse(): boolean {
        if (this.track.expansionLevel === PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL) {
            return this.patientDataSummaryTrackModel.canCollapse();
        } else {
            return this.patientDataDetailTrackModel.canCollapse();
        }
    }

    canExpandAll(): boolean {
        return this.canExpand();
    }
    canCollapseAll(): boolean {
        return this.canCollapse();
    }

    protected updatePlotData(): void {
        if (this.track.expansionLevel === PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.patientDataSummaryTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        }
        if (this.track.expansionLevel === PatientDataTrackUtils.LINECHART_LEVEL_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.patientDataDetailTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        }
    }
}
