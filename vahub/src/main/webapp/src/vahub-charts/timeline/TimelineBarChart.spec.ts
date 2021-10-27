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

import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {TimelineBarChart} from './TimelineBarChart';
import {getAllElementsBySelector} from '../utils/test-utils';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');

describe('Timeline bar chart', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = {
            chart: {
                renderTo: renderTo,
                type: 'timeline-barchart',
                height: 400,
                width: 800,
                id: 'test_id',
                disableExport: true,
                events: {
                    selection: select,
                    click: click,
                    removeSelection: () => {
                    },
                }
            },
            title: {
                text: 'test title',
            },
            id: 'test',
        };
        testChart = new TimelineBarChart(testOptions);
    });

    afterEach(function () {
        d3.select(renderTo).selectAll('svg').remove();
    });

    describe('create chart', function () {

        it('should render svg', function () {
            const svg = d3.select(renderTo).select('#svg_test_id');
            expect(svg.attr('height')).toEqual('400');
            expect(svg.attr('width')).toEqual('100%');
        });

        it('should not render title', function () {
            const title = renderTo.getElementsByClassName('chart-title');
            expect(title.length).toBe(0);
        });

        it('should not render export button', function () {
            const button = renderTo.getElementsByClassName('custom-export');
            expect(button.length).toBe(0);
        });
    });
    describe('create axes', function () {
        it('should render yAxis', function () {
            const testData = {
                color: 'red',
                name: 'data1',
                data: [
                    {
                        x: 0,
                        y: 5,
                    }
                ],
                point: {
                    events: {
                        click: () => {
                        }
                    }
                }
            };
            testChart.addSeries(testData);
            testChart.redraw();
            const axis = getAllElementsBySelector('.y-axis', renderTo);
            expect(axis.length).toEqual(1);
        });
    });

    describe('create bars', function () {
        it('should render one bar and one marker', function () {
            const testData = {
                color: 'red',
                name: 'data1',
                data: [
                    {
                        x: 0,
                        high: 0,
                        low: -10,
                    }
                ],
                point: {
                    events: {
                        click: () => {
                        }
                    }
                }
            };
            const testMarkerData = {
                color: 'blue',
                type: 'scatter',
                name: 'data1',
                data: [
                    {
                        x: 0,
                        y: 5,
                        name: 'marker',
                        marker: {
                            fillColor: 'blue',
                            name: 'test',
                            symbol: 'diamond',
                        }
                    }
                ],
                point: {
                    events: {
                        click: () => {
                        }
                    }
                }
            };

            d3.selectAll('svg').remove();
            testChart.addSeries(testData);
            testChart.addSeries(testMarkerData);
            testChart.redraw();
            expect(getAllElementsBySelector('.bar', renderTo).length).toBe(1);
            expect(getAllElementsBySelector('.marker', renderTo).length).toBe(1);
            const markerColor = d3.select(renderTo).select('.marker').select('path');
            expect(markerColor.attr('fill')).toEqual('blue');
        });
        it('should render two bars and three markers', function () {
            const testData = {
                color: 'red',
                name: 'data1',
                data: [
                    {
                        x: 0,
                        high: 0,
                        low: -10,
                    },
                    {
                        x: 1,
                        high: 45,
                        low: 25,
                        noStartDate: true
                    }
                ],
                point: {
                    events: {
                        click: () => {}
                    }
                }
            };
            const testMarkerData = {
                color: 'blue',
                type: 'scatter',
                name: 'data1',
                data: [
                    {
                        x: 0,
                        y: 5,
                        name: 'marker',
                        marker: {
                            fillColor: 'blue',
                            name: 'test',
                            symbol: 'diamond',
                        }
                    },
                    {
                        x: 7,
                        y: -3,
                        name: 'marker',
                        marker: {
                            fillColor: 'blue',
                            name: 'test',
                            symbol: 'diamond',
                        }
                    },
                    {
                        x: 2,
                        y: 9,
                        name: 'marker',
                        marker: {
                            fillColor: 'blue',
                            name: 'test',
                            symbol: 'diamond',
                        }
                    }
                ],
                point: {
                    events: {
                        click: () => {}
                    }
                }
            };

            d3.selectAll('svg').remove();
            testChart.addSeries(testData);
            testChart.addSeries(testMarkerData);
            testChart.redraw();
            expect(getAllElementsBySelector('.bar', renderTo).length).toBe(2);
            expect(getAllElementsBySelector('.marker', renderTo).length).toBe(3);
        });
    });
});
