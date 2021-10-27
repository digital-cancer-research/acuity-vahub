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

import {fromJS} from 'immutable';

import {TimelineTrackService} from './TimelineTrackService';
import {ITrack, TrackName, TrackRecord, TrackSelectionRecord} from '../../store/ITimeline';

describe('GIVEN TimelineTrackService', () => {

    let service: TimelineTrackService;

    beforeEach(() => {
        service = new TimelineTrackService();
        service.allTracks = generateTracks();
    });

    // For cases when the initial state sets the order, but some tracks aren't displayed
    describe('WHEN initialising the tracks AND some tracks have no data', () => {

        it('THEN subsequent tracks have their order set correctly', () => {
            const initialTracks = generateInitialLoadingState();

            service.initialiseTracks(initialTracks, true);

            expect(service.allTracks[TrackName.SUMMARY].order).toBe(1);
            expect(service.allTracks[TrackName.DOSE].order).toBe(2);
            expect(service.allTracks[TrackName.AES].order).toBe(3);
            expect(service.allTracks[TrackName.LABS].order).toBe(4);
            expect(service.allTracks[TrackName.CONMEDS].order).toBe(5);
            expect(service.allTracks[TrackName.VITALS].order).toBe(6);
            expect(service.allTracks[TrackName.ECG].order).toBe(7);
        });
    });

    // For cases when switching between the timeline & ssv timeline
    describe('WHEN updating the track selection and order status', () => {
        it('THEN the selection status is updated', () => {
            service.addSelectedTracks(service.allTracks[TrackName.SUMMARY].track);
            service.addSelectedTracks(service.allTracks[TrackName.AES].track);
            service.addSelectedTracks(service.allTracks[TrackName.LABS].track);

            service.updateTrackSelectedAndOrderStatus(<any[]> [{
                name: TrackName.AES,
                order: 2
            }, {
                name: TrackName.SUMMARY,
                order: 1
            }]);

            expect(service.allTracks[TrackName.SUMMARY].selected).toBe(true);
            expect(service.allTracks[TrackName.AES].selected).toBe(true);
            expect(service.allTracks[TrackName.LABS].selected).toBe(false);
        });

        it('THEN the order status is updated', () => {
            service.addSelectedTracks(service.allTracks[TrackName.SUMMARY].track);
            service.addSelectedTracks(service.allTracks[TrackName.AES].track);
            service.addSelectedTracks(service.allTracks[TrackName.LABS].track);

            service.updateTrackSelectedAndOrderStatus(<any[]> [{
                name: TrackName.AES,
                order: 2
            }, {
                name: TrackName.SUMMARY,
                order: 1
            }]);

            expect(service.allTracks[TrackName.SUMMARY].order).toBe(1);
            expect(service.allTracks[TrackName.AES].order).toBe(2);
            expect(service.allTracks[TrackName.LABS].order).toBe(null);
        });
    });

    describe('WHEN the first track is added', () => {

        beforeEach(() => {
            service.addSelectedTracks(service.allTracks[TrackName.LABS].track);
        });

        it('THEN the order is set', () => {
            expect(service.allTracks[TrackName.LABS].order).toBe(1);
        });

        it('THEN the selection status is set', () => {
            expect(service.allTracks[TrackName.LABS].selected).toBe(true);
        });
    });

    describe('WHEN the second track is added', () => {

        beforeEach(() => {
            service.addSelectedTracks(service.allTracks[TrackName.LABS].track);
            service.addSelectedTracks(service.allTracks[TrackName.AES].track);
        });

        it('THEN the order is set', () => {
            expect(service.allTracks[TrackName.LABS].order).toBe(1);
            expect(service.allTracks[TrackName.AES].order).toBe(2);
        });

        it('THEN the selection status is set', () => {
            expect(service.allTracks[TrackName.LABS].selected).toBe(true);
            expect(service.allTracks[TrackName.AES].selected).toBe(true);
        });
    });

    describe('WHEN the third track is added', () => {

        beforeEach(() => {
            service.addSelectedTracks(service.allTracks[TrackName.LABS].track);
            service.addSelectedTracks(service.allTracks[TrackName.DOSE].track);
            service.addSelectedTracks(service.allTracks[TrackName.AES].track);
        });

        it('THEN the order is set', () => {
            expect(service.allTracks[TrackName.LABS].order).toBe(1);
            expect(service.allTracks[TrackName.DOSE].order).toBe(2);
            expect(service.allTracks[TrackName.AES].order).toBe(3);
        });

        it('THEN the selection status is set', () => {
            expect(service.allTracks[TrackName.LABS].selected).toBe(true);
            expect(service.allTracks[TrackName.DOSE].selected).toBe(true);
            expect(service.allTracks[TrackName.AES].selected).toBe(true);
        });
    });

    describe('WHEN the first track is removed', () => {

        beforeEach(() => {
            service.addSelectedTracks(service.allTracks[TrackName.LABS].track);
            service.addSelectedTracks(service.allTracks[TrackName.DOSE].track);
            service.addSelectedTracks(service.allTracks[TrackName.AES].track);

            service.addSelectedTracks(service.allTracks[TrackName.LABS].track);
        });

        it('THEN the order is updated', () => {
            expect(service.allTracks[TrackName.LABS].order).toBe(null);
            expect(service.allTracks[TrackName.DOSE].order).toBe(1);
            expect(service.allTracks[TrackName.AES].order).toBe(2);
        });

        it('THEN the selection status is set', () => {
            expect(service.allTracks[TrackName.LABS].selected).toBe(false);
            expect(service.allTracks[TrackName.DOSE].selected).toBe(true);
            expect(service.allTracks[TrackName.AES].selected).toBe(true);
        });
    });

    describe('WHEN the second track is removed', () => {

        beforeEach(() => {
            service.addSelectedTracks(service.allTracks[TrackName.LABS].track);
            service.addSelectedTracks(service.allTracks[TrackName.DOSE].track);
            service.addSelectedTracks(service.allTracks[TrackName.AES].track);

            service.addSelectedTracks(service.allTracks[TrackName.DOSE].track);
        });

        it('THEN the order is updated', () => {
            expect(service.allTracks[TrackName.LABS].order).toBe(1);
            expect(service.allTracks[TrackName.DOSE].order).toBe(null);
            expect(service.allTracks[TrackName.AES].order).toBe(2);
        });

        it('THEN the selection status is set', () => {
            expect(service.allTracks[TrackName.LABS].selected).toBe(true);
            expect(service.allTracks[TrackName.DOSE].selected).toBe(false);
            expect(service.allTracks[TrackName.AES].selected).toBe(true);
        });
    });

    describe('WHEN the third track is removed', () => {

        beforeEach(() => {
            service.addSelectedTracks(service.allTracks[TrackName.LABS].track);
            service.addSelectedTracks(service.allTracks[TrackName.DOSE].track);
            service.addSelectedTracks(service.allTracks[TrackName.AES].track);

            service.addSelectedTracks(service.allTracks[TrackName.AES].track);
        });

        it('THEN the order is updated', () => {
            expect(service.allTracks[TrackName.LABS].order).toBe(1);
            expect(service.allTracks[TrackName.DOSE].order).toBe(2);
            expect(service.allTracks[TrackName.AES].order).toBe(null);
        });

        it('THEN the selection status is set', () => {
            expect(service.allTracks[TrackName.LABS].selected).toBe(true);
            expect(service.allTracks[TrackName.DOSE].selected).toBe(true);
            expect(service.allTracks[TrackName.AES].selected).toBe(false);
        });
    });
});

function generateInitialLoadingState(): any {
    const tracks: { [trackName: string]: any } = {};
    tracks[TrackName.SUMMARY] = {name: TrackName.SUMMARY, expansionLevel: 1, selected: true, order: 1, data: null, changed: true};
    tracks[TrackName.SUMMARY].track = new TrackRecord(tracks[TrackName.SUMMARY]);
    tracks[TrackName.DOSE] = {name: TrackName.DOSE, expansionLevel: 2, selected: true, order: 2, data: null, changed: true};
    tracks[TrackName.DOSE].track = new TrackRecord(tracks[TrackName.DOSE]);
    tracks[TrackName.AES] = {name: TrackName.AES, expansionLevel: 2, selected: true, order: 3, data: null, changed: true};
    tracks[TrackName.AES].track = new TrackRecord(tracks[TrackName.AES]);
    tracks[TrackName.LABS] = {name: TrackName.LABS, expansionLevel: 3, selected: true, order: 5, data: null, changed: true};
    tracks[TrackName.LABS].track = new TrackRecord(tracks[TrackName.LABS]);
    tracks[TrackName.CONMEDS] = {name: TrackName.CONMEDS, expansionLevel: 3, selected: true, order: 7, data: null, changed: true};
    tracks[TrackName.CONMEDS].track = new TrackRecord(tracks[TrackName.CONMEDS]);
    tracks[TrackName.VITALS] = {name: TrackName.VITALS, expansionLevel: 2, selected: true, order: 9, data: null, changed: true};
    tracks[TrackName.VITALS].track = new TrackRecord(tracks[TrackName.VITALS]);
    tracks[TrackName.ECG] = {name: TrackName.ECG, expansionLevel: 2, selected: true, order: 10, data: null, changed: true};
    tracks[TrackName.ECG].track = new TrackRecord(tracks[TrackName.ECG]);
    return tracks;
}

function generateTracks(): any {
    const tracks: { [trackName: string]: any } = {};
    tracks[TrackName.AES] = {
        track: new TrackRecord({
            name: 'Adverse events',
            expansionLevel: 1,
            selected: false,
            order: null,
            data: null,
            changed: true
        }),
        selected: false,
        changed: false,
        order: null
    };
    tracks[TrackName.CONMEDS] = {
        track: new TrackRecord({
            name: 'Conmeds',
            expansionLevel: 1,
            selected: false,
            order: null,
            data: null,
            changed: true
        }),
        selected: false,
        changed: false,
        order: null
    };
    tracks[TrackName.DOSE] = {
        track: new TrackRecord({
            name: 'Dose',
            expansionLevel: 1,
            selected: false,
            order: null,
            data: null,
            changed: true
        }),
        selected: false,
        changed: false,
        order: null
    };
    tracks[TrackName.ECG] = {
        track: new TrackRecord({
            name: 'ECG',
            expansionLevel: 1,
            selected: false,
            order: null,
            data: null,
            changed: true
        }),
        selected: false,
        changed: false,
        order: null
    };
    tracks[TrackName.LABS] = {
        track: new TrackRecord({
            name: 'Lab measurements',
            expansionLevel: 1,
            selected: false,
            order: null,
            data: null,
            changed: true
        }),
        selected: false,
        changed: false,
        order: null
    };
    tracks[TrackName.SUMMARY] = {
        track: new TrackRecord({
            name: 'Status summary',
            expansionLevel: 1,
            selected: true,
            order: 1,
            data: null,
            changed: true
        }),
        selected: true,
        changed: false,
        order: null
    };
    tracks[TrackName.VITALS] = {
        track: new TrackRecord({
            name: 'Vitals',
            expansionLevel: 1,
            selected: false,
            order: null,
            data: null,
            changed: true
        }),
        selected: false,
        changed: false,
        order: null
    };
    return tracks;
}
