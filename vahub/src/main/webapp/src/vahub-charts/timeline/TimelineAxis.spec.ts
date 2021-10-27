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

