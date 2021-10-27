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
    TabId,
    PlotType,
    IPlot,
    PlotRecord,
    TrellisDesign, ZoomRanges
} from '../ITrellising';
import {fromJS, List} from 'immutable';
import {BoxPlotUtilsService} from './BoxPlotUtilsService';

describe('GIVEN a BoxPlotUtilsService class', () => {
    let utils: BoxPlotUtilsService;
    beforeEach(() => {
        utils = new BoxPlotUtilsService();
        TestBed.configureTestingModule({
            providers: [{provide: BoxPlotUtilsService, useValue: utils}],
            declarations: []
        });
    });

    beforeEach(inject([BoxPlotUtilsService], (_boxPlotUtilsService) => {
        utils = _boxPlotUtilsService;
    }));

    describe('WHEN zoom ranges are calculated', () => {
        describe('AND axis is CONTINUOUS_OVER_TIME', () => {
            describe('AND data is not present', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([new PlotRecord(), new PlotRecord()]);
                });

                it('THEN returns the ranges of the plots as zeros', () => {
                    const expectedZoom = <ZoomRanges>{
                        x: {min: 0, max: 0}, y: {min: 0, max: 100}
                    };
                    expect(utils.calculateZoomRanges(plots, TrellisDesign.CONTINUOUS_OVER_TIME, TabId.AES_COUNTS_BARCHART)).toEqual(expectedZoom);
                });
            });

            describe('AND data is present', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([
                        {
                            plotType: PlotType.BOXPLOT,
                            trellisedBy: null,
                            data: [
                                {
                                    x: '2.0',
                                    xrank: 2,
                                    median: null,
                                    upperQuartile: null,
                                    lowerQuartile: null,
                                    upperWhisker: null,
                                    lowerWhisker: null,
                                    outliers: []
                                },
                                {
                                    x: '9995.0',
                                    xrank: 9995,
                                    median: 31.5,
                                    upperQuartile: 155.25,
                                    lowerQuartile: 11.0,
                                    upperWhisker: 155.25,
                                    lowerWhisker: -37.0,
                                    outliers: [{
                                        'x': 9995.0,
                                        'outlierValue': 513.0,
                                        'subjectId': 'DummyData-2684463227'
                                    }]
                                }
                            ]
                        },
                        {
                            plotType: PlotType.BOXPLOT,
                            trellisedBy: null,
                            data: [
                                {
                                    x: '9996.0',
                                    xrank: 9996,
                                    median: 15.0,
                                    upperQuartile: 22.0,
                                    lowerQuartile: 3.0,
                                    upperWhisker: 38.0,
                                    lowerWhisker: 3.0,
                                    outliers: [{
                                        'x': 9996.0,
                                        'outlierValue': 65.0,
                                        'subjectId': 'DummyData-2684463227'
                                    }]
                                },
                                {
                                    x: '9998.0',
                                    xrank: 9998,
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

                it('THEN returns the ranges of the plots', () => {
                    const expectedZoom = <ZoomRanges>{
                        x: {min: 2.0, max: 9998.0},
                        y: {min: 0, max: 100.0}
                    };
                    expect(utils.calculateZoomRanges(plots, TrellisDesign.CONTINUOUS_OVER_TIME, TabId.LAB_BOXPLOT)).toEqual(expectedZoom);
                });
            });

            describe('AND boxplot has one data point', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([
                        {
                            plotType: PlotType.BOXPLOT,
                            trellisedBy: null,
                            data: [
                                {
                                    x: '9995.0',
                                    xrank: 9995,
                                    median: 31.5,
                                    upperQuartile: 155.25,
                                    lowerQuartile: 11.0,
                                    upperWhisker: 155.25,
                                    lowerWhisker: -37.0,
                                    outliers: [{
                                        'x': 9995.0,
                                        'outlierValue': 513.0,
                                        'subjectId': 'DummyData-2684463227'
                                    }]
                                }
                            ]
                        }
                    ]);
                });

                it('THEN returns the zoom range in x', () => {
                    const expectedZoom = <ZoomRanges>{
                        x: {max: 10994.5, min: 8995.5},
                        y: {min: 0, max: 100}
                    };
                    expect(utils.calculateZoomRanges(plots, TrellisDesign.CONTINUOUS_OVER_TIME, TabId.LAB_BOXPLOT)).toEqual(expectedZoom);
                });
            });
        });

        describe('AND axis is CATEGORICAL_COUNTS_AND_PERCENTAGES', () => {
            describe('AND data is not present', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([new PlotRecord(), new PlotRecord()]);
                });

                it('THEN returns the ranges of the plots as zeros', () => {
                    const expectedZoom = <ZoomRanges>{
                        x: {min: 0, max: 0}, y: {min: 0, max: 0}
                    };
                    expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.AES_COUNTS_BARCHART))
                        .toEqual(expectedZoom);
                });
            });

            describe('AND data is present', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([
                        {
                            plotType: PlotType.BOXPLOT,
                            trellisedBy: null,
                            data: [
                                {
                                    x: 'category1',
                                    xrank: 1,
                                    median: null,
                                    upperQuartile: null,
                                    lowerQuartile: null,
                                    upperWhisker: null,
                                    lowerWhisker: null,
                                    outliers: []
                                },
                                {
                                    x: 'category2',
                                    xrank: 2,
                                    median: 31.5,
                                    upperQuartile: 155.25,
                                    lowerQuartile: 11.0,
                                    upperWhisker: 155.25,
                                    lowerWhisker: -37.0,
                                    outliers: [{
                                        'x': 'category1',
                                        'outlierValue': 513.0,
                                        'subjectId': 'DummyData-2684463227'
                                    }]
                                }
                            ]
                        },
                        {
                            plotType: PlotType.BOXPLOT,
                            trellisedBy: null,
                            data: [
                                {
                                    x: 'category1',
                                    xrank: 1,
                                    median: 15.0,
                                    upperQuartile: 22.0,
                                    lowerQuartile: 3.0,
                                    upperWhisker: 38.0,
                                    lowerWhisker: 3.0,
                                    outliers: [{
                                        x: 'category1',
                                        outlierValue: 65.0,
                                        subjectId: 'DummyData-2684463227'
                                    }]
                                },
                                {
                                    x: 'category2',
                                    xrank: 2,
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

                it('THEN returns the ranges of the plots', () => {
                    const expectedZoom = <ZoomRanges>{
                        x: {min: 0, max: 1},
                        y: {min: 0, max: 0}
                    };
                    expect(utils.calculateZoomRanges(plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.LAB_BOXPLOT))
                        .toEqual(expectedZoom);
                });
            });

            describe('AND boxplot has one data point', () => {
                let plots: List<IPlot>;
                beforeEach(() => {
                    plots = fromJS([
                        {
                            plotType: PlotType.BOXPLOT,
                            trellisedBy: null,
                            data: [
                                {
                                    x: '1',
                                    xrank: 1,
                                    median: 31.5,
                                    upperQuartile: 155.25,
                                    lowerQuartile: 11.0,
                                    upperWhisker: 155.25,
                                    lowerWhisker: -37.0,
                                    outliers: [{
                                        x: '1',
                                        outlierValue: 513.0,
                                        subjectId: 'DummyData-2684463227'
                                    }]
                                }
                            ]
                        }
                    ]);
                });

                it('THEN returns the zoom range in x', () => {
                    const expectedZoom = <ZoomRanges>{
                        x: {max: 1.1, min: 0.9},
                        y: {min: 0, max: 100}
                    };
                    expect(utils.calculateZoomRanges(plots, TrellisDesign.CONTINUOUS_OVER_TIME, TabId.LAB_BOXPLOT)).toEqual(expectedZoom);
                });
            });
        });
    });
});
