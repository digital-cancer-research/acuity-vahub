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
