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

import {SimpleLinePlotService} from './SimpleLinePlotService';
import {TabId} from '../../../../store';

describe('GIVEN SimpleLinePlotService class', () => {
    describe('WHEN plot with zones', () => {
        it('THEN data is handled correctly when axis is continuous', () => {
            const rawData = [{
                'seriesBy': 'subject1',
                'series': [{
                    'x': {
                        'start': 89,
                        'end': 89,
                        'empty': false
                    },
                    'y': 18,
                    'name': 'Stable Disease',
                    'color': '#808080'
                }, {
                    'x': {
                        'start': 182,
                        'end': 182,
                        'empty': false
                    },
                    'y': 31,
                    'name': 'Progressive Disease',
                    'color': '#FF0000'
                }]
            }, {
                'seriesBy': 'subject2',
                'series': [{
                    'x': {
                        'start': -16,
                        'end': -16,
                        'empty': false
                    },
                    'y': 0,
                    'name': 'Stable Disease',
                    'color': '#808080'
                }, {
                    'x': {
                        'start': 82,
                        'end': 82,
                        'empty': false
                    },
                    'y': -48,
                    'name': 'Partial Response',
                    'color': '#0000FF'
                }]
            }];
            const service = new SimpleLinePlotService();
            const result = service.splitServerDataToZones(rawData, null);
            expect(result[0].name).toEqual('subject1');
            expect(result[0].series).toEqual([
                {x: 89, y: 18, category: 89, color: '#808080', name: 'Stable Disease'},
                {x: 182, y: 31, category: 182, color: '#FF0000', name: 'Progressive Disease'}
            ]);
            expect(result[0].zones).toEqual([
                {value: 182, color: '#808080'},
                {value: 183, color: '#FF0000'},
            ]);

            expect(result[1].name).toEqual('subject2');
            expect(result[1].series).toEqual([
                {x: -16, y: 0, category: -16, color: '#808080', name: 'Stable Disease'},
                {x: 82, y: -48, category: 82, color: '#0000FF', name: 'Partial Response'}
            ]);
            expect(result[1].zones).toEqual([
                {value: 82, color: '#808080'},
                {value: 83, color: '#0000FF'},
            ]);
        });
        it('THEN data is handled correctly when axis is categorical', () => {
            const rawData = [{
                'seriesBy': 'subject1',
                'series': [{
                    'x': 'Baseline',
                    'y': 0,
                    'name': 'Stable Disease',
                    'color': '#808080'
                }, {
                    'x': 'Week 6',
                    'y': -36,
                    'name': 'Partial Response',
                    'color': '#0000FF'
                }, {
                    'x': 'Week 12',
                    'y': -45,
                    'name': 'Progressive Disease',
                    'color': '#FF0000'
                }, {
                    'x': 'Week 48',
                    'y': -91,
                    'name': 'Partial Response',
                    'color': '#0000FF'
                }]
            }, {
                'seriesBy': 'subject2',
                'series': [{
                    'x': 'Baseline',
                    'y': 0,
                    'name': 'Stable Disease',
                    'color': '#808080'
                }, {
                    'x': 'Week 12',
                    'y': -17,
                    'name': 'Stable Disease',
                    'color': '#808080'
                }]
            }];
            const service = new SimpleLinePlotService();
            const result = service.splitServerDataToZones(rawData, ['Baseline', 'Week 6', 'Week 12', 'Week 48']);
            expect(result[0].name).toEqual('subject1');
            expect(result[0].series).toEqual([
                {x: 0, y: 0, category: 'Baseline', color: '#808080', name: 'Stable Disease'},
                {x: 1, y: -36, category: 'Week 6', color: '#0000FF', name: 'Partial Response'},
                {x: 2, y: -45, category: 'Week 12', color: '#FF0000', name: 'Progressive Disease'},
                {x: 3, y: -91, category: 'Week 48', color: '#0000FF', name: 'Partial Response'}
            ]);
            expect(result[0].zones).toEqual([
                {value: 1, color: '#808080'},
                {value: 2, color: '#0000FF'},
                {value: 3, color: '#FF0000'},
                {value: 4, color: '#0000FF'}
            ]);

            expect(result[1].name).toEqual('subject2');
            expect(result[1].series).toEqual([
                {x: 0, y: 0, category: 'Baseline', color: '#808080', name: 'Stable Disease'},
                {x: 2, y: -17, category: 'Week 12', color: '#808080', name: 'Stable Disease'}
            ]);
            expect(result[1].zones).toEqual([
                {value: 3, color: '#808080'},
            ]);
        });
    });
    describe('WHEN getStringCategories is called', () => {
        describe('AND plot is categorical', () => {
            it('THEN categories are calculated correctly', () => {
                const rawData = [{
                    'seriesBy': 'subject1',
                    'series': [{
                        'x': 'Baseline',
                        'y': 0,
                        'name': 'Stable Disease',
                        'color': '#808080'
                    }, {
                        'x': 'Week 6',
                        'y': -36,
                        'name': 'Partial Response',
                        'color': '#0000FF'
                    }, {
                        'x': 'Week 12',
                        'y': -45,
                        'name': 'Progressive Disease',
                        'color': '#FF0000'
                    }, {
                        'x': 'Week 48',
                        'y': -91,
                        'name': 'Partial Response',
                        'color': '#0000FF'
                    }]
                }, {
                    'seriesBy': 'subject2',
                    'series': [{
                        'x': 'Baseline',
                        'y': 0,
                        'name': 'Stable Disease',
                        'color': '#808080'
                    }, {
                        'x': 'Week 36',
                        'y': -17,
                        'name': 'Stable Disease',
                        'color': '#808080'
                    }]
                }];
                const result = SimpleLinePlotService.getStringCategories(rawData, true);
                expect(result).toEqual(['Baseline', 'Week 6', 'Week 12', 'Week 36', 'Week 48']);
            });
        });
    });
    describe('WHEN getShiftedYAxis is called', () => {
        describe('AND plot can have logarithmic scale', () => {
            it('correct zooming for y axis should be applied', () => {
                const zoomY = <any>{zoomMin: 1, zoomMax: 10, absMin: 1, absMax: 10};
                const shiftedResult = SimpleLinePlotService.getShiftedYAxis(zoomY, true, TabId.ANALYTE_CONCENTRATION, 'label');
                expect(shiftedResult).toEqual({min: 0.91, max: 10.09});

                const smallMinZoomY = <any>{zoomMin: 0.01, zoomMax: 10, absMin: 0.01, absMax: 10};
                const halfShiftedResult = SimpleLinePlotService.getShiftedYAxis(smallMinZoomY, true,
                    TabId.ANALYTE_CONCENTRATION, 'label fraction');
                expect(halfShiftedResult).toEqual({min: 0.01, max: 10.0999});
            });
        });
        describe('AND plot can not have logarithmic scale', () => {
            it('correct zooming for y axis should be applied', () => {
                const zoomY = <any>{zoomMin: -1, zoomMax: 10, absMin: 1, absMax: 10};
                const shiftedResult = SimpleLinePlotService.getShiftedYAxis(zoomY, false, TabId.TL_DIAMETERS_PLOT, 'label');
                expect(shiftedResult).toEqual({min: -1.11, max: 10.11});
            });
        });
        describe('AND tabId is CtDna', () => {
            describe('AND y axis is not fractional', () => {
                it('correct zooming for y axis should be applied', () => {
                    const zoomY = <any>{zoomMin: 0, zoomMax: 10, absMin: 0, absMax: 10};
                    const shiftedResult = SimpleLinePlotService.getShiftedYAxis(zoomY, false,
                        TabId.CTDNA_PLOT, 'some label');
                    expect(shiftedResult).toEqual({min: 0, max: 10.1});
                });
            });
            describe('AND y axis is not fractional', () => {
                it('correct zooming for y axis should be applied', () => {
                    const zoomY = <any>{zoomMin: 0, zoomMax: 10, absMin: 0, absMax: 10};
                    const shiftedResult = SimpleLinePlotService.getShiftedYAxis(zoomY, false, TabId.CTDNA_PLOT,
                        'some label (fractional)');
                    expect(shiftedResult).toEqual({min: 0, max: 10.1});

                    const zoomYBorder = <any>{zoomMin: 0, zoomMax: 0, absMin: 0, absMax: 10};
                    const shiftedBorderResult = SimpleLinePlotService.getShiftedYAxis(zoomYBorder, false, TabId.CTDNA_PLOT,
                        'some label (fraction)');
                    expect(shiftedBorderResult).toEqual({min: 0, max: 0.05});
                });
            });
        });
    });
});
