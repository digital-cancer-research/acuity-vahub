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
import {
    getShape, selectionProperties,
} from '../utils/utils';
import {
    ColumnRangeBarItem,
    SeriesOptions,
    TimelineMarkerItem,
    UserOptions
} from '../types/interfaces';
import {tooltip_margin} from '../Chart';
import {TimelineChart} from './TimelineChart';

const MARKER_RADIUS = 10;
const TICKS_COUNT = 10;
const BAR_RATIO = 0.4;

export class TimelineBarChart extends TimelineChart {
    public markers = [];

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
        this.limits.x = null;
    }
    protected initSeries(options: SeriesOptions<ColumnRangeBarItem & TimelineMarkerItem>) {
        const newSeries = [...this.series];
        const data = [];
        if (options.type === 'scatter') {
            options.data.forEach((marker) => {
                if (typeof marker.y === 'number') { // filter everything we can't display
                    this.markers.push({
                        ...marker,
                        color: options.color,
                        name: options.name,
                        type: options.type,
                        subjectId: this.subjectId.subject,
                    });
                }
            });
        } else {
            options.data.forEach(
                item => {
                    data.push({
                        ...item,
                        type: options.type,
                        name: options.name,
                        category: this.xAxis[0].categories[item.x],
                        click: options.point.events.click,
                        subjectId: this.subjectId.subject,
                        ...selectionProperties
                    });
                });
            newSeries.push(data);
        }
        return newSeries;
    }

    protected drawXAxis(innerSpace: d3.Selection<SVGElement>) {
        this.xScale = d3.scaleBand()
            .domain([0])
            .padding(0.1)
            .range([0, this.height]);
        const scale = d3.axisLeft(this.xScale)
            .ticks(TICKS_COUNT)
            .tickFormat('')
            .tickSizeOuter(0);

        innerSpace.select('.y-axis')
            .call(scale);
    }

    protected drawYAxis(innerSpace: d3.Selection<SVGElement>) {
        this.yScale = d3.scaleLinear()
            .domain(this.limits.x)
            .range([0, this.width]);
        const scale = d3.axisBottom(this.yScale)
            .tickSize(this.height)
            .ticks(TICKS_COUNT)
            .tickFormat('');

        innerSpace.select('.x-axis')
            .call(scale);

        innerSpace.select('.no-axis-line')
            .select('path')
            .attr('stroke-width', '0');
    }

    public redraw(animate = false): void {
        const series = this.series.reduce((acc, val) => acc.concat(val), []);
        let innerSpace, barContainer, markerContainer;

        if (animate) {
            innerSpace = this.svg.select('g');
            barContainer = innerSpace.select('#bars');
            markerContainer = innerSpace.select('#markers');
        } else {
            if (!this.limits.x) {
                this.limits.x = [0, 0];
                series.forEach(item => {
                    if (this.limits.x[1] < item.high) {
                        this.limits.x[1] = item.high;
                    }
                    if (this.limits.x[0] > item.low) {
                        this.limits.x[0] = item.low;
                    }
                });
            }
            innerSpace = this.svg.append('g')
                .attr('transform', 'translate(' + this.margins.left + ',' + this.margins.top + ')');
            innerSpace.append('rect')
                .attr('width', this.width)
                .attr('height', this.height)
                .attr('class', 'background')
                .attr('fill', 'white')
                .on('dblclick', (event) => {
                    const coords = d3.pointer(event);
                    this.handleDblclick(this.yScale.invert(coords[0]));
                });

            this.addClipPath(innerSpace);

            innerSpace.append('clipPath')
                .attr('id',  `clip-markers-${this.id}`)
                .append('rect')
                .attr('x', - MARKER_RADIUS)
                .attr('y', 0)
                .attr('width', this.width + MARKER_RADIUS)
                .attr('height', this.height);


            innerSpace.append('g')
                .attr('class', 'x-axis timeline-x-axis');
            innerSpace.append('g')
                .attr('class', 'y-axis timeline-y-axis');

            innerSpace.append('g')
                .attr('class', 'plot-bands-container')
                .attr('clip-path', this.clipPathURL);
            innerSpace.append('g')
                .attr('class', 'line-container')
                .attr('clip-path', this.clipPathURL);

            barContainer = innerSpace.append('g')
                .attr('id', 'bars')
                .attr('clip-path', this.clipPathURL);

            markerContainer = innerSpace.append('g')
                .attr('id', 'markers')
                .attr('clip-path', `url(#clip-markers-${this.id})`);

            this.addTooltip();
        }

        this.drawXAxis(innerSpace);
        this.drawYAxis(innerSpace);

        const bars = barContainer
            .selectAll('.bar')
            .data(series);
        this.drawBars(bars, animate);

        const markers = markerContainer
            .selectAll('.marker')
            .data(this.markers);
        // draw markers
        this.drawMarkers(markers, animate);
        this.movePlotBands(true, animate);
        // move plotLines
        this.svg.select('.line-container')
            .selectAll('.plot-line')
            .transition()
            .duration(animate && this.animationTime)
            .attr('x1', d => this.yScale(d.value))
            .attr('x2', d => this.yScale(d.value));
    }

    public resize(): void {

        const innerSpace = this.svg.select('g');

        innerSpace.select('rect')
            .attr('width', this.width);

        innerSpace.select(`#${this.clipPathId}`)
            .select('rect')
            .attr('width', this.width);

        this.yScale
            .range([0, this.width]);

        const scale = d3.axisBottom(this.yScale)
            .tickSize(this.height)
            .ticks(TICKS_COUNT)
            .tickFormat('');

        innerSpace.select('.x-axis')
            .call(scale);
        // move bars
        this.drawBar(this.svg.selectAll('.bar'));

        // move markers
        this.svg.selectAll('.marker')
            .attr('transform', d => (`translate(${this.yScale(d.y) + MARKER_RADIUS}, ${this.xScale.bandwidth()}) rotate(-90)`));

        // move plotLines
        this.svg.select('.line-container')
            .selectAll('.plot-line')
            .attr('x1', d => this.yScale(d.value))
            .attr('x2', d => this.yScale(d.value));

        this.movePlotBands(true);
    }

    protected drawBars(source: d3.Selection<SVGElement>, animate = false) {
        const that = this;
        source.enter()
            .append('rect')
            .attr('class', 'bar')
            .attr('pointer-events', 'all')
            .on('mouseover', function (event, d) {
                const tooltipText = that.tooltip.formatter.call(d);
                d3.select('.chart-tooltip').transition().duration(500)
                    .style('opacity', 1);
                d3.select('.chart-tooltip').html(tooltipText)
                    .style('left', event.pageX + tooltip_margin + 'px')
                    .style('top', event.pageY + 'px');

                d3.select(this).style('opacity', .7);
            })
            .on('mouseout', function () {
                d3.select(this).style('opacity', 1);
                d3.selectAll('.chart-tooltip')
                    .style('opacity', '0');

                d3.select('.chart-tooltip')
                    .style('top', '-1000px');
            })
            .on('click', (event, d) => { d.click({
                point: {
                    high: d.high,
                    low: d.low
                },
                ctrlKey: event.ctrlKey,
                });
            })
            .on('dblclick', (event) => {
                const coords = d3.pointer(event);
                this.handleDblclick(this.yScale.invert(coords[0]));
            });
        this.drawBar(this.svg.select('#bars')
            .selectAll('.bar'), animate);
    }

    protected drawBar(rect: d3.Selection<SVGElement>, animate = false) {
        rect
            .attr('fill', d => (d.color))
            .transition()
            .duration(animate && this.animationTime)
            .attr('x', d => this.yScale(d.low))
            .attr('y', this.xScale.bandwidth() * BAR_RATIO)
            .attr('width', d => Math.abs(this.yScale(d.high) - this.yScale(d.low)))
            .attr('height', this.xScale.bandwidth() * BAR_RATIO);
    }

    protected drawMarkers(source: d3.Selection<SVGElement>, animate = false) {
        const that = this;
        const markers = source.enter()
            .append('g')
            .attr('class', 'marker')
            .on('mouseover', function (event, d) {
                    const tooltipText = that.tooltip.formatter.call(d);
                d3.select('.chart-tooltip').transition().duration(500)
                        .style('opacity', 1);
                    setTimeout(() => {
                        d3.select('.chart-tooltip').style('opacity', 0);
                    }, 5000);
                d3.select('.chart-tooltip').html(tooltipText)
                        .style('left', event.pageX + tooltip_margin + 'px')
                        .style('top', event.pageY + 'px');

                    d3.select(this)
                        .select('.highlight-marker')
                        .style('opacity', '0.2');
            })
            .on('mouseout', function () {
                d3.selectAll('.chart-tooltip')
                    .style('opacity', '0');
                d3.select(this)
                    .select('.highlight-marker')
                    .style('opacity', '0');

                d3.select('.chart-tooltip')
                    .style('top', '-1000px');
            });
        markers
            .filter(d => d.marker.symbol.indexOf('/') !== -1 )
            .append('image')
            .attr('xlink:href', d => d.marker.symbol)
            .attr('width', d => d.marker.width)
            .attr('height', d => d.marker.height)
            .attr('transform', d => `rotate(180) translate(${-d.marker.width})`);

        markers.append('path')
            .filter(d => d.marker.symbol.indexOf('/') === -1 )
            .attr('fill', d => d.marker.fillColor)
            .attr('transform', `translate(${this.xScale.bandwidth() * BAR_RATIO})`)
            .attr('d', d => (getShape(d.marker.symbol)()));

        markers.append('circle')
            .filter(d => d.marker.symbol.indexOf('/') === -1 )
            .attr('transform', `translate(${this.xScale.bandwidth() * BAR_RATIO})`)
            .attr('class', 'highlight-marker')
            .attr('fill', d => d.marker.fillColor)
            .attr('opacity', 0)
            .attr('r', MARKER_RADIUS);

        this.svg.select('#markers')
            .selectAll('.marker')
            .transition()
            .duration(animate && this.animationTime)
            .attr('transform', d => `translate(${this.yScale(d.y) + MARKER_RADIUS} , ${this.xScale.bandwidth()}) rotate(-90)`)
            .attr('opacity', d =>  (this.yScale(d.y) < 0) ? 0 : 1);
    }
}
