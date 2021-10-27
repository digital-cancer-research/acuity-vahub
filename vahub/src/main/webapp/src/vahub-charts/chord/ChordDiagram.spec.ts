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

import {ChordDiagram} from './ChordDiagram';
import * as d3 from 'd3';
import createSpy = jasmine.createSpy;
import {getAllElementsBySelector} from '../utils/test-utils';

const renderTo = document.createElement('div');

const click = createSpy('');
const select = createSpy('');

const testChordData = {
    'name': {
        start: 'START',
        end: 'END'
    },
    color: 'red',
    value: 20,
    subjects: [
        ['SUBJECT1', 1],
        ['SUBJECT2', 5]
    ],
    'data': [
        {
            'x': 0,
            'y': 1
        },
        {
            'x': 1,
            'y': 1
        }
    ],
    events: {
        click: click,
        selection: select
    }
};
const testArcData = {
    type: 'pie',
    innerSize: '90%',
    size: '100%',
    data: [
        {
            name: 'START',
            y: 1,
            color: 'blue'
        },
        {
            name: 'END',
            y: 1,
            color: 'green'
        }
    ]
};

describe('Chord diagram', function () {
    let testChart;
    beforeEach(function () {
        const testOptions = {
            chart: {
                renderTo: renderTo,
                type: 'range',
                height: 400,
                width: 800,
                id: 'test_id',
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
            exporting: {
                buttons: [{
                    text: 'testClick',
                    onclick: click,
                }]
            },
            tooltip: {
                formatter: () => null,
                onclick: () => {}
            }
        };
        testChart = new ChordDiagram(testOptions);
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

    describe('create chart elements', function () {

        it('should render two arcs', function () {
            testChart.addSeries(testChordData);
            testChart.addSeries(testArcData);
            testChart.redraw();
            expect(getAllElementsBySelector('.pie path', renderTo).length).toBe(2);
            const chordColor = d3.select(renderTo).select(`.pie path`).attr('fill');
            expect(chordColor).toEqual('blue');
        });

        it('should render one chord', function () {
            testChart.addSeries(testChordData);
            testChart.addSeries(testArcData);
            testChart.redraw();
            expect(getAllElementsBySelector('.ribbons path', renderTo).length).toBe(1);
            const chord = d3.select(renderTo).select(`.ribbons path`).attr('fill');
            expect(chord).toEqual('red');
        });
    });
});
