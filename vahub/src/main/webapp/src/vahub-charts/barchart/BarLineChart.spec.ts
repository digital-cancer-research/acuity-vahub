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

import {BarLineChart} from './BarLineChart';
import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {getAllElementsBySelector, getTestOptions} from '../utils/test-utils';
import {BAR_CLASS} from './BarChart';
import {fakeAsync, tick} from '@angular/core/testing';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');

const testLineData = {
    name: 'SUBJECTS',
    color: 'green',
    data: [
        {
          0: 1,
          1: 12,
        }, {
          0: 2,
          1: 123,
        }
    ]
};
const testTwoBarData = {
    name: 'data1',
    color: 'red',
    data: [
        {
            0: 0,
            1: 1,
        }, {
            0: 1,
            1: 2,
        }
    ]
};
const testOneBarData = {
    name: 'data2',
    color: 'red',
    data: [
        {
            0: 0,
            1: 1,
        }
    ]
};

describe('BarLine chart', function () {
    let testChart;
    beforeEach( function () {
        const testOptions = getTestOptions('barline', renderTo, click, select);
        testChart = new BarLineChart(testOptions);
    });

    afterEach( function () {
        d3.selectAll('svg').remove();
    });

    describe('create chart', function () {

        it('should render svg', function () {
            const svg = d3.select(renderTo).select('#svg_test_id');
            expect(svg).not.toBeNull();
            expect(svg.attr('height')).toEqual('400');
            expect(svg.attr('width')).toEqual('100%');
        });

        it ('should render export button', function () {
            expect(getAllElementsBySelector('.custom-export', renderTo).length).toBe(1);
        });

        it('should render title', function () {
            const title = renderTo.getElementsByClassName('chart-title');
            expect(title[0].innerHTML).toEqual('test title');
        });
    });

    describe('create bar', function () {
        it('should render xAxis', function () {
            testChart.addSeries(testLineData);
            testChart.redraw();
            expect(getAllElementsBySelector('.x-axis', renderTo).length).toBe(1);
        });
        it('should render Subjects Number Line', function () {
            testChart.addSeries(testLineData);
            testChart.redraw();
            expect(getAllElementsBySelector('.subjects-number-line', renderTo).length).toBe(1);
        });
        it('should render one bar', function () {
            testChart.addSeries(testLineData);
            testChart.addSeries(testOneBarData);
            testChart.redraw();
            expect(getAllElementsBySelector(`.${BAR_CLASS}`, renderTo).length).toBe(1);
            const barColor = d3.select(renderTo).select(`.${BAR_CLASS}`).attr('fill');
            expect(barColor).toEqual('red');
        });
        it('should render two bar', function () {
            testChart.addSeries(testLineData);
            testChart.addSeries(testTwoBarData);
            testChart.redraw();
            expect(getAllElementsBySelector(`.${BAR_CLASS}`, renderTo).length).toBe(2);
        });
    });

    describe('bar selection', function () {
        it('should call select when bar is selected', fakeAsync(function () {
            testChart.addSeries(testLineData);
            testChart.addSeries(testOneBarData);
            testChart.redraw();
            const bar = d3.select(renderTo).select(`.${BAR_CLASS}`);
            bar.node().dispatchEvent(new MouseEvent('click', {}));
            tick(500);
            expect(select).toHaveBeenCalledTimes(1);
        }));
    });
});
