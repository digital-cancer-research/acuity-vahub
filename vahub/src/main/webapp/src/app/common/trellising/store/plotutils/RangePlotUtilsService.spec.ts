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

import {TestBed, inject} from '@angular/core/testing';
import {
    ITrellises,
    TabId,
    PlotType,
    IPlot,
    ILegend,
    LegendSymbol,
    TrellisDesign, YAxisParameters
} from '../ITrellising';
import {fromJS, List} from 'immutable';
import {RangePlotUtilsService} from './RangePlotUtilsService';
import RangeChartSeries = Request.RangeChartSeries;

describe('GIVEN a RangePlotUtilsService class', () => {
    let utils: RangePlotUtilsService;
    beforeEach(() => {
        utils = new RangePlotUtilsService();
        TestBed.configureTestingModule({
            providers: [{provide: RangePlotUtilsService, useValue: utils}],
            declarations: []
        });
    });

    beforeEach(inject([RangePlotUtilsService], (_rangePlotUtilsService) => {
        utils = _rangePlotUtilsService;
    }));

    describe('WHEN extractLegend is called', () => {
        describe('AND multiple range plots with 1 series are present', () => {
            let plots: List<IPlot>;
            let trellises: List<ITrellises>;
            beforeEach(() => {
                plots = fromJS([{
                    plotType: PlotType.RANGEPLOT,
                    trellising: null,
                    series: null,
                    data: <RangeChartSeries<any, any>[]>[{
                        data: [
                            {
                                dataPoints: 10,
                                max: 5,
                                y: 3,
                                min: 2,
                                stdDev: 2,
                                stdErr: 2,
                                x: '1',
                            }
                        ],
                        name: 'Placebo',
                        // nonSeriesTrellis: [],
                        // trellisedBy: []
                    }]
                }]);
            });
            describe('AND no trellising is present', () => {
                it('THEN legend data is returned', () => {
                    const legends = utils.extractLegend(plots, TabId.LAB_LINEPLOT, fromJS([]));
                    expect(legends).toEqual([<ILegend>{
                        title: 'All',
                        entries: [
                            {
                                label: 'All',
                                symbol: LegendSymbol.CIRCLE,
                                color: '#00AAFF'
                            }
                        ]
                    }]);
                });
            });
            describe('AND trellising is present', () => {
                trellises = fromJS([{
                    'trellisedBy': YAxisParameters.MEASUREMENT,
                    'category': 'MANDATORY_TRELLIS',
                    'trellisOptions': ['ALBUMIN (g/dL)']
                }, {
                    'trellisedBy': 'ARM',
                    'category': 'NON_MANDATORY_SERIES',
                    'trellisOptions': ['SuperDex 10 mg', 'Placebo', 'SuperDex 20 mg']
                }]);
                plots = fromJS([{
                    'plotType': 'RANGEPLOT',
                    'trellising': [{
                        'trellisedBy': YAxisParameters.MEASUREMENT,
                        'category': 'MANDATORY_TRELLIS',
                        'trellisOption': 'ALBUMIN (g/dL)'
                    }],
                    'data': [{
                        'name': 'Placebo',
                        'trellisedBy': [{
                            'trellisedBy': YAxisParameters.MEASUREMENT,
                            'category': 'MANDATORY_TRELLIS',
                            'trellisOption': 'ALBUMIN (g/dL)'
                        }, {'trellisedBy': 'ARM', 'category': 'NON_MANDATORY_SERIES', 'trellisOption': 'Placebo'}],
                        'data': [{
                            'x': '5',
                            'min': 3.28,
                            'y': 3.41,
                            'max': 3.54,
                            'stdDev': 0.87,
                            'stdErr': 0.13,
                            'dataPoints': 42,
                            'xrank': 5
                        }, {
                            'x': '9',
                            'min': 3.58,
                            'y': 3.69,
                            'max': 3.8,
                            'stdDev': 0.61,
                            'stdErr': 0.11,
                            'dataPoints': 30,
                            'xrank': 9
                        }, {
                            'x': '13',
                            'min': 3.31,
                            'y': 3.53,
                            'max': 3.75,
                            'stdDev': 0.9,
                            'stdErr': 0.22,
                            'dataPoints': 17,
                            'xrank': 13
                        }, {
                            'x': '17',
                            'min': 4,
                            'y': 4,
                            'max': 4,
                            'stdDev': 0,
                            'stdErr': 0,
                            'dataPoints': 1,
                            'xrank': 17
                        }],
                        'nonSeriesTrellis': [{
                            'trellisedBy': YAxisParameters.MEASUREMENT,
                            'category': 'MANDATORY_TRELLIS',
                            'trellisOption': 'ALBUMIN (g/dL)'
                        }]
                    }, {
                        'name': 'SuperDex 10 mg',
                        'trellisedBy': [{
                            'trellisedBy': YAxisParameters.MEASUREMENT,
                            'category': 'MANDATORY_TRELLIS',
                            'trellisOption': 'ALBUMIN (g/dL)'
                        }, {
                            'trellisedBy': 'ARM',
                            'category': 'NON_MANDATORY_SERIES',
                            'trellisOption': 'SuperDex 10 mg'
                        }],
                        'data': [{
                            'x': '5',
                            'min': 3.41,
                            'y': 3.49,
                            'max': 3.57,
                            'stdDev': 0.67,
                            'stdErr': 0.08,
                            'dataPoints': 71,
                            'xrank': 5
                        }, {
                            'x': '9',
                            'min': 3.41,
                            'y': 3.49,
                            'max': 3.57,
                            'stdDev': 0.63,
                            'stdErr': 0.08,
                            'dataPoints': 57,
                            'xrank': 9
                        }, {
                            'x': '13',
                            'min': 3.51,
                            'y': 3.6,
                            'max': 3.69,
                            'stdDev': 0.55,
                            'stdErr': 0.09,
                            'dataPoints': 34,
                            'xrank': 13
                        }, {
                            'x': '17',
                            'min': 3.1,
                            'y': 3.2,
                            'max': 3.3,
                            'stdDev': 0.14,
                            'stdErr': 0.1,
                            'dataPoints': 2,
                            'xrank': 17
                        }],
                        'nonSeriesTrellis': [{
                            'trellisedBy': YAxisParameters.MEASUREMENT,
                            'category': 'MANDATORY_TRELLIS',
                            'trellisOption': 'ALBUMIN (g/dL)'
                        }]
                    }, {
                        'name': 'SuperDex 20 mg',
                        'trellisedBy': [{
                            'trellisedBy': YAxisParameters.MEASUREMENT,
                            'category': 'MANDATORY_TRELLIS',
                            'trellisOption': 'ALBUMIN (g/dL)'
                        }, {
                            'trellisedBy': 'ARM',
                            'category': 'NON_MANDATORY_SERIES',
                            'trellisOption': 'SuperDex 20 mg'
                        }],
                        'data': [{
                            'x': '5',
                            'min': 3.18,
                            'y': 3.3,
                            'max': 3.42,
                            'stdDev': 0.8,
                            'stdErr': 0.12,
                            'dataPoints': 44,
                            'xrank': 5
                        }, {
                            'x': '9',
                            'min': 3.44,
                            'y': 3.56,
                            'max': 3.68,
                            'stdDev': 0.65,
                            'stdErr': 0.12,
                            'dataPoints': 30,
                            'xrank': 9
                        }, {
                            'x': '13',
                            'min': 3.55,
                            'y': 3.69,
                            'max': 3.83,
                            'stdDev': 0.66,
                            'stdErr': 0.14,
                            'dataPoints': 23,
                            'xrank': 13
                        }, {
                            'x': '17',
                            'min': 3.7,
                            'y': 3.7,
                            'max': 3.7,
                            'stdDev': 0,
                            'stdErr': 0,
                            'dataPoints': 3,
                            'xrank': 17
                        }],
                        'nonSeriesTrellis': [{
                            'trellisedBy': YAxisParameters.MEASUREMENT,
                            'category': 'MANDATORY_TRELLIS',
                            'trellisOption': 'ALBUMIN (g/dL)'
                        }]
                    }]
                }]);
                it('THEN legend data is returned', () => {
                    const legends = utils.extractLegend(plots, TabId.LAB_LINEPLOT, trellises);
                    expect(legends).toEqual([<ILegend>{
                        entries: [{label: 'Placebo', color: '#00AAFF', symbol: 'CIRCLE'}],
                        title: 'Actual study arm'
                    }]);
                });
            });
        });
    });

    describe('WHEN the zoom is requested', () => {
        describe('AND axes types is CONTINUOUS_OVER_TIME', () => {
            it('THEN zoom is calculated correctly', () => {
                const plots = fromJS([{
                    'plotType': 'RANGEPLOT',
                    'trellising': null,
                    'series': [],
                    'data': [{
                        'name': 'All',
                        'trellisedBy': null,
                        'data': [{
                            'x': '5',
                            'y': 25.04,
                            'xrank': 5
                        }, {
                            'x': '9',
                            'y': 25.81,
                            'xrank': 9
                        }],
                        'nonSeriesTrellis': null
                    }]
                }, {
                    'plotType': 'RANGEPLOT',
                    'trellising': null,
                    'series': [],
                    'data': [{
                        'name': 'All',
                        'trellisedBy': null,
                        'data': [{
                            'x': '5',
                            'y': 6.04,
                            'xrank': 5
                        }, {
                            'x': '9',
                            'y': 6.07,
                            'xrank': 9
                        }],
                        'nonSeriesTrellis': null
                    }]
                }, {
                    'plotType': 'RANGEPLOT',
                    'trellising': null,
                    'series': [],
                    'data': [{
                        'name': 'All',
                        'trellisedBy': null,
                        'data': [{
                            'x': '5',
                            'y': 137.39,
                            'xrank': 5
                        }, {
                            'x': '9',
                            'y': 137.83,
                            'xrank': 9
                        }],
                        'nonSeriesTrellis': null
                    }]
                }, {
                    'plotType': 'RANGEPLOT',
                    'trellising': null,
                    'series': [],
                    'data': [{
                        'name': 'All',
                        'trellisedBy': null,
                        'data': [{
                            'x': '5',
                            'y': 9.41,
                            'xrank': 5
                        }, {
                            'x': '9',
                            'y': 9.22,
                            'xrank': 9
                        }],
                        'nonSeriesTrellis': null
                    }]
                }]);
                const expectedZoom = {x: {max: 9, min: 5}, y: {min: 0, max: 100}};
                expect(utils.calculateZoomRanges(plots, TrellisDesign.CONTINUOUS_OVER_TIME, TabId.LAB_LINEPLOT)).toEqual(expectedZoom);
            });

            describe('AND data contains values less than 0', () => {
                it('THEN zoom is calculated correctly', () => {
                    const plots = fromJS([{
                        'plotType': 'RANGEPLOT',
                        'trellising': null,
                        'series': [],
                        'data': [{
                            'name': 'All',
                            'trellisedBy': null,
                            'data': [{
                                'x': '-2',
                                'y': 8.22,
                                'xrank': 2
                            }, {
                                'x': '3',
                                'y': 9.1,
                                'xrank': 3
                            }, {
                                'x': '4',
                                'y': 17.56,
                                'xrank': 4
                            }],
                            'nonSeriesTrellis': null
                        }]
                    }, {
                        'plotType': 'RANGEPLOT',
                        'trellising': null,
                        'series': [],
                        'data': [{
                            'name': 'All',
                            'trellisedBy': null,
                            'data': [{
                                'x': '-2',
                                'y': 134.27,
                                'xrank': 2
                            }, {
                                'x': '1',
                                'y': 137.39,
                                'xrank': 3
                            }, {
                                'x': '2',
                                'y': 137.2,
                                'xrank': 4
                            }],
                            'nonSeriesTrellis': null
                        }]
                    }]);

                    const expectedZoom = {x: {max: 4, min: -2}, y: {min: 0, max: 100}};
                    expect(
                        utils.calculateZoomRanges(
                            plots, TrellisDesign.CONTINUOUS_OVER_TIME, TabId.LAB_LINEPLOT
                        )
                    ).toEqual(expectedZoom);
                });
            });
        });
        describe('AND axes types is CATEGORICAL_COUNTS_AND_PERCENTAGES', () => {
            it('THEN zoom is calculated correctly', () => {
                const plots = fromJS([{
                    'plotType': 'RANGEPLOT',
                    'trellising': null,
                    'series': [],
                    'data': [{
                        'name': 'All',
                        'trellisedBy': null,
                        'data': [{
                            'x': '2',
                            'y': 8.22,
                            'xrank': 2
                        }, {
                            'x': '3',
                            'y': 9.1,
                            'xrank': 3
                        }, {
                            'x': '4',
                            'y': 17.56,
                            'xrank': 4
                        }],
                        'nonSeriesTrellis': null
                    }]
                }, {
                    'plotType': 'RANGEPLOT',
                    'trellising': null,
                    'series': [],
                    'data': [{
                        'name': 'All',
                        'trellisedBy': null,
                        'data': [{
                            'x': '2',
                            'y': 134.27,
                            'xrank': 2
                        }, {
                            'x': '3',
                            'y': 137.39,
                            'xrank': 3
                        }, {
                            'x': '4',
                            'y': 137.2,
                            'xrank': 4
                        }],
                        'nonSeriesTrellis': null
                    }]
                }, {
                    'plotType': 'RANGEPLOT',
                    'trellising': null,
                    'series': [],
                    'data': [{
                        'name': 'All',
                        'trellisedBy': null,
                        'data': [{
                            'x': '2',
                            'y': 5.89,
                            'xrank': 2
                        }, {
                            'x': '3',
                            'y': 6.03,
                            'xrank': 3
                        }, {
                            'x': '4',
                            'y': 6.3,
                            'xrank': 4
                        }],
                        'nonSeriesTrellis': null
                    }]
                }, {
                    'plotType': 'RANGEPLOT',
                    'trellising': null,
                    'series': [],
                    'data': [{
                        'name': 'All',
                        'trellisedBy': null,
                        'data': [{
                            'x': '2',
                            'y': 0.12,
                            'xrank': 2
                        }, {
                            'x': '3',
                            'y': 0.09,
                            'xrank': 3
                        }],
                        'nonSeriesTrellis': null
                    }]
                }]);
                const expectedZoom = {x: {min: 0, max: 2}, y: {min: 0, max: 0}};
                expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.LAB_LINEPLOT))
                    .toEqual(expectedZoom);
            });
        });
    });
});
