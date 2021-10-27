import {FilterPipe} from './FilterPipe';

describe('GIVEN FilterPipe', () => {
    let pipe: FilterPipe;
    beforeEach(() => {
        pipe = new FilterPipe();
    });
    describe('WHEN elements are filtered', () => {

        it('THEN list of elements that that contain filter is returned', () => {
            expect(pipe.transform(['abc', 'bcd', 'cde'], ['b'])).toEqual(['abc', 'bcd']);
        });

        it('THEN full list is returned in case filter is empty', () => {
            expect(pipe.transform(['abc', 'bcd', 'cde'], [null])).toEqual(['abc', 'bcd', 'cde']);
        });
    });
});
