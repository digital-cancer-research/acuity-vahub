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

import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChange} from '@angular/core';
import {DoseSummaryTrackModel} from './DoseSummaryTrackModel';
import {DoseDrugSummaryTrackModel} from './DoseDrugSummaryTrackModel';
import {DoseDrugDetailTrackModel} from './DoseDrugDetailTrackModel';
import {AbstractTrackComponent} from '../AbstractTrackComponent';
import {DoseTrackUtils} from './DoseTrackUtils';
import {IHighlightedPlotArea, ITrack, IZoom} from '../../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'dose-track',
    templateUrl: 'DoseTrackComponent.html',
    styleUrls: ['../TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DoseTrackComponent extends AbstractTrackComponent implements OnChanges {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() zoom: IZoom;
    @Input() subjectHighlighted: boolean;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);
    @Output() cursorXCoordinate = new EventEmitter<number>();

    constructor(private doseSummaryTrackModel: DoseSummaryTrackModel,
                private doseDrugSummaryTrackModel: DoseDrugSummaryTrackModel,
                private doseDrugDetailTrackModel: DoseDrugDetailTrackModel) {
        super();
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        super.ngOnChanges(changes);
    }

    canExpand(): boolean {
        if (this.track.expansionLevel === DoseTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.doseSummaryTrackModel.canExpand();
        } else if (this.track.expansionLevel === DoseTrackUtils.DRUG_SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.doseDrugSummaryTrackModel.canExpand();
        } else if (this.track.expansionLevel === DoseTrackUtils.DRUG_DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            return this.doseDrugDetailTrackModel.canExpand();
        } else {
            return false;
        }
    }

    canCollapse(): boolean {
        if (this.track.expansionLevel === DoseTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.doseSummaryTrackModel.canCollapse();
        } else if (this.track.expansionLevel === DoseTrackUtils.DRUG_SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.doseDrugSummaryTrackModel.canCollapse();
        } else if (this.track.expansionLevel === DoseTrackUtils.DRUG_DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            return this.doseDrugDetailTrackModel.canCollapse();
        } else {
            return false;
        }
    }

    canExpandAll(): boolean {
        return this.canExpand();
    }

    canCollapseAll(): boolean {
        return this.canCollapse();
    }

    protected updatePlotData(): void {
        if (this.track.expansionLevel === DoseTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.doseSummaryTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        } else if (this.track.expansionLevel === DoseTrackUtils.DRUG_SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.doseDrugSummaryTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        } else if (this.track.expansionLevel === DoseTrackUtils.DRUG_DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.doseDrugDetailTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        }
    }
}
