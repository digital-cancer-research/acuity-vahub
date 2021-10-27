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
    OnChanges,
    SimpleChange,
    AfterViewInit
} from '@angular/core';
import {AesSummaryTrackModel} from './AesSummaryTrackModel';
import {AesDetailTrackModel} from './AesDetailTrackModel';
import {AbstractTrackComponent} from '../AbstractTrackComponent';
import {AesTrackUtils} from './AesTrackUtils';
import {IHighlightedPlotArea, ITrack, IZoom} from '../../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'aes-track',
    templateUrl: 'AesTrackComponent.html',
    styleUrls: ['../TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AesTrackComponent extends AbstractTrackComponent implements OnChanges {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() zoom: IZoom;
    @Input() subjectHighlighted: boolean;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);

    constructor(private aesSummaryTrackModel: AesSummaryTrackModel,
                private aesDetailTrackModel: AesDetailTrackModel) {
        super();
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        super.ngOnChanges(changes);
    }

    canExpand(): boolean {
        if (this.track.expansionLevel === AesTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.aesSummaryTrackModel.canExpand();
        } else if (this.track.expansionLevel === AesTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            return this.aesDetailTrackModel.canExpand();
        } else {
            return false;
        }
    }

    canCollapse(): boolean {
        if (this.track.expansionLevel === AesTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            return this.aesSummaryTrackModel.canCollapse();
        } else if (this.track.expansionLevel === AesTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            return this.aesDetailTrackModel.canCollapse();
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
        if (this.track.expansionLevel === AesTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.aesSummaryTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        } else if (this.track.expansionLevel === AesTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL) {
            this.trackPlotDetails = this.aesDetailTrackModel.createTrackPlotDetail(this.subjectId, this.track);
        }
    }
}
