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
import {TimelineAxis} from './TimelineAxis';
import {getAllElementsBySelector} from '../utils/test-utils';

const renderTo = document.createElement('div');

describe('Timeline axis', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = {
            chart: {
                renderTo: renderTo,
                type: 'timeline-xaxis',
                height: 400,
                width: 800,
                disableExport: true,
                events: {
                    selection: (): boolean => false,
                    click: (): boolean => false,
                    removeSelection: () => {
                    },
                }
            },
            title: {
                text: 'test title',
            },
            xAxis: [{
                borders: {
                    min: 0,
                    max: 100,
                }
            }]
        };
        testChart = new TimelineAxis(testOptions);
    });

    afterEach(function () {
        d3.select(renderTo).selectAll('svg').remove();
    });

    describe('create chart', function () {

        it('should render svg', function () {
            const svg = d3.select(renderTo).select('#axis');
            expect(svg._groups[0].length).toEqual(1);
            expect(svg.attr('height')).toEqual('400');
            expect(svg.attr('width')).toEqual('100%');
        });
    });

    it('should render xAxis', function () {
        const testData = {
            color: 'red',
            name: 'data1',
            data: [
                {
                    id: 0,
                    value: 5,
                }
            ],
            point: {
                events: {
                    click: () => {}
                }
            }
        };
        testChart.addSeries(testData);
        const axis = getAllElementsBySelector('.x-axis', renderTo);
        expect(axis.length).toEqual(1);
    });

});

