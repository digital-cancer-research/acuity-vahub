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
import {Series, UserOptions} from '../types/interfaces';
import {Axis} from '../axis';
import {getShape, MARGINS_TIMELINE} from '../utils/utils';
import {Chart} from '../Chart';


export class TimelineAxis extends Chart {
    public xAxis: Array<Axis>;
    public xAxisBorders: {min: number, max: number};
    public margins = {...MARGINS_TIMELINE, top: 20};
    public animationTime = 0;

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
    }

    protected initSeries(): Series[] { return []; }
    public redraw(): void {}

    public init(options: UserOptions) {
        const svg = d3.select(options.chart.renderTo);
        this.container = options.chart.renderTo;
        this.xAxisBorders = options.xAxis[0].borders;
        this.animationTime = options.chart.animationTime;
        const id = 'axis';
        svg.append('svg')
            .attr('id', id)
            .attr('width', '100%')
            .attr('height', options.chart.height);
        this.xAxis = [new Axis(this.onZoomX)];
        const calculatedWidth = this.container.offsetWidth - this.margins.left - this.margins.right;
        this.width = calculatedWidth > 100 ? calculatedWidth : 100;
        this.svg = svg.select(`#${id}`);
        this.drawXAxis([this.xAxisBorders.min, this.xAxisBorders.max]);
    }

    private drawXAxis(domain: [number, number]) {
        this.xScale = d3.scaleLinear()
            .domain(domain)
            .range([0, this.width]);
        this.xAxisScale = d3.axisBottom(this.xScale);

        this.svg.append('g')
            .attr('class', 'x-axis')
            .attr('transform', `translate(${this.margins.left},${this.margins.top})`)
            .call(this.xAxisScale);
    }

    public onZoomX = (interval: [number, number]) => {
        this.xScale.domain(interval);
        this.svg.select('.x-axis')
            .transition()
            .duration(this.animationTime)
            .call(this.xAxisScale);
        this.svg.selectAll('.marker')
            .transition()
            .duration(this.animationTime)
            .attr('transform', d => `translate(${this.xScale(d.value)})`);

    }

    public addSeries(markers) {
        const renderedMarker = this.svg.select('.x-axis')
            .selectAll('.marker')
            .data(markers)
            .enter()
            .append('g')
            .attr('class', 'marker')
            .attr('id', d => d.id)
            .attr('transform', d => `translate(${this.xScale(d.value)})`)
            .on('mouseover', function () {
               d3.select(this)
                 .select('.highlight-marker')
                 .style('opacity', 0.3);
            })
            .on('mouseout', function () {
                d3.select(this)
                    .select('.highlight-marker')
                    .style('opacity', 0);
            });

        renderedMarker.append('path')
            .attr('fill', d => d.color)
            .attr('d', d => getShape(d.marker.symbol)())
            .attr('transform', d => d.transform);

        renderedMarker.append('circle')
            .attr('class', 'highlight-marker')
            .attr('fill', d => d.color)
            .attr('opacity', 0)
            .attr('r', d => d.marker.radius);
    }

    public removeMarker(marker: string) {
        this.svg.select('.x-axis')
            .select(`#${marker}`)
            .remove();
    }

    public resize() {
        this.xScale
            .range([0, this.width]);
        this.svg.select('.x-axis')
            .call(this.xAxisScale);
        this.svg.selectAll('.marker')
            .attr('transform', d => `translate(${this.xScale(d.value)})`);
    }

    public destroy(): void {
        d3.select('svg').remove();
    }

}
