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

import {EcgSummaryTrackModel} from './EcgSummaryTrackModel';
import {EcgMeasurementTrackModel} from './EcgMeasurementTrackModel';
import {EcgDetailTrackModel} from './EcgDetailTrackModel';
import {AbstractTrackComponent} from '../AbstractTrackComponent';
import {EcgTrackUtils} from './EcgTrackUtils';
import {EcgWarnings, EcgYAxisValue, IHighlightedPlotArea, ITrack, IZoom} from '../../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'ecg-track',
    templateUrl: 'EcgTrackComponent.html',
    styleUrls: ['../TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EcgTrackComponent extends AbstractTrackComponent implements OnInit, OnChanges, OnDestroy {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() subjectHighlighted: boolean;
    @Input() zoom: IZoom;
    @Input() ecgYAxisValue: EcgYAxisValue;
    @Input() ecgWarnings: EcgWarnings;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);
    @Output() cursorXCoordinate = new EventEmitter<number>();

    private ecgYAxisValueSubscription: Subscription;
    private ecgWarningsSubscription: Subscription;

    constructor(private ecgSummaryTrackModel: EcgSummaryTrackModel,
                private ecgMeasurementTrackModel: EcgMeasurementTrackModel,
                private ecgDetailTrackModel: EcgDetailTrackModel) {
        super();
    }

    ngOnInit(): void {
        if (this.ecgYAxisValue) {
            this.ecgDetailTrackModel.setYAxisPlotValue(this.ecgYAxisValue);
        }
        if (this.ecgWarnings) {
            EcgTrackUtils.setWarnings(this.ecgWarnings);
        }
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['ecgYAxisValue']) {
            this.ecgDetailTrackModel.setYAxisPlotValue(this.ecgYAxisValue);
            this.updateComponent(changes);
        }
        if (changes['ecgWarnings']) {
            EcgTrackUtils.setWarnings(this.ecgWarnings);
            this.updateComponent(changes);
        }

        super.ngOnChanges(changes);
    }

    ngOnDestroy(): void {
        if (this.ecgYAxisValueSubscription) {
            this.ecgYAxisValueSubscription.unsubscribe();
        }
        if (this.ecgWarningsSubscription) {
            this.ecgWarningsSubscription.unsubscribe();
        }
    }

    canExpand(): boolean {
        if (this.track.expansionLevel === EcgTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.ecgSummaryTrackModel.canExpand();
        } else if (this.track.expansionLevel === EcgTrackUtils.MEASUREMENT_SUB_TRACK_EXPANSION_LEVEL) {
            return this.ecgMeasurementTrackModel.canExpand();
        } else if (this.track.expansionLevel === EcgTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            return this.ecgDetailTrackModel.canExpand();
        } else {
            return false;
        }
    }

    canCollapse(): boolean {
        if (this.track.expansionLevel === EcgTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.ecgSummaryTrackModel.canCollapse();
        } else if (this.track.expansionLevel === EcgTrackUtils.MEASUREMENT_SUB_TRACK_EXPANSION_LEVEL) {
            return this.ecgMeasurementTrackModel.canCollapse();
        } else if (this.track.expansionLevel === EcgTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            return this.ecgDetailTrackModel.canCollapse();
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
        if (this.track.expansionLevel === EcgTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.ecgSummaryTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        } else if (this.track.expansionLevel === EcgTrackUtils.MEASUREMENT_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.ecgMeasurementTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        } else if (this.track.expansionLevel === EcgTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.ecgDetailTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        }
    }
}
