import {fromJS, List} from 'immutable';
import {PagesService} from './PagesService';
import {ITrellises, TrellisCategory, IPage} from '../ITrellising';
import {YAxisParameters} from '../index';

describe('GIVEN a paginated trellis', () => {
    let trellisingOptions: List<ITrellises>;

    beforeEach(() => {
        trellisingOptions = fromJS([
            {
                category: TrellisCategory.MANDATORY_TRELLIS,
                trellisedBy: YAxisParameters.MEASUREMENT,
                trellisOptions: ['PR', 'HR', 'QTCF']
            },
            {
                category: TrellisCategory.NON_MANDATORY_TRELLIS,
                trellisedBy: 'ARM',
                trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
            }
        ]);
    });
    describe('WHEN pages() method is called', () => {
        describe('AND the pages are generated with limit 1', () => {
            it('THEN produces list of pages', () => {
                expect(PagesService.pages(trellisingOptions, 1)).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9]);
            });
        });

        describe('AND the pages are generated with limit which plot count not divisible by', () => {
            it('THEN produces list of pages', () => {
                expect(PagesService.pages(trellisingOptions, 2)).toEqual([1, 2, 3, 4, 5]);
            });
        });

        describe('AND the pages are generated with limit which plot count divisible by', () => {
            it('THEN produces list of pages', () => {
                expect(PagesService.pages(trellisingOptions, 3)).toEqual([1, 2, 3]);
            });
        });
    });

    describe('WHEN page() method is called', () => {
        describe('AND page object is null or undefined', () => {
            it('THEN null is returned', () => {
                expect(PagesService.page(null, 0)).toBeNull();
            });
        });

        describe('AND the page object is not null', () => {
            it('THEN current page is returned', () => {
                expect(PagesService.page(2, 10)).toBe(5);
                expect(PagesService.page(2, 5)).toBe(3);
            });
        });

    });
});
