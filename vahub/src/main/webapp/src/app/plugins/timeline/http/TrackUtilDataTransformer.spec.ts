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

import {inject, TestBed} from '@angular/core/testing';
import {TrackUtilDataTransformer} from './TrackUtilDataTransformer';
import {TrackName, ITrack, TrackRecord} from '../store/ITimeline';

describe('GIVEN a TrackUtilDataTransformer', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                TrackUtilDataTransformer
            ]
        });
    });

    describe('WHEN transforming possible tracks', () => {

        const simpleDataFromServer: string[] = ['STATUS_SUMMARY', 'AES', 'EXACERBATIONS', 'BLA-BLA'];

        it('THEN it should just return the correct tracks',
            inject([TrackUtilDataTransformer], (restDataTransformer: TrackUtilDataTransformer) => {
                const transformedDataArray: Array<ITrack> = [];
                transformedDataArray.push(<ITrack>new TrackRecord({
                    name: TrackName.SUMMARY,
                    order: 1,
                    expansionLevel: 1,
                    selected: false
                }));
                transformedDataArray.push(<ITrack>new TrackRecord({
                    name: TrackName.AES,
                    order: 2,
                    expansionLevel: 1,
                    selected: false
                }));
                transformedDataArray.push(<ITrack>new TrackRecord({
                    name: TrackName.EXACERBATION,
                    order: 3,
                    expansionLevel: 1,
                    selected: false
                }));

                const transformedData = restDataTransformer.transformPossibleTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });
});
