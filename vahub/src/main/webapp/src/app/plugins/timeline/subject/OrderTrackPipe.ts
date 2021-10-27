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

import { Pipe, PipeTransform } from '@angular/core';
import {ITrack} from '../store/ITimeline';
import {List} from 'immutable';
import * as  _ from 'lodash';

@Pipe({
    name: 'ordertrack'
})
export class OrderTrackPipe implements PipeTransform {
    /**
     * Sort tracks by order value or by name if order value isn't provided
     * (e. g. when Show on Timeline button is used on one of Respiratory tabs requiring more than 1 Timeline track)
     * @param tracks - tracks to sort
     */
    transform(tracks: List<ITrack>): ITrack[] {
        return _.sortBy(tracks.toArray(), 'order', 'name');
    }
}
