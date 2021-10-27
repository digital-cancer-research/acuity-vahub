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

import {XAxisRangeService} from './XAxisRangeService';
import {TestBed, inject} from '@angular/core/testing';
import {IPlot, PlotType, TabId, TrellisDesign} from '../../../index';
import {fromJS, List} from 'immutable';
import {YAxisParameters} from '../../../store';

describe('GIVEN XAxisRangeService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({providers: [XAxisRangeService]});
    });

    describe('WHEN one plot data with one box plot returns', () => {
        let plots: List<IPlot>;
        beforeEach(() => {
            plots = fromJS([{
                plotType: PlotType.BOXPLOT,
                trellising: [
                    {
                        category: 'MANDATORY_TRELLIS',
                        trellisedBy: YAxisParameters.MEASUREMENT,
                        trellisOption: 'SODIUM-HYPO (MEQ/L)'
                    },
                    {
                        category: 'NON_MANDATORY_TRELLIS',
                        trellisedBy: 'ARM',
                        trellisOption: 'Placebo'
                    }
                ],
                series: [],
                data: [{
                    'x': '55.0',
                    'median': 6.3,
                    'upperQuartile': 6.45,
                    'lowerQuartile': 6.15,
                    'upperWhisker': 6.6,
                    'lowerWhisker': 6.0,
                    'subjectCount': 1,
                    'outliers': []
                }]
            }]);
        });
        it('THEN will set x range to be +-1', inject([XAxisRangeService], (service) => {
            expect(service.getRange(0, 0, 1, 1, plots, 'LAB_BOXPLOT')).toEqual({min: 54.0, max: 56.0});
        }));
    });

    describe('WHEN empty data comes back for one of the plots', () => {
        let plots: List<IPlot>;
        beforeEach(() => {
            plots = fromJS([
                {
                    plotType: PlotType.BOXPLOT,
                    trellising: [
                        {
                            category: 'MANDATORY_TRELLIS',
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'SODIUM-HYPO (MEQ/L)'
                        },
                        {
                            category: 'NON_MANDATORY_TRELLIS',
                            trellisedBy: 'ARM',
                            trellisOption: 'Placebo'
                        }
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
                            x: '11',
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
                            x: '15',
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
                        }
                    ]
                },
                {
                    plotType: PlotType.BOXPLOT,
                    trellising: [
                        {
                            category: 'MANDATORY_TRELLIS',
                            trellisedBy: YAxisParameters.MEASUREMENT,
                            trellisOption: 'SODIUM-HYPO (MEQ/L)'
                        },
                        {
                            category: 'NON_MANDATORY_TRELLIS',
                            trellisedBy: 'ARM',
                            trellisOption: 'SuperDex 10 mg'
                        }
                    ],
                    series: [],
                    data: List()
                }
            ]);
        });
        it('THEN the range is set to be the range from the non-empty plot', inject([XAxisRangeService], (service) => {
            expect(service.getRange(0, 0, 2, 4, plots, TabId.LAB_BOXPLOT, TrellisDesign.CONTINUOUS_OVER_TIME)).toEqual({min: 9, max: 15});
        }));
    });
});
