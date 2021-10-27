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

import {YAxisRangeService} from './YAxisRangeService';
import {TestBed, inject} from '@angular/core/testing';
import {IPlot, TabId, TrellisCategory, PlotType, TrellisRecord} from '../../../index';
import {fromJS, List} from 'immutable';
import {YAxisParameters} from '../../../store';
import RangeChartSeries = Request.RangeChartSeries;
import OutputRangeChartEntry = Request.OutputRangeChartEntry;

describe('GIVEN YAxisRangeService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({providers: [YAxisRangeService]});
    });
    describe('WHEN rangeplots', () => {
        describe('WHEN no other common measurement in visible plots', () => {
            let plots: List<IPlot>;
            beforeEach(() => {
                plots = fromJS([
                    {
                        plotType: PlotType.RANGEPLOT,
                        trellising: [new TrellisRecord({
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'A'
                        })],
                        data: <RangeChartSeries<any, any>[]>[{
                            name: 'Placebo', data: <OutputRangeChartEntry[]>[
                                {
                                    x: '2.0',
                                    min: -100.0,
                                    max: 110.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                },
                                {
                                    x: '9995.0',
                                    min: -100.0,
                                    max: 200.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                }
                            ]
                        }]
                    },
                    {
                        plotType: PlotType.RANGEPLOT,
                        trellising: [new TrellisRecord({
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'B'
                        })],
                        data: <RangeChartSeries<any, any>[]>[{
                            name: 'Placebo', data: [
                                {
                                    x: '2.0',
                                    min: -50.0,
                                    max: 110.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                },
                                {
                                    x: '9995.0',
                                    min: -25.0,
                                    max: 200.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                }
                            ]
                        }]
                    }]);
            });
            it('THEN returns the range for just that plot', inject([YAxisRangeService], (service) => {
                expect(service.getRange(0, 0, 2, 4, plots)).toEqual({min: -115.0, max: 215.0});
                expect(service.getRange(0, 1, 2, 4, plots)).toEqual({min: -62.5, max: 212.5});
            }));
        });

        describe('WHEN no other common measurement in visible plots and min 0.0', () => {
            let plots: List<IPlot>;
            beforeEach(() => {
                plots = fromJS([
                    {
                        plotType: PlotType.RANGEPLOT,
                        trellising: [new TrellisRecord({
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'A'
                        })],
                        data: <RangeChartSeries<any, any>[]>[{
                            name: 'Placebo', data: <OutputRangeChartEntry[]>[
                                {
                                    x: '2.0',
                                    min: 0.0,
                                    max: 110.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                },
                                {
                                    x: '9995.0',
                                    min: null,
                                    max: null,
                                    y: 200.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                }
                            ]
                        }]
                    },
                    {
                        plotType: PlotType.RANGEPLOT,
                        trellising: [new TrellisRecord({
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'B'
                        })],
                        data: <RangeChartSeries<any, any>[]>[{
                            name: 'Placebo', data: [
                                {
                                    x: '2.0',
                                    min: -50.0,
                                    max: 110.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                },
                                {
                                    x: '9995.0',
                                    min: -25.0,
                                    max: 200.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                },
                                {
                                    x: '9995.2',
                                    min: null,
                                    max: null,
                                    y: null,
                                    stdErr: 25.0,
                                    dataPoints: 0
                                }
                            ]
                        }]
                    }]);
            });
            it('THEN returns the range for just that plot', inject([YAxisRangeService], (service) => {
                expect(service.getRange(0, 0, 2, 4, plots)).toEqual({min: -10.0, max: 210.0});
                expect(service.getRange(0, 1, 2, 4, plots)).toEqual({min: -62.5, max: 212.5});
            }));
        });

        describe('WHEN no other common measurement in visible plots and nulls', () => {
            let plots: List<IPlot>;
            beforeEach(() => {
                plots = fromJS([
                    {
                        plotType: PlotType.RANGEPLOT,
                        trellising: [new TrellisRecord({
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'A'
                        })],
                        data: <RangeChartSeries<any, any>[]>[{
                            name: 'Placebo', data: <OutputRangeChartEntry[]>[
                                {
                                    x: '2.0',
                                    min: -100.0,
                                    max: 110.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                },
                                {
                                    x: '9995.0',
                                    min: null,
                                    max: null,
                                    y: 200.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                }
                            ]
                        }]
                    },
                    {
                        plotType: PlotType.RANGEPLOT,
                        trellising: [new TrellisRecord({
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'B'
                        })],
                        data: <RangeChartSeries<any, any>[]>[{
                            name: 'Placebo', data: [
                                {
                                    x: '2.0',
                                    min: -50.0,
                                    max: 110.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                },
                                {
                                    x: '9995.0',
                                    min: -25.0,
                                    max: 200.0,
                                    y: 50.0,
                                    stdErr: 25.0,
                                    dataPoints: 10
                                },
                                {
                                    x: '9995.2',
                                    min: null,
                                    max: null,
                                    y: null,
                                    stdErr: 25.0,
                                    dataPoints: 0
                                }
                            ]
                        }]
                    }]);
            });
            it('THEN returns the range for just that plot', inject([YAxisRangeService], (service) => {
                expect(service.getRange(0, 0, 2, 4, plots)).toEqual({min: -115.0, max: 215.0});
                expect(service.getRange(0, 1, 2, 4, plots)).toEqual({min: -62.5, max: 212.5});
            }));
        });

        describe('WHEN boxplots', () => {
            describe('WHEN no other common measurement in visible plots', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([
                        {
                            plotType: PlotType.BOXPLOT,
                            trellising: [new TrellisRecord({
                                category: TrellisCategory.MANDATORY_TRELLIS,
                                trellisedy: YAxisParameters.MEASUREMENT,
                                trellisOption: 'A'
                            })],
                            data: [
                                {
                                    x: '2.0',
                                    median: null,
                                    upperQuartile: null,
                                    lowerQuartile: null,
                                    upperWhisker: null,
                                    lowerWhisker: null,
                                    outliers: []
                                },
                                {
                                    x: '9995.0',
                                    median: 31.5,
                                    upperQuartile: 155.25,
                                    lowerQuartile: 11.0,
                                    upperWhisker: 155.25,
                                    lowerWhisker: -37.0,
                                    outliers: [{'x': 9995.0, 'outlierValue': 513.0, 'subjectId': 'DummyData-2684463227'}]
                                }
                            ]
                        },
                        {
                            plotType: PlotType.BOXPLOT,
                            trellising: [new TrellisRecord({
                                category: TrellisCategory.MANDATORY_TRELLIS,
                                trellisedy: YAxisParameters.MEASUREMENT,
                                trellisOption: 'B'
                            })],
                            data: [
                                {
                                    x: '9996.0',
                                    median: 15.0,
                                    upperQuartile: 22.0,
                                    lowerQuartile: 3.0,
                                    upperWhisker: 38.0,
                                    lowerWhisker: 3.0,
                                    outliers: [{'x': 9996.0, 'outlierValue': 65.0, 'subjectId': 'DummyData-2684463227'}]
                                },
                                {
                                    x: '9998.0',
                                    median: 0.5,
                                    upperQuartile: 17.5,
                                    lowerQuartile: -14.5,
                                    upperWhisker: 37.0,
                                    lowerWhisker: -28.0,
                                    outliers: []
                                }
                            ]
                        }
                    ]);
                });
                it('THEN returns the range for just that plot +-5%', inject([YAxisRangeService], (service) => {
                    expect(service.getRange(0, 0, 2, 4, plots)).toEqual({min: -64.5, max: 540.5});
                    expect(service.getRange(0, 1, 2, 4, plots)).toEqual({min: -31.25, max: 68.1});
                }));
            });

            describe('WHEN one other common measurement in visible plots', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([
                        {
                            plotType: PlotType.BOXPLOT,
                            trellising: [new TrellisRecord({
                                category: TrellisCategory.MANDATORY_TRELLIS,
                                trellisedy: YAxisParameters.MEASUREMENT,
                                trellisOption: 'A'
                            })],
                            data: [
                                {
                                    x: '2.0',
                                    median: null,
                                    upperQuartile: null,
                                    lowerQuartile: null,
                                    upperWhisker: null,
                                    lowerWhisker: null,
                                    outliers: []
                                },
                                {
                                    x: '9995.0',
                                    median: 31.5,
                                    upperQuartile: 155.25,
                                    lowerQuartile: 11.0,
                                    upperWhisker: 155.25,
                                    lowerWhisker: -37.0,
                                    outliers: [{'x': 9995.0, 'outlierValue': 513.0, 'subjectId': 'DummyData-2684463227'}]
                                }
                            ]
                        },
                        {
                            plotType: PlotType.BOXPLOT,
                            trellising: [new TrellisRecord({
                                category: TrellisCategory.MANDATORY_TRELLIS,
                                trellisedy: YAxisParameters.MEASUREMENT,
                                trellisOption: 'A'
                            })],
                            data: [
                                {
                                    x: '9996.0',
                                    median: 15.0,
                                    upperQuartile: 22.0,
                                    lowerQuartile: 3.0,
                                    upperWhisker: 38.0,
                                    lowerWhisker: 3.0,
                                    outliers: [{'x': 9996.0, 'outlierValue': 65.0, 'subjectId': 'DummyData-2684463227'}]
                                },
                                {
                                    x: '9998.0',
                                    median: 0.5,
                                    upperQuartile: 17.5,
                                    lowerQuartile: -14.5,
                                    upperWhisker: 37.0,
                                    lowerWhisker: -28.0,
                                    outliers: []
                                }
                            ]
                        }
                    ]);
                });
                it('THEN returns the range for just that plot', inject([YAxisRangeService], (service) => {
                    expect(service.getRange(0, 0, 2, 4, plots)).toEqual({min: -64.5, max: 540.5});
                    expect(service.getRange(0, 1, 2, 4, plots)).toEqual({min: -64.5, max: 540.5});
                }));
            });
        });
    });

    describe('WHEN data with 0 comes back for plot', () => {
        let plots: List<IPlot>;
        beforeEach(() => {
            plots = fromJS([
                {
                    plotType: PlotType.BOXPLOT,
                    trellising: [
                        new TrellisRecord({
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'LVEF'
                        })
                    ],
                    series: [],
                    data: [
                        {
                            x: 1,
                            median: 0,
                            upperQuartile: 0,
                            lowerQuartile: 0,
                            upperWhisker: null,
                            lowerWhisker: null,
                            subjectCount: 1,
                            outliers: [
                                {
                                    x: 1,
                                    outlierValue: 0,
                                    subjectId: 'DummyData-8891699353'
                                }
                            ]
                        },
                        {
                            x: 11,
                            median: 1,
                            upperQuartile: 1,
                            lowerQuartile: 1,
                            upperWhisker: null,
                            lowerWhisker: null,
                            subjectCount: 1,
                            outliers: [
                                {
                                    x: 11,
                                    outlierValue: 1,
                                    subjectId: 'DummyData-8891699353'
                                }
                            ]
                        }, {
                            x: 99,
                            median: 2,
                            upperQuartile: 2,
                            lowerQuartile: 2,
                            upperWhisker: null,
                            lowerWhisker: null,
                            subjectCount: 1,
                            outliers: [
                                {
                                    x: 99,
                                    outlierValue: 2,
                                    subjectId: 'DummyData-8891699353'
                                }
                            ]
                        }
                    ]
                }
            ]);
        });
        it('THEN the range is set must be from zero', inject([YAxisRangeService], (service) => {
            expect(service.getRange(0, 0, 2, 4, plots, TabId.CARDIAC_BOXPLOT)).toEqual({min: 0, max: 2});
        }));
    });

    describe('WHEN empty data comes back for one of the plots', () => {
        let plots: List<IPlot>;
        beforeEach(() => {
            plots = fromJS([
                {
                    plotType: PlotType.BOXPLOT,
                    trellising: [
                        new TrellisRecord({
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'SODIUM-HYPO (MEQ/L)'
                        }),
                        new TrellisRecord({
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'Placebo'
                        })
                    ],
                    series: [],
                    data: [
                        {
                            x: '9',
                            median: 124,
                            upperQuartile: 124,
                            lowerQuartile: 124,
                            upperWhisker: null,
                            lowerWhisker: null,
                            subjectCount: 1,
                            outliers: [
                                {
                                    x: 9,
                                    outlierValue: 124,
                                    subjectId: 'DummyData-8891699353'
                                }
                            ]
                        },
                        {
                            x: '10',
                            median: 124,
                            upperQuartile: 124,
                            lowerQuartile: 124,
                            upperWhisker: null,
                            lowerWhisker: null,
                            subjectCount: 1,
                            outliers: [
                                {
                                    x: 9,
                                    outlierValue: 127,
                                    subjectId: 'DummyData-8891699353'
                                }
                            ]
                        }
                    ]
                },
                {
                    plotType: PlotType.BOXPLOT,
                    trellising: [
                        new TrellisRecord({
                            category: TrellisCategory.MANDATORY_TRELLIS,
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'SODIUM-HYPO (MEQ/L)'
                        }),
                        new TrellisRecord({
                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                            trellisedBy: 'ARM',
                            trellisOption: 'SuperDex 10 mg'
                        })
                    ],
                    series: [],
                    data: List()
                }
            ]);
        });
        it('THEN the range is set to be the range from the non-empty plot', inject([YAxisRangeService], (service) => {
            expect(service.getRange(0, 0, 2, 4, plots, TabId.LAB_BOXPLOT)).toEqual({min: 123.85, max: 127.15});
        }));
    });
});
