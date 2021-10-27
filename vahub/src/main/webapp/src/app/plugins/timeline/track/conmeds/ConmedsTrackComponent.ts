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
    Component,
    ChangeDetectionStrategy,
    Input,
    Output,
    EventEmitter,
    AfterViewInit,
    OnChanges,
    SimpleChange
} from '@angular/core';
import {ConmedsSummaryTrackModel} from './ConmedsSummaryTrackModel';
import {ConmedsClassTrackModel} from './ConmedsClassTrackModel';
import {ConmedsLevelTrackModel} from './ConmedsLevelTrackModel';
import {AbstractTrackComponent} from '../AbstractTrackComponent';
import {ConmedsTrackUtils} from './ConmedsTrackUtils';
import {IHighlightedPlotArea, ITrack, IZoom} from '../../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'conmeds-track',
    templateUrl: 'ConmedsTrackComponent.html',
    styleUrls: ['../TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConmedsTrackComponent extends AbstractTrackComponent implements OnChanges {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() zoom: IZoom;
    @Input() subjectHighlighted: boolean;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);
    @Output() cursorXCoordinate = new EventEmitter<number>();

    constructor(private conmedsSummaryTrackModel: ConmedsSummaryTrackModel,
                private conmedsClassTrackModel: ConmedsClassTrackModel,
                private conmedsLevelTrackModel: ConmedsLevelTrackModel) {
        super();
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        super.ngOnChanges(changes);
    }

    canExpand(): boolean {
        if (this.track.expansionLevel === ConmedsTrackUtils.CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL) {
            return this.conmedsSummaryTrackModel.canExpand();
        } else if (this.track.expansionLevel === ConmedsTrackUtils.MEDICATION_CLASS_TRACK_EXPANSION_LEVEL) {
            return this.conmedsClassTrackModel.canExpand();
        } else if (this.track.expansionLevel === ConmedsTrackUtils.MEDICATION_LEVEL_TRACK_EXPANSION_LEVEL) {
            return this.conmedsLevelTrackModel.canExpand();
        } else {
            return false;
        }
    }

    canCollapse(): boolean {
        if (this.track.expansionLevel === ConmedsTrackUtils.CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL) {
            return this.conmedsSummaryTrackModel.canCollapse();
        } else if (this.track.expansionLevel === ConmedsTrackUtils.MEDICATION_CLASS_TRACK_EXPANSION_LEVEL) {
            return this.conmedsClassTrackModel.canCollapse();
        } else if (this.track.expansionLevel === ConmedsTrackUtils.MEDICATION_LEVEL_TRACK_EXPANSION_LEVEL) {
            return this.conmedsLevelTrackModel.canCollapse();
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
        if (this.track.expansionLevel === ConmedsTrackUtils.CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.conmedsSummaryTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        } else {
            if (this.track.expansionLevel === ConmedsTrackUtils.MEDICATION_CLASS_TRACK_EXPANSION_LEVEL) {
                this.trackPlotDetails = this.conmedsClassTrackModel.createTrackPlotDetail(this.subjectId, this.track);
            } else {
                if (this.track.expansionLevel === ConmedsTrackUtils.MEDICATION_LEVEL_TRACK_EXPANSION_LEVEL) {
                    this.trackPlotDetails = this.conmedsLevelTrackModel.createTrackPlotDetail(this.subjectId, this.track);
                }
            }
        }
    }
}
