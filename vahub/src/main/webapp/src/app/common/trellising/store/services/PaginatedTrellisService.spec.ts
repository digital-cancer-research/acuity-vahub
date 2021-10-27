import {fromJS, List} from 'immutable';

import {PaginatedTrellisService} from './PaginatedTrellisService';
import {ITrellises, TrellisCategory, PlotType, IPlot, YAxisParameters} from '../ITrellising';

describe('GIVEN PaginatedTrellisService', () => {
    let trellisingOptions: List<ITrellises>;
    let service: PaginatedTrellisService;

    beforeEach(() => {
        trellisingOptions = fromJS([
            {
                category: TrellisCategory.MANDATORY_TRELLIS,
                trellisedBy: YAxisParameters.MEASUREMENT,
                trellisOptions: ['PR', 'HR']
            },
            {
                category: TrellisCategory.NON_MANDATORY_SERIES,
                trellisedBy: 'ARM',
                trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
            }
        ]);

        service = new PaginatedTrellisService();
    });

    describe('WHEN the pagination limit is less than possible combinations', () => {
        let expectedTrellis: List<ITrellises>;

        beforeEach(() => {
            expectedTrellis = fromJS([
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOptions: ['PR', 'HR']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
                }
            ]);
        });

        it('THEN only return possible combinations to fill limit', () => {
            expect(PaginatedTrellisService.paginatedTrellis(2, 1, trellisingOptions)).toEqual(expectedTrellis);
        });

    });

    describe('WHEN the pagination limit is less than possible combinations and has an offset', () => {
        let expectedTrellis: List<ITrellises>;

        beforeEach(() => {
            expectedTrellis = fromJS([
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOptions: ['HR']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
                }
            ]);
        });

        it('THEN only return possible combinations to fill limit and offset', () => {
            expect(PaginatedTrellisService.paginatedTrellis(2, 2, trellisingOptions)).toEqual(expectedTrellis);
        });
    });

    describe('AND a paginated trellis is generated', () => {
        let trellisingOpts: List<ITrellises>;

        beforeEach(() => {
            trellisingOpts = fromJS([
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

        describe('WHEN the pagination limit exceeds possible combinations', () => {
            it('THEN does nothing ', () => {
                expect(PaginatedTrellisService.paginatedTrellis(100, 1, trellisingOpts)).toEqual(trellisingOpts);
            });
        });

        describe('WHEN the pagination limit is less than possible combinations', () => {
            let expectedTrellis: List<ITrellises>;

            beforeEach(() => {
                expectedTrellis = fromJS([
                    {
                        category: TrellisCategory.MANDATORY_TRELLIS,
                        trellisedBy: YAxisParameters.MEASUREMENT,
                        trellisOptions: ['PR']
                    },
                    {
                        category: TrellisCategory.NON_MANDATORY_TRELLIS,
                        trellisedBy: 'ARM',
                        trellisOptions: ['Placebo', 'Drug 1']
                    }
                ]);
            });

            it('THEN only return possible combinations to fill limit', () => {
                expect(PaginatedTrellisService.paginatedTrellis(2, 1, trellisingOpts)).toEqual(expectedTrellis);
            });

        });

        describe('WHEN the pagination limit is less than possible combinations and has an offset', () => {
            let expectedTrellis: List<ITrellises>;

            beforeEach(() => {
                expectedTrellis = fromJS([
                    {
                        category: TrellisCategory.MANDATORY_TRELLIS,
                        trellisedBy: YAxisParameters.MEASUREMENT,
                        trellisOptions: ['PR', 'HR']
                    },
                    {
                        category: TrellisCategory.NON_MANDATORY_TRELLIS,
                        trellisedBy: 'ARM',
                        trellisOptions: ['Drug 2', 'Placebo']
                    }
                ]);
            });

            it('THEN only return possible combinations to fill limit and offset', () => {
                expect(PaginatedTrellisService.paginatedTrellis(2, 3, trellisingOpts)).toEqual(expectedTrellis);
            });
        });
    });
});
