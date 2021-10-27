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

import {Injectable} from '@angular/core';
import {List} from 'immutable';

import {ITrack, TrackName} from '../../store/ITimeline';
import {chain, max, forEach, isEmpty, isNull, each, isEqual, map} from 'lodash';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {Subscription} from 'rxjs/Subscription';

@Injectable()
export class TimelineTrackService {
    public tracks: Observable<List<ITrack>>;
    public loading: Observable<boolean>;
    public updatedTrack: Subject<{[trackName: string]: any}> = new Subject<{[trackName: string]: any}>();
    public updatedSelectedTracks: Subject<TrackName[]> = new Subject<TrackName[]>();
    public tracksWithExpansion: any;
    public allTracks: { [trackName: string]: any } = {};
    private dict: { [id: string]: Subscription } = {};
    private trackSelection: { [trackName: string]: boolean} = {};

    constructor() {
    }

    initialiseTracks(initialTracks: { [trackName: string]: any }, setOrder: boolean): void {
        this.allTracks = initialTracks;
        if (setOrder) {
            this.updateOrderForAllTracks(null);
        }
    }

    updateTrackSelectedAndOrderStatus(updatedTracks: ITrack[]): void {
        const currentSelectedTracks = this.getSelectedTrackNames();
        const updatedTrackNames = map(updatedTracks, 'name').sort();
        if (!isEqual(updatedTrackNames, currentSelectedTracks)) {
            this.unselectAllTracks();
            each(updatedTracks, (t: ITrack) => {
                this.allTracks[t.name].selected = true;
                this.allTracks[t.name].order = t.order;
            });
        }
    }

    removeTracksSubscription(): void {
        for (const trackName in this.trackSelection) {
            if (this.trackSelection.hasOwnProperty(trackName)) {
                this.trackSelection[trackName] = false;
            }
        }
    }

    addSelectedTracks(track: ITrack): void {
        if (this.isFirstTrackToBeSelected()) {
            this.updateOrderForTrack(track, 1);
        } else if (this.isTrackBeingRemoved(track)) {
            this.updateOrderForAllTracks(track);
        } else {
            const order = this.calculateNextOrder();
            this.updateOrderForTrack(track, this.calculateNextOrder());
        }
    }

    updateSelectedTracks(): void {
        const sendTracks: { [trackName: string]: any } = {};
        for (const track in this.allTracks) {
            if (this.allTracks[track].changed) {
                sendTracks[track] = this.allTracks[track];
                this.allTracks[track].changed = false;
            }
        }
        this.updatedTrack.next(sendTracks);
    }

    isTrackSelected(track: ITrack): boolean {
        return this.allTracks[track.name].selected;
    }

    /**
     * Adds a new Subscription but removes and cancels an old one because of a bug with ngOnDestroy
     */
    addSubscription(key: string, sub: Subscription): void {
        const gotSub = this.dict[key];
        if (gotSub) {
            gotSub.unsubscribe();
            delete this.dict[key];
        }
        this.dict[key] = sub;
    }

    private updateOrderForTrack(track: ITrack, order: number): void {
        this.allTracks[track.name] = {
            track: track.set('order', order),
            changed: true,
            order: order,
            selected: !isNull(order)
        };
    }

    private isFirstTrackToBeSelected(): boolean {
        const orders = chain(this.allTracks).values().map('order').uniq().value();
        return orders.length === 1 && isEmpty(orders[0]);
    }

    private isTrackBeingRemoved(track: ITrack): boolean {
        const tracksBeingRemoved = chain(this.allTracks)
            .values()
            .filter((value: any) => value.track.name === track.name && !isNull(value.order))
            .value();
        return tracksBeingRemoved.length > 0;
    }

    private calculateNextOrder(): number {
        const orders = chain(this.allTracks).values().map('order').value();
        return 1 + <number> max(orders);
    }

    private updateOrderForAllTracks(trackToRemove: ITrack): void {
        let orderNumber = 0;
        forEach(this.getSelectedTracksInOrder(), (trackRecord: any) => {
            let order: number = null;
            if (isEmpty(trackToRemove) || trackRecord.track.name !== trackToRemove.name) {
                order = ++orderNumber;
            }
            this.updateOrderForTrack(trackRecord.track, order);
        });
    }

    private getSelectedTracksInOrder(): any {
        return chain(this.allTracks)
            .values()
            .filter((track: any) => track.selected)
            .sortBy('order')
            .value();
    }

    private getSelectedTrackNames(): string[] {
        return <string[]> chain(this.allTracks)
            .values()
            .filter((t: ITrack) => t.selected)
            .map('track.name')
            .sort()
            .value();
    }

    private unselectAllTracks(): void {
        chain(this.allTracks)
            .values()
            .each((t: ITrack) => {
                t.selected = false;
                t.order = null;
            })
            .value();
    }
}
