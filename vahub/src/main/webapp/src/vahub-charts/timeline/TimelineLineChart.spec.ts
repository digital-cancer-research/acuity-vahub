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
import {TimelineLineChart} from './TimelineLineChart';
import {getAllElementsBySelector} from '../utils/test-utils';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');

const testOneMarkerData = {
    color: 'red',
    name: 'data1',
    data: [
        {
            x: 0,
            y: 5,
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

describe('Timeline line chart', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = {
            chart: {
                renderTo: renderTo,
                type: 'timeline-linechart',
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
        testChart = new TimelineLineChart(testOptions);
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
            const title = renderTo.getElementsByClassName('custom-export');
            expect(title.length).toBe(0);
        });
    });

    describe('create lines', function () {
        it('should render yAxis', function () {
            testChart.addSeries(testOneMarkerData);
            testChart.redraw();
            expect(getAllElementsBySelector('.y-axis', renderTo).length).toBe(1);
        });
        it('should render line and two markers', function () {
            testChart.addSeries(testOneMarkerData);
            testChart.redraw();
            expect(getAllElementsBySelector('.line', renderTo).length).toBe(1);
            expect(getAllElementsBySelector('.marker', renderTo).length).toBe(1);
            const markerColor = d3.select(renderTo).select('.marker').select('path');
            expect(markerColor.attr('fill')).toEqual('blue');
        });
        it('should render two markers', function () {
            const testTwoMarkersData =  {
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
                    }
                ],
                point: {
                    events: {
                        click: () => {}
                    }
                }
            };
            testChart.addSeries(testTwoMarkersData);
            testChart.redraw();
            expect(getAllElementsBySelector('.marker', renderTo).length).toBe(2);
        });
    });
});
