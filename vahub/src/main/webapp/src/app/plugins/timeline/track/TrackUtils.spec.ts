import {TestBed, inject} from '@angular/core/testing';
import {TrackUtils} from './TrackUtils';
import {HttpClient} from '@angular/common/http';

describe('GIVEN a TrackUtils class', () => {
    let trackUtils;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                TrackUtils,
                HttpClient,
            ]
        });
    });

    beforeEach(inject([TrackUtils], (_trackUtils: TrackUtils) => {
        trackUtils = _trackUtils;
    }));

    describe('WHEN getting diff', () => {
        it('SHOULD return diff multiplied by multiplier', () => {
            const diff = TrackUtils.getDiff(1, 101, 0.05);

            expect(diff).toEqual(5);
        });
    });
});
