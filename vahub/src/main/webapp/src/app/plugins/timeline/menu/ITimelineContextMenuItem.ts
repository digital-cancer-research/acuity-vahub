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

import {Subject} from 'rxjs/Subject';
import {TrackName} from '../store/ITimeline';

export interface ITimelineContextMenuItem {
    title: TimelineContextMenuOptions;
    enabled: boolean;
    action?: Subject<any>;
}

export enum TimelineContextMenuOptions {
    EXPAND_MENU_ITEM = <any>'Expand ...',
    EXPAND_ALL_MENU_ITEM = <any>'Expand all ',
    COLLAPSE_MENU_ITEM = <any>'Collapse ...',
    COLLAPSE_ALL_MENU_ITEM = <any>'Collapse all '
}

export interface ITrackExpansion {
    subjectId?: string;
    track: TrackName;
    expansionLevel: number;
    expand: boolean;
}
