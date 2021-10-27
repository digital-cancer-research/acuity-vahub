import {OrderByPipe} from './OrderByPipe';

describe('GIVEN OrderByPipe', () => {
    let pipe: OrderByPipe;
    beforeEach(() => {
        pipe = new OrderByPipe();
    });
    describe('WHEN elements are sorted', () => {

        it('THEN alphabetically sorted list is returned', () => {
            expect(pipe.transform(['bcd', 'abc', 'cde'])).toEqual(['abc', 'bcd', 'cde']);
        });
    });
});
