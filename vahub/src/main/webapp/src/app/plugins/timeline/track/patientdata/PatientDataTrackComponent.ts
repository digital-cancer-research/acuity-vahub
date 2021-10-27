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
