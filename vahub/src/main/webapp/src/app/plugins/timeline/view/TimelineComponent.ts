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

import {Component, ChangeDetectionStrategy, Input, OnInit, OnDestroy} from '@angular/core';
import {Store} from '@ngrx/store';
import {AppStore} from '../store/ITimeline';
import {FilterEventService} from '../../../filters/event/FilterEventService';
import {TimelineTrackService} from '../config/trackselection/TimelineTrackService';
import {AbstractTimelineComponent} from './AbstractTimelineComponent';
import {TrackDataService} from '../http/TrackDataService';
import {TimelineConfigurationService} from '../config/tracksconfig/TracksConfigurationService';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TimelineDispatcher} from '../store/dispatcher/TimelineDispatcher';
import {TimelineObservables} from '../store/observable/TimelineObservables';
import {ApplicationState} from '../../../common/store/models/ApplicationState';

@Component({
    selector: 'timeline',
    templateUrl: 'TimelineComponent.html',
    styleUrls: ['./TimelineComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineComponent extends AbstractTimelineComponent<TrackDataService> implements OnInit, OnDestroy {

    @Input() timelineId: string;
    // Flag for TimelineComponent.html template to change the view behaviour in case of SSV plugin
    isSingleSubjectComponent = false;

    constructor(_store: Store<ApplicationState>,
                dataService: TrackDataService,
                filterEventService: FilterEventService,
                trackService: TimelineTrackService,
                timelineConfigurationService: TimelineConfigurationService,
                trellisingDispatcher: TrellisingDispatcher,
                timelineDispatcher: TimelineDispatcher,
                public timelineObservables: TimelineObservables) {
        super(_store,
            dataService,
            filterEventService,
            trackService,
            timelineConfigurationService,
            trellisingDispatcher,
            timelineDispatcher,
            timelineObservables);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.calculateViewportHeight();
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    calculateViewportHeight(): void {
        this.maxHeight = 'calc(100vh - 315px)';
    }
}
