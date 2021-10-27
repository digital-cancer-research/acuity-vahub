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
    ColumnRangeBarItem,
    PlotLine,
    SeriesOptions,
    TimelineMarkerItem,
    UserOptions
} from '../types/interfaces';
import {tooltip_margin} from '../Chart';
import {TimelineChart} from './TimelineChart';
import {getShape, MARGINS_LINE_TIMELINE, selectionProperties} from '../utils/utils';

const DOT_RADIUS = 8;
const MARKER_RADIUS = 10;
const TICKS_COUNT = 3;
const LINE_OFFSET = 5;

export class TimelineLineChart extends TimelineChart {
    public margins = MARGINS_LINE_TIMELINE;
    public markers = [];
    public plotLines: PlotLine[] = [];

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
        this.limits.x = null;
        this.limits.y = null;
    }

    protected lineGenerator = d3.line()
        .x((d) => this.xScale(d.x)) // set the x values for the line generator
        .y((d) => this.yScale(d.y)) // set the y values for the line generator
        .curve(d3.curveLinear);

    protected initSeries(options: SeriesOptions<ColumnRangeBarItem | TimelineMarkerItem>) {
        const newSeries = [...this.series];
        const data = [];
        options.data.forEach(item => {
            if (item) {
                data.push({
                    type: options.type,
                    name: options.name,
                    color: options.color,
                    subjectId: this.subjectId.subject,
                    ...item,
                    ...selectionProperties
                });
            }
        });
        if (data.length) {
            newSeries.push(data);
        }
        return newSeries;
    }

    protected drawXAxis(innerSpace: d3.Selection<SVGElement>, animate = false) {
        this.xScale = d3.scaleLinear()
            .domain(this.limits.x)
            .range([0, this.width]);

        this.xAxisScale = d3.axisBottom(this.xScale)
            .tickSize(this.height)
            .tickFormat('');

        if (!animate) {
            innerSpace.append('g')
                .attr('class', 'x-axis no-axis-line');
        }
        innerSpace.select('.x-axis')
            .transition()
            .duration(animate && this.animationTime)
            .call(this.xAxisScale);
    }

    protected drawYAxis(innerSpace: d3.Selection<SVGElement>, animate = false) {
        this.yScale = d3.scaleLinear()
            .domain(this.limits.y)
            .range([this.height, 0]);

        this.yAxisScale = d3.axisLeft(this.yScale)
            .ticks(TICKS_COUNT)
            .tickSize(-this.width);

        if (!animate) {
            innerSpace.append('g')
                .attr('class', 'y-axis');
        }
        innerSpace.select('.y-axis')
            .transition()
            .duration(animate && this.animationTime)
            .call(this.yAxisScale);
    }

    public redraw(animate = false): void {
        const series = this.series.reduce((acc, val) => acc.concat(val), []);
        let innerSpace, linesContainer, markersContainer;

        if (animate) {
            innerSpace = this.svg.select('g');
            linesContainer = innerSpace.select('#lines');
            markersContainer = linesContainer.select('#markers');
        } else {
            if (!this.limits.x || !this.limits.y) {
                this.limits.x = [0, 0];
                this.limits.y = [0, 0];
                series.forEach(item => {
                    if (this.limits.y[1] < item.y) {
                        this.limits.y[1] = item.y;
                    }
                    if (this.limits.y[0] > item.y) {
                        this.limits.y[0] = item.y;
                    }
                    if (this.limits.x[1] < item.x) {
                        this.limits.x[1] = item.x;
                    }
                    if (this.limits.x[0] > item.x) {
                        this.limits.x[0] = item.x;
                    }
                });
            }

            this.svg.selectAll('g').remove();
            innerSpace = this.svg.append('g')
                .attr('transform', 'translate(' + this.margins.left + ',' + this.margins.top + ')');

            innerSpace.append('rect')
                .attr('width', this.width)
                .attr('height', this.height)
                .attr('class', 'background')
                .attr('fill', 'white')
                .on('dblclick', (event) => {
                    const coords = d3.pointer(event);
                    this.handleDblclick(this.xScale.invert(coords[0]));
                });

            this.addClipPath(innerSpace, this.height + 2 * LINE_OFFSET);
            innerSpace.select(`#${this.clipPathId}`)
                .attr('y', -LINE_OFFSET);

            innerSpace.append('g')
                .attr('class', 'plot-bands-container')
                .attr('clip-path', this.clipPathURL);

            innerSpace.append('g')
                .attr('class', 'line-container')
                .attr('clip-path', this.clipPathURL);

            this.addTooltip();

            linesContainer = innerSpace.append('g')
                .attr('id', 'lines')
                .attr('clip-path', this.clipPathURL);
            markersContainer = linesContainer
                .append('g')
                .attr('id', 'markers');
        }

        this.drawXAxis(innerSpace, animate);
        this.drawYAxis(innerSpace, animate);

        const lines = linesContainer
            .selectAll('.line')
            .data(this.series);
        this.drawLines(lines, animate);

        const dots = markersContainer
            .selectAll('.marker')
            .data(series);
        this.drawMarkers(dots, animate);

        // move plotLines
        this.movePlotBands(false, animate);

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

        this.xScale
            .range([0, this.width]);

        this.xAxisScale = d3.axisBottom(this.xScale)
            .tickSize(this.height)
            .tickFormat('');

        this.svg.select('.x-axis')
            .call(this.xAxisScale);

        this.yAxisScale = d3.axisLeft(this.yScale)
            .ticks(TICKS_COUNT)
            .tickSize(-this.width);

        this.svg.select('.y-axis')
            .call(this.yAxisScale);

        // move lines
        this.svg.selectAll('.line')
            .attr('d', d => this.lineGenerator(d));

        // move dots
        this.svg.select('#lines').selectAll('.marker')
            .attr('transform', d => (`translate(${this.xScale(d.x)}, ${this.yScale(d.y)})`));

        // move bands
        this.movePlotBands();

        // move plotLines
        this.svg.select('.line-container')
            .selectAll('.plot-line')
            .attr('x1', d => this.xScale(d.value))
            .attr('x2', d => this.xScale(d.value));
    }

    protected drawLines(source: d3.Selection<SVGElement>, animate = false) {
        source.enter()
            .append('path')
            .attr('class', 'line')
            .on('mouseover', function () {
                d3.select(this).attr('stroke-width', 3);
            })
            .on('mouseout', function () {
                d3.select(this).attr('stroke-width', 2);
            })
            .attr('pointer-events', 'all')
            .attr('fill', 'none')
            .attr('stroke', d => d[0].color || 'red')
            .attr('stroke-width', 2);

        this.svg.selectAll('.line')
            .transition()
            .duration(animate && this.animationTime)
            .attr('d', d => this.lineGenerator(d));
    }

    protected drawMarkers(source: d3.Selection<SVGElement>, animate = false) {
        const that = this;
        const markers = source.enter()
            .append('g')
            .attr('class', 'marker')
            .on('mouseover', function (event, d) {
                    const tooltipText = that.tooltip.formatter.call(d);
                    that.tooltipContainer.transition().duration(500)
                        .style('opacity', 1);
                    setTimeout(() => {
                        that.tooltipContainer.style('opacity', 0);
                    }, 5000);
                    that.tooltipContainer.html(tooltipText)
                        .style('left', event.pageX + tooltip_margin + 'px')
                        .style('top', event.pageY + 'px');

                    d3.select(this)
                        .select('path')
                        .style('opacity', '1');

                    d3.select(this)
                        .select('.highlight-marker')
                        .style('opacity', '0.2');

                d3.select(this).attr('r', DOT_RADIUS);
            })
            .on('mouseout', function () {
                d3.selectAll('.chart-tooltip')
                    .style('opacity', '0');

                d3.select(this)
                    .select('path')
                    .style('opacity', d => ( d.marker.enabled === false ? 0 : 1));

                d3.select(this)
                    .select('.highlight-marker')
                    .style('opacity', '0');

                that.tooltipContainer
                    .style('top', '-1000px');
            });

        markers.append('path')
            .attr('fill', d => d.marker.fillColor || d.color)
            .attr('d', d => getShape(d.marker.symbol)())
            .attr('transform', d => d.transform)
            .style('opacity', d =>  d.marker.enabled === false ? 0 : 1); // important that enabled equals false and not undefined

        markers.append('circle')
            .attr('class', 'highlight-marker')
            .attr('fill', d => d.marker.fillColor)
            .attr('opacity', 0)
            .attr('r', MARKER_RADIUS);

        this.svg.selectAll('.marker')
            .transition()
            .duration(animate && this.animationTime)
            .attr('transform', d => `translate(${this.xScale(d.x)}, ${this.yScale(d.y)})`);

    }

    public addPlotLine (line: PlotLine) {
        const domain = this.yScale.domain();
        if (line.value > domain[1]) {
            this.onZoomY([domain[0], line.value]);
        } else if (line.value < domain[0]) {
            this.onZoomY([line.value, domain[1]]);
        }
        const that = this;
        this.svg.select('.line-container')
            .selectAll(`#id-${Math.floor(line.value)}`)
            .data([line])
            .enter()
            .append('line')
            .attr('class', 'horizontal-plot-line')
            .attr('id', d => `id-${Math.floor(d.value)}`)
            .attr('stroke-width', d => d.width + 'px')
            .attr('stroke-dasharray', '4,3')
            .attr('fill', 'none')
            .attr('stroke', d =>  d.color)
            .attr('y1', d => that.yScale(d.value))
            .attr('y2', d => that.yScale(d.value))
            .attr('x1', 0)
            .attr('x2', that.width);
    }

}
