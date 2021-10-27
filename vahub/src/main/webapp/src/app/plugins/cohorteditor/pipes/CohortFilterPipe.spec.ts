import {CohortFilterPipe} from './CohortFilterPipe';

describe('GIVEN CohortFilterPipe', () => {
    let pipe: CohortFilterPipe;
    const savedFilters = [{
        savedFilter: {
            name: 'abc'
        }
    },
        {
            savedFilter: {
                name: 'bcd'
            }
        },
        {
            savedFilter: {
                name: 'cde'
            }
        }];

    beforeEach(() => {
        pipe = new CohortFilterPipe();
    });

    describe('WHEN elements are filtered', () => {

        it('THEN list of elements that that contain filter is returned', () => {
            expect(pipe.transform(savedFilters, ['b'])).toEqual([savedFilters[0], savedFilters[1]]);
        });

        it('THEN full list is returned in case filter is empty', () => {
            expect(pipe.transform(savedFilters, [null])).toEqual(savedFilters);
        });
    });
});
