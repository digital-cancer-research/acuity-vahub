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
    getSelectionRequest,
    getShape,
    getTicksForColumnRange,
    MARGINS_INVERTED,
    PLOT_COLORS, selectionProperties,
} from '../utils/utils';
import {ColumnRangeBarItem, MarkerItem, SeriesOptions, UserOptions, ZoneSelection} from '../types/interfaces';
import {BAR_CLASS, BarChart, TICK_GAP} from './BarChart';

const barRatio = 0.33;

export class ColumnRangeChart extends BarChart {
    public margins = MARGINS_INVERTED;
    public markers = [];

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
    }

    protected initSeries(options: SeriesOptions<ColumnRangeBarItem | MarkerItem>) {
        const newSeries = [...this.series];
        const data = [];
        if (options.type === 'scatter') {
            options.data.forEach((marker) => {
                this.markers.push({
                    ...marker,
                    color: options.color,
                    name: options.name,
                });
            });
        } else {
            options.data.forEach(
                item => {
                    data.push({
                        ...item,
                        category: this.xAxis[0].categories[item.x],
                        ...selectionProperties
                    });
                });
            newSeries.push({
                data,
                points: data,
            });
        }
        return newSeries;
    }

    protected drawXAxis(innerSpace: d3.Selection<SVGElement>, animate = false) {
        this.xScale = d3.scaleLinear()
            .domain([this.limits.x[0], this.limits.x[1] + 1])
            .range([0, this.height]);
        this.xAxisScale = d3.axisLeft(this.xScale)
            .tickValues(getTicksForColumnRange(this.xAxis[0].categories, this.xScale.domain(), this.height))
            .tickFormat(d => (this.xAxis[0].categories[d]));

        if (!animate) {
            innerSpace.append('g')
                .attr('class', 'x-axis');
        }
        innerSpace.select('.x-axis')
            .transition()
            .duration(animate && this.animationTime)
            .call(this.xAxisScale);
        this.svg.selectAll('.x-axis .tick line, .x-axis .tick text')
            .attr('transform', () => `translate(0,${this.getBarHeight() / 2 })`);

        innerSpace.select('.x-axis')
            .selectAll('line')
            .style('color', PLOT_COLORS.lineColor);
    }

    protected drawYAxis(innerSpace: d3.Selection<SVGElement>, animate = false) {
        this.yScale = d3.scaleLinear()
            .domain(this.limits.y)
            .range([0, this.width]);

        this.yAxisScale = d3.axisBottom(this.yScale)
            .ticks(Math.floor(this.width / TICK_GAP));

        if (!animate) {
            innerSpace.append('g')
                .attr('class', 'y-axis')
                .attr('transform', `translate(0,${this.height})`);
        }
        innerSpace.select('.y-axis')
            .transition()
            .duration(animate && this.animationTime)
            .call(this.yAxisScale);

        innerSpace.select('.y-axis')
            .selectAll('line')
            .style('color', PLOT_COLORS.lineColor);
    }

    protected addBrush(innerSpace: d3.Selection<SVGElement>) {
        const getSelectionZone = (selection: number[][]): ZoneSelection => {
            const xMin = this.xScale.invert(selection[0][1]);
            const xMax = this.xScale.invert(selection[1][1]);
            const yMin = this.yScale.invert(selection[0][0]);
            const yMax = this.yScale.invert(selection[1][0]);
            return { xMin, xMax, yMin, yMax };
        };
        this.drawBrush(innerSpace, getSelectionZone);
    }

    protected getBarHeight () {
        return (this.height / (this.xScale.domain()[1] - this.xScale.domain()[0])) * barRatio;
    }

    public redraw(animate = false): void {
        const series = this.series.reduce((acc, val) => acc.concat(val.data), []);
        let innerSpace, barsContainer, markersContainer, centerLine, plotLinesContainer;

        if (animate) {
            innerSpace = this.svg.select('g');
            barsContainer = innerSpace.select('#bars');
            markersContainer = innerSpace.select('#markers');
            centerLine = innerSpace.select('#centered_line');
            plotLinesContainer = innerSpace.select('.plot-lines');
        } else {
            this.svg.selectAll('g').remove();
            innerSpace = this.drawInnerSpace();

            this.addClipPath(innerSpace);
            this.addBrush(innerSpace);
            this.addTooltip();
            plotLinesContainer = innerSpace.append('g')
                .attr('class', 'plot-lines')
                .attr('clip-path', this.clipPathURL);
            barsContainer = innerSpace.append('g')
                .attr('id', 'bars')
                .attr('clip-path', this.clipPathURL);
            markersContainer = innerSpace.append('g')
                .attr('id', 'markers')
                .attr('clip-path', this.clipPathURL);
            centerLine = innerSpace.append('line')
                .attr('id', 'centered_line')
                .attr('stroke-width', '1.5px')
                .attr('stroke', 'black');
        }

        this.drawXAxis(innerSpace, animate);
        this.drawYAxis(innerSpace, animate);

        const bars = barsContainer
            .selectAll(`.${BAR_CLASS}`)
            .data(series);
        this.enter(bars, animate);

        // draw line on center
        centerLine
            .transition()
            .duration(animate && this.animationTime)
            .attr('y1', 0)
            .attr('y2', this.height)
            .attr('x1', this.yScale(0))
            .attr('x2', this.yScale(0));

        const markers = markersContainer
            .selectAll('.marker')
            .data(this.markers);
        this.drawMarkers(markers, animate);

        //drawPlotLines
        this.drawPlotLines(plotLinesContainer, animate);
    }

    protected handleSingleClickOnElement(event, d) {
        const selectionCords = {
            xAxis: [
                {
                    value: d.x,
                    min: d.x,
                    max: d.x,
                    axis: {
                        categories: this.xAxis[0].categories
                    }
                }
            ],
            yAxis: [
                {
                    value: d.low,
                    min: d.low,
                    max: d.high,
                }
            ]
        };
        this.selection(getSelectionRequest(event, selectionCords, event.offsetX, event.offsetY));
    }

    protected drawBar(rect, animate = false) {
        rect
            .attr('fill', d => (d.selected ? PLOT_COLORS.selectedBarColor : d.color))
            .transition()
            .duration(animate && this.animationTime)
            .attr('x', d => (this.yScale(d.low)))
            .attr('y', d => (this.xScale(d.x)))
            .attr('width', (d) => (Math.abs(this.yScale(d.high) - this.yScale(d.low))))
            .attr('height', () => (this.getBarHeight()));
    }

    protected drawMarkers(markers: d3.Selection<SVGElement>, animate = false) {
        const that = this;
        const barHeight = this.getBarHeight();
        const newMarkers = markers.enter()
            .append('g')
            .attr('class', 'marker')
            .attr('transform', d => (`translate(${this.yScale(d.y)}, ${this.xScale(d.x) + barHeight / 2})`))
            .on('mouseover', function (event, d) {
                if (d.name !== 'startdate') {
                    const tooltipText = that.tooltip.formatter.call({...d, category: that.xAxis[0].categories[d.x]});
                    that.handleMouseOver(event, d, tooltipText);
                    d3.select(this)
                        .select('.highlight-marker')
                        .style('opacity', '0.2');
                    d3.select(this)
                        .select('path')
                        .attr('d', (el) => getShape(el.marker.symbol).size(100)());
                }
            })
            .on('mouseout', function () {
                that.handleMouseOut();
                d3.select(this)
                    .select('.highlight-marker')
                    .style('opacity', '0');
                d3.select(this)
                    .select('path')
                    .attr('d', (el) => getShape(el.marker.symbol).size(25)());
            });

        newMarkers.append('path')
            .filter((d) => (d.name !== 'startdate'))
            .attr('fill', d => (d.color))
            .attr('d', (d) => (getShape(d.marker.symbol).size(25)()))
            .filter((d) => (d.marker.symbol === 'triangle'))
            .attr('transform', 'rotate(90)');

        newMarkers.append('circle')
            .filter((d) => (d.name !== 'startdate'))
            .attr('class', 'highlight-marker')
            .attr('fill', d => (d.color))
            .attr('opacity', 0)
            .attr('r', '10');

        newMarkers
            .filter((d) => (d.name === 'startdate'))
            .append('image')
            .attr('xlink:href', (d) => (d.marker.symbol))
            .attr('transform', (d) => (`${d.marker.transform} translate(${-barHeight / 2}, 0)`))
            .attr('width', barHeight)
            .attr('height', barHeight / 2);

        markers
            .transition()
            .duration(animate && this.animationTime)
            .attr('transform', d => (`translate(${this.yScale(d.y)}, ${this.xScale(d.x) + barHeight / 2})`));
    }

}
