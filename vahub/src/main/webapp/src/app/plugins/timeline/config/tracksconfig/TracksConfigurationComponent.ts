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

import {Component, OnInit, OnDestroy} from '@angular/core';
import {TimelineConfigurationService} from './TracksConfigurationService';
import {
    TrackName
} from '../../store/ITimeline';
import {Subscription} from 'rxjs/Subscription';
import {TimelineTrackService} from '../trackselection/TimelineTrackService';
import * as  _ from 'lodash';
import {TimelineObservables} from '../../store/observable/TimelineObservables';

@Component({
    selector: 'tracks-config',
    templateUrl: 'TracksConfigurationComponent.html',
    styleUrls: ['../../../../filters/filters.css'],
})
export class TracksConfigurationComponent implements OnInit, OnDestroy {
    private updateSelectedTrackskListener: Subscription;
    public timelineSelectedTracks: TrackName[] = [];
    isComponentHidden = true;
    trackName = TrackName;

    constructor(public timelineConfigurationService: TimelineConfigurationService,
                private timelineTrackService: TimelineTrackService,
                public timelineObservables: TimelineObservables) {
    }

    ngOnInit(): void {
        this.listenToTrackUpdates();
    }

    ngOnDestroy(): void {
        if (this.updateSelectedTrackskListener) {
            this.updateSelectedTrackskListener.unsubscribe();
        }
    }

    isVisible(trackName: TrackName, expansionLevel: any): boolean {
        if (!expansionLevel) {
            return this.timelineSelectedTracks.indexOf(trackName) !== -1;
        } else {
            return this.timelineTrackService.tracksWithExpansion && this.timelineTrackService.tracksWithExpansion[trackName.toString() + expansionLevel];
        }
    }

    listenToTrackUpdates(): void {
        this.updateSelectedTrackskListener = this.timelineTrackService.updatedSelectedTracks.subscribe((tracks: TrackName[]) => {
            this.timelineSelectedTracks = tracks;
            this.isComponentHidden = _.isEmpty(_.intersection(this.timelineSelectedTracks, [TrackName.LABS, TrackName.ECG, TrackName.VITALS, TrackName.SPIROMETRY]));
        });
    }
}
