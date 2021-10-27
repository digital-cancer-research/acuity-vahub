import {StackedBarChart} from './StackedBarChart';
import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {getAllElementsBySelector, getTestOptions} from '../utils/test-utils';
import {BAR_CLASS} from './BarChart';
import {fakeAsync, tick} from '@angular/core/testing';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');

const testOneBarData = {
    color: 'red',
    name: 'data1',
    data: [
        {
            x: 0,
            y: 5,
        }
    ]
};
const testTwoBarData = {
    color: 'red',
    name: 'data1',
    data: [
        {
            x: 0,
            y: 5,
        },
        {
            x: 1,
            y: 5,
        }
    ]
};

describe('StackedBar chart', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = getTestOptions('stacked-bar-chart', renderTo, click, select);
        testChart = new StackedBarChart(testOptions);
    });

    afterEach(function () {
        d3.selectAll('svg').remove();
    });

    describe('create chart', function () {

        it('should render svg', function () {
            const svg = d3.select(renderTo).select('#svg_test_id');
            expect(svg.attr('height')).toEqual('400');
            expect(svg.attr('width')).toEqual('100%');
        });

        it('should render title', function () {
            const title = renderTo.getElementsByClassName('chart-title');
            expect(title[0].innerHTML).toEqual('test title');
        });

        it('should render export button', function () {
            expect(getAllElementsBySelector('.custom-export', renderTo).length).toBe(1);
        });
    });

    describe('create bars', function () {
        it('should render one bar', function () {
            testChart.addSeries(testOneBarData);
            testChart.redraw();
            expect(getAllElementsBySelector(`.${BAR_CLASS}`, renderTo).length).toBe(1);
            const barColor = d3.select(renderTo).select(`.${BAR_CLASS}`).attr('fill');
            expect(barColor).toEqual('red');
        });

        it('should render two bars', function () {
            testChart.addSeries(testTwoBarData);
            testChart.redraw();
            expect(getAllElementsBySelector(`.${BAR_CLASS}`, renderTo).length).toBe(2);
        });

        it('should render xAxis', function () {
            testChart.addSeries(testOneBarData);
            testChart.redraw();
            expect(getAllElementsBySelector('.x-axis', renderTo).length).toBe(1);
        });

        it('should render yAxis', function () {
            testChart.addSeries(testOneBarData);
            testChart.redraw();
            expect(getAllElementsBySelector('.y-axis', renderTo).length).toBe(1);
        });
    });
    describe('bar selection', function () {
        it('should call select when bar is selected', fakeAsync(function () {
            testChart.addSeries(testTwoBarData);
            testChart.redraw();
            const bar = d3.select(renderTo).select(`.${BAR_CLASS}`);
            bar.node().dispatchEvent(new MouseEvent('click', {}));
            tick(500);
            expect(select).toHaveBeenCalledTimes(1);
        }));
    });
});

