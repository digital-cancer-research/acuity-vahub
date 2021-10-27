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

import {OnInit, OnDestroy} from '@angular/core';
import {Store} from '@ngrx/store';
import {Subscription} from 'rxjs/Subscription';
import {AppStore, ISubject, ITrack} from './../store/ITimeline';
import {Timeline} from './../store/Timeline';
import {FilterEventService} from '../../../filters/event/FilterEventService';
import {TimelineTrackService} from '../config/trackselection/TimelineTrackService';
import {TrackDataService} from '../http/TrackDataService';
import {TimelineConfigurationService} from '../config/tracksconfig/TracksConfigurationService';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TimelineDispatcher} from '../store/dispatcher/TimelineDispatcher';
import {TimelineObservables} from '../store/observable/TimelineObservables';
import {ApplicationState} from '../../../common/store/models/ApplicationState';

export class AbstractTimelineComponent<T extends TrackDataService> implements OnInit, OnDestroy {

    timelineId: string;
    public timeline: Timeline<T>;
    public maxHeight = 'calc(100vh - 315px)';
    private trackServiceSubscription: Subscription;
    private spirometryYAxisValueSubscription: Subscription;
    private ecgYAxisValueSubscription: Subscription;
    private ecgWarningsSubscription: Subscription;
    private vitalsYAxisValueSubscription: Subscription;
    private labsYAxisValueSubscription: Subscription;
    public modalVisible = false;

    constructor(protected _store: Store<ApplicationState>,
                protected dataService: T,
                protected filterEventService: FilterEventService,
                protected trackService: TimelineTrackService,
                protected timelineConfigurationService: TimelineConfigurationService,
                protected trellisingDispatcher: TrellisingDispatcher,
                protected timelineDispatcher: TimelineDispatcher,
                public timelineObservables: TimelineObservables) {
    }

    ngOnInit(): void {
        this.timeline = new Timeline<T>(
            this.timelineId,
            this._store,
            this.dataService,
            this.filterEventService,
            this.trellisingDispatcher,
            this.timelineDispatcher,
            this.timelineObservables
        );
        this.timeline.init();
        this.linkToTrackService();
        this.linkToConfigurationService();
        this.listenToSubjectChanges();
    }

    linkToTrackService(): void {
        this.trackServiceSubscription = this.trackService.updatedTrack.subscribe((allTracks) =>
            this.timeline.updateTrackSelection(allTracks)
        );
    }

    listenToSubjectChanges(): void {
        this.timeline.subjects.subscribe((subjects) => {
            this.trackService.tracksWithExpansion = subjects.reduce((vis: any, subject: ISubject) => {
                subject.tracks.forEach((track: ITrack) => {
                    vis[track.name + track.expansionLevel] = track.selected;
                });
                return vis;
            }, {});
        });
    }


    linkToConfigurationService(): void {
        this.spirometryYAxisValueSubscription = this.timelineConfigurationService.updateSpirometryYAxisValue.subscribe((value) =>
            this.timeline.updateSpirometryYAxisValue(value)
        );
        this.ecgYAxisValueSubscription = this.timelineConfigurationService.updateEcgYAxisValue.subscribe((value) =>
            this.timeline.updateEcgYAxisValue(value)
        );
        this.ecgWarningsSubscription = this.timelineConfigurationService.updateEcgWarnings.subscribe((value) =>
            this.timeline.updateEcgWarnings(value)
        );
        this.vitalsYAxisValueSubscription = this.timelineConfigurationService.updateVitalsYAxisValue.subscribe((value) =>
            this.timeline.updateVitalsYAxisValue(value)
        );
        this.labsYAxisValueSubscription = this.timelineConfigurationService.updateLabsYAxisValue.subscribe((value) =>
            this.timeline.updateLabsYAxisValue(value)
        );
    }

    toggleModal(): void {
        this.modalVisible = !this.modalVisible;
    }

    ngOnDestroy(): void {
        if (this.trackServiceSubscription) {
            this.trackServiceSubscription.unsubscribe();
        }
        if (this.spirometryYAxisValueSubscription) {
            this.spirometryYAxisValueSubscription.unsubscribe();
        }
        if (this.ecgYAxisValueSubscription) {
            this.ecgYAxisValueSubscription.unsubscribe();
        }
        if (this.ecgWarningsSubscription) {
            this.ecgWarningsSubscription.unsubscribe();
        }
        if (this.vitalsYAxisValueSubscription) {
            this.vitalsYAxisValueSubscription.unsubscribe();
        }
        if (this.labsYAxisValueSubscription) {
            this.labsYAxisValueSubscription.unsubscribe();
        }
        this.timeline.destroy();
    }
}
