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
import {Router, NavigationEnd} from '@angular/router';
import {TimelineTrackService} from './TimelineTrackService';
import {ITrack, TimelineId} from '../../store/ITimeline';
import {TimelineObservables} from '../../store/observable/TimelineObservables';
import {Subscription} from 'rxjs/Subscription';

@Component({
    selector: 'timeline-track-selector',
    templateUrl: 'TimelineTrackSelectionComponent.html',
    styleUrls: ['../../../../filters/filters.css', './TimelineTrackSelectionComponent.css']
})
export class TimelineTrackSelectionComponent implements OnInit, OnDestroy {
    private tracksSubscription: Subscription;

    constructor(public trackService: TimelineTrackService,
                public timelineObservables: TimelineObservables,
            private router: Router) {
    }

    ngOnInit(): void {
        this.trackService.removeTracksSubscription();
        this.subscribeToTracks();
        this.router.events.subscribe((event: any) => {
            if (event instanceof NavigationEnd && event.url.indexOf('timeline') > -1) {
                this.timelineObservables.setTimelineId(TimelineId.COMPARE_SUBJECTS);
                const displayedTracks = this.timelineObservables.getCurrentDisplayedTracks();
                this.trackService.updateTrackSelectedAndOrderStatus(displayedTracks);
            }
        });
    }

    ngOnDestroy(): void {
        if (this.tracksSubscription) {
            this.tracksSubscription.unsubscribe();
        }
    }

    addToSelected(track: ITrack): void {
        this.trackService.addSelectedTracks(track);
    }

    applySelectedTracks(): void {
        this.trackService.updateSelectedTracks();
    }

    private subscribeToTracks(): void {
        this.tracksSubscription = this.timelineObservables.tracks.subscribe((tracks) => {
            const trackNames = tracks.filter((state) => {
                return state.selected;
            }).map(state => state.name).toArray();
            this.trackService.updatedSelectedTracks.next(trackNames);

            const allTracks: { [trackName: string]: any } = {};
            for (let i = 0; i < tracks.size; i++) {
                allTracks[tracks.get(i).name] = {
                    track: tracks.get(i),
                    selected: tracks.get(i).selected,
                    changed: false,
                    order: tracks.get(i).order
                };
            }
            this.trackService.initialiseTracks(allTracks, window.location.hash.indexOf('singlesubject') > -1);
        });
    }
}
