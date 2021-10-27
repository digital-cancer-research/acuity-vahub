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

import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChange
} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {LabsSummaryTrackModel} from './LabsSummaryTrackModel';
import {LabsCategoryTrackModel} from './LabsCategoryTrackModel';
import {LabsDetailTrackModel} from './LabsDetailTrackModel';
import {LabsDetailWithThresholdTrackModel} from './LabsDetailWithThresholdTrackModel';

import {AbstractTrackComponent} from '../AbstractTrackComponent';
import {LabsTrackUtils} from './LabsTrackUtils';
import {IHighlightedPlotArea, ITrack, IZoom, LabsYAxisValue} from '../../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'labs-track',
    templateUrl: 'LabsTrackComponent.html',
    styleUrls: ['../TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class LabsTrackComponent extends AbstractTrackComponent implements OnInit, OnChanges, OnDestroy {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() subjectHighlighted: boolean;
    @Input() zoom: IZoom;
    @Input() labsYAxisValue: LabsYAxisValue;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);
    @Output() cursorXCoordinate = new EventEmitter<number>();

    private labsYAxisValueSubscription: Subscription;

    constructor(private labsSummaryTrackModel: LabsSummaryTrackModel,
                private labsCategoryTrackModel: LabsCategoryTrackModel,
                private labsDetailTrackModel: LabsDetailTrackModel,
                private labsDetailWithThresholdTrackModel: LabsDetailWithThresholdTrackModel) {
        super();
    }

    ngOnInit(): void {
        if (this.labsYAxisValue) {
            this.labsDetailWithThresholdTrackModel.setYAxisPlotValue(this.labsYAxisValue);
        }
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['labsYAxisValue']) {
            this.labsDetailWithThresholdTrackModel.setYAxisPlotValue(this.labsYAxisValue);
            this.updateComponent(changes);
        }

        super.ngOnChanges(changes);
    }

    ngOnDestroy(): void {
        if (this.labsYAxisValueSubscription) {
            this.labsYAxisValueSubscription.unsubscribe();
        }
    }

    canExpand(): boolean {
        if (this.track.expansionLevel === LabsTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsSummaryTrackModel.canExpand();
        } else if (this.track.expansionLevel === LabsTrackUtils.CATEGORY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsCategoryTrackModel.canExpand();
        } else if (this.track.expansionLevel === LabsTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsDetailTrackModel.canExpand();
        } else if (this.track.expansionLevel === LabsTrackUtils.DETAIL_WITH_THRESHOLD_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsDetailWithThresholdTrackModel.canExpand();
        } else {
            return false;
        }
    }

    canCollapse(): boolean {
        if (this.track.expansionLevel === LabsTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsSummaryTrackModel.canCollapse();
        } else if (this.track.expansionLevel === LabsTrackUtils.CATEGORY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsCategoryTrackModel.canCollapse();
        } else if (this.track.expansionLevel === LabsTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsDetailTrackModel.canCollapse();
        } else if (this.track.expansionLevel === LabsTrackUtils.DETAIL_WITH_THRESHOLD_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsDetailWithThresholdTrackModel.canCollapse();
        } else {
            return false;
        }
    }

    canExpandAll(): boolean {
        if (this.track.expansionLevel === LabsTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsSummaryTrackModel.canExpand();
        } else {
            return false;
        }
    }

    canCollapseAll(): boolean {
        if (this.track.expansionLevel === LabsTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsSummaryTrackModel.canCollapse();
        } else if (this.track.expansionLevel === LabsTrackUtils.CATEGORY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsCategoryTrackModel.canCollapse();
        } else if (this.track.expansionLevel === LabsTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsDetailTrackModel.canCollapse();
        } else if (this.track.expansionLevel === LabsTrackUtils.DETAIL_WITH_THRESHOLD_SUB_TRACK_EXPANSION_LEVEL) {
            return this.labsDetailWithThresholdTrackModel.canCollapse();
        } else {
            return false;
        }
    }

    protected updatePlotData(): void {
        if (this.track.expansionLevel === LabsTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.labsSummaryTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        } else if (this.track.expansionLevel === LabsTrackUtils.CATEGORY_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.labsCategoryTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        } else if (this.track.expansionLevel === LabsTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.labsDetailTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        } else if (this.track.expansionLevel === LabsTrackUtils.DETAIL_WITH_THRESHOLD_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.labsDetailWithThresholdTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        }
    }
}
