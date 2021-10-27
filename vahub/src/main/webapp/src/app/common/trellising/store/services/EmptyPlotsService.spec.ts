import {fromJS, List} from 'immutable';

import {EmptyPlotsService} from './EmptyPlotsService';
import {ITrellises, TrellisCategory, IPlot, YAxisParameters} from '../ITrellising';

describe('GIVEN EmptyPlotsService', () => {

    let trellisingOptions: List<ITrellises>;
    let seriesOnlyTrellisingOptions: List<ITrellises>;

    beforeEach(() => {
        trellisingOptions = fromJS([
            {
                category: TrellisCategory.MANDATORY_TRELLIS,
                trellisedBy: YAxisParameters.MEASUREMENT,
                trellisOptions: ['PR', 'HR']
            },
            {
                category: TrellisCategory.NON_MANDATORY_TRELLIS,
                trellisedBy: 'ARM',
                trellisOptions: ['Placebo', 'Drug 1']
            }
        ]);

        seriesOnlyTrellisingOptions = fromJS([
            {
                category: TrellisCategory.NON_MANDATORY_SERIES,
                trellisedBy: 'SEX',
                trellisOptions: ['M', 'F']
            }
        ]);

    });

    describe('WHEN the plots only trellised on series', () => {
        let expectedPlots: List<IPlot>;

        beforeEach(() => {
            expectedPlots = fromJS([
                {
                    plotType: null,
                    data: null,
                    series: [{
                        category: TrellisCategory.NON_MANDATORY_SERIES,
                        trellisedBy: 'SEX',
                        trellisOptions: ['M', 'F']
                    }],
                    trellising: []
                }]);
        });

        it('THEN returns 1 plot with series information', () => {
            const res = EmptyPlotsService.generateEmptyPlots(100, 1, seriesOnlyTrellisingOptions).toJS();
            expect(res).toEqual(expectedPlots.toJS());
        });
    });

    describe('WHEN the pagination limit exceeds possible combinations', () => {
        let expectedPlots: List<IPlot>;

        beforeEach(() => {
            expectedPlots = fromJS([
                {
                    plotType: null,
                    data: null,
                    series: [],
                    trellising: [
                        {
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'PR'
                        },
                        {
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'Placebo'
                        }
                    ]
                },
                {
                    plotType: null,
                    data: null,
                    series: [],
                    trellising: [
                        {
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'PR'
                        },
                        {
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'Drug 1'
                        }
                    ]
                },
                {
                    plotType: null,
                    data: null,
                    series: [],
                    trellising: [
                        {
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'HR'
                        },
                        {
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'Placebo'
                        }
                    ]
                },
                {
                    plotType: null,
                    data: null,
                    series: [],
                    trellising: [
                        {
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'HR'
                        },
                        {
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'Drug 1'
                        }
                    ]
                }
            ]);
        });

        it('THEN returns plots for all possible combinations', () => {
            const res = EmptyPlotsService.generateEmptyPlots(100, 1, trellisingOptions).toJS();
            expect(res).toEqual(expectedPlots.toJS());
        });
    });
    describe('WHEN the pagination limit is less than possible combinations', () => {
        let expectedPlots: List<IPlot>;

        beforeEach(() => {
            expectedPlots = fromJS([
                {
                    plotType: null,
                    data: null,
                    series: [],
                    trellising: [
                        {
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'PR'
                        },
                        {
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'Placebo'
                        }
                    ]
                },
                {
                    plotType: null,
                    data: null,
                    series: [],
                    trellising: [
                        {
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'PR'
                        },
                        {
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'Drug 1'
                        }
                    ]
                }
            ]);
        });

        it('THEN returns sliced subset', () => {
            expect(EmptyPlotsService.generateEmptyPlots(2, 1, trellisingOptions).toJS()).toEqual(expectedPlots.toJS());
        });
    });
    describe('WHEN the pagination limit exceeds possible combinations', () => {
        let expectedPlots: List<IPlot>;

        beforeEach(() => {
            expectedPlots = fromJS([
                {
                    plotType: null,
                    data: null,
                    series: [],
                    trellising: [
                        {
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'HR'
                        },
                        {
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'Placebo'
                        }
                    ]
                },
                {
                    plotType: null,
                    data: null,
                    series: [],
                    trellising: [
                        {
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'HR'
                        },
                        {
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'Drug 1'
                        }
                    ]
                }
            ]);
        });

        it('THEN returns sliced subset ', () => {
            expect(EmptyPlotsService.generateEmptyPlots(2, 3, trellisingOptions).toJS()).toEqual(expectedPlots.toJS());
        });
    });

    describe('AND generating a set of plots with multiple series', () => {
        let trellisingOpts: List<ITrellises>;
        beforeEach(() => {
            trellisingOpts = fromJS([
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
        describe('WHEN the pagination limit exceeds possible combinations', () => {
            let expectedPlots: List<IPlot>;

            beforeEach(() => {
                expectedPlots = fromJS([
                    {
                        plotType: null,
                        data: null,
                        series: [{
                            category: 'NON_MANDATORY_SERIES',
                            trellisedBy: 'ARM',
                            trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
                        }],
                        trellising: [
                            {
                                category: TrellisCategory.MANDATORY_TRELLIS,
                                trellisedBy: YAxisParameters.MEASUREMENT,
                                trellisOption: 'PR'
                            }
                        ]
                    },
                    {
                        plotType: null,
                        data: null,
                        series: [{
                            category: 'NON_MANDATORY_SERIES',
                            trellisedBy: 'ARM',
                            trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
                        }],
                        trellising: [
                            {
                                category: TrellisCategory.MANDATORY_TRELLIS,
                                trellisedBy: YAxisParameters.MEASUREMENT,
                                trellisOption: 'HR'
                            }
                        ]
                    }
                ]);
            });

            it('THEN returns plots for all possible combinations', () => {
                const res = EmptyPlotsService.generateEmptyPlots(100, 1, trellisingOpts).toJS();
                expect(res).toEqual(expectedPlots.toJS());
            });
        });

        describe('WHEN the pagination limit is less than possible combinations', () => {
            let expectedPlots: List<IPlot>;

            beforeEach(() => {
                expectedPlots = fromJS([
                    {
                        plotType: null,
                        data: null,
                        series: [{
                            category: 'NON_MANDATORY_SERIES',
                            trellisedBy: 'ARM',
                            trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
                        }],
                        trellising: [
                            {
                                category: TrellisCategory.MANDATORY_TRELLIS,
                                trellisedBy: YAxisParameters.MEASUREMENT,
                                trellisOption: 'PR'
                            }
                        ]
                    }
                ]);
            });

            it('THEN returns sliced subset', () => {
                expect(EmptyPlotsService.generateEmptyPlots(1, 1, trellisingOpts).toJS()).toEqual(expectedPlots.toJS());
            });
        });
    });
});
