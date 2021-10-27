import {TimelinePaginationService} from './TimelinePaginationService';

describe('GIVEN TimelinePaginationService', () => {
    describe('WHEN getting updated page set', () => {
        it('THEN returns single page for less than limit', () => {
            expect(TimelinePaginationService.getPages(1, 20)).toEqual([1]);
        });
        it('THEN returns single page for equal to limit', () => {
            expect(TimelinePaginationService.getPages(20, 20)).toEqual([1]);
        });
        it('THEN returns multiple pages for over the limit', () => {
            expect(TimelinePaginationService.getPages(21, 20)).toEqual([1, 2]);
        });
    });
});
