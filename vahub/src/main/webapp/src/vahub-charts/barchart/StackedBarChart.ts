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
    getCategoricalAxisValues,
    getReversedScaledValue,
    getSelectionRequest,
    wrap,
    rotatedLabelsMargin,
    PLOT_COLORS,
    selectionProperties
} from '../utils/utils';
import {SeriesItem, SeriesOptions, UserOptions, ZoneSelection} from '../types/interfaces';
import {BAR_CLASS, BarChart, TICK_GAP} from './BarChart';

export const ROTATED_LABELS_SHIFT = {x: -8, y: -4};

export class StackedBarChart extends BarChart {

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
    }

    protected initSeries(options: SeriesOptions<SeriesItem>) {
        const newSeries = [...this.series];
        let dataMax = 0, dataMin = 0;
        const data = [];
        options.data.forEach(
            item => {
                data.push({
                    color: options.color,
                    name: options.name,
                    series: {
                        name: options.name
                    },
                    y0: this.axis[item.x] || 0,
                    category: this.xAxis[0].categories[item.x],
                    ...item,
                    ...selectionProperties
                });
                if (this.axis[item.x]) {
                    this.axis[item.x] += item.y;
                } else {
                    this.axis[item.x] = item.y;
                }

                dataMax = this.axis[item.x] > dataMax ? this.axis[item.x] : dataMax;
                dataMin = this.axis[item.x] < dataMin ? this.axis[item.x] : dataMin;
            });
        newSeries.push({
            data,
            points: data,
            dataMax,
            dataMin
        });
        return newSeries;
    }

    protected addBrush(innerSpace: d3.Selection<SVGElement>, chartHeight: number) {
        const getSelectionZone = (selection: number[][]): ZoneSelection => {
            const xMin = getReversedScaledValue(this.xAxis[0], this.xScale, selection[0][0]);
            const xMax = getReversedScaledValue(this.xAxis[0], this.xScale, selection[1][0]);
            const yMin = this.yScale.invert(selection[0][1]);
            const yMax = this.yScale.invert(selection[1][1]);
            return { xMin, xMax, yMin, yMax };
        };
        this.drawBrush(innerSpace, getSelectionZone, chartHeight);
    }

    public redraw(animate = false): void {
        const series = this.series.reduce((acc, val) => acc.concat(val.data), []);
        let innerSpace, barsContainer, plotLinesContainer;

        this.xScale = d3.scaleBand()
            .domain(this.xAxis[0].getFilteredCategories(this.limits.x))
            .padding(0.3)
            .range([0, this.width]);

        this.xAxisScale = d3.axisBottom(this.xScale)
            .tickValues(getCategoricalAxisValues(this.xScale.domain(), this.width));

        const rotatedTicksMargin = rotatedLabelsMargin(this.xAxis[0], this.xScale, this.xAxisScale, this.width, true);
        const chartHeight = this.height - rotatedTicksMargin;

        this.yScale = d3.scaleLinear()
            .domain(this.limits.y)
            .range([chartHeight, 0]);

        this.yAxisScale = d3.axisLeft(this.yScale)
            .tickSize(-this.width)
            .ticks(Math.floor(this.height / TICK_GAP));

        if (animate) {
            innerSpace = this.svg.select('g');
            innerSpace.select('.y-axis')
                .transition()
                .duration(this.animationTime)
                .call(this.yAxisScale);
            innerSpace.select('.x-axis')
                .transition()
                .duration(this.animationTime)
                .call(this.xAxisScale)
                .attr('transform', `translate(0, ${chartHeight})`)
                .on('end', () => {
                    innerSpace.select('.x-axis')
                        .selectAll('.tick text')
                        .call(wrap(rotatedTicksMargin && this.width, ROTATED_LABELS_SHIFT, this.xAxisScale.tickValues()));
                });
            barsContainer = innerSpace.select('#bars');
            plotLinesContainer = innerSpace.select('.plot-lines');
            this.resizeClipPath(chartHeight);
        } else {
            this.svg.selectAll('g').remove();
            innerSpace = this.drawInnerSpace();

            innerSpace.append('g')
                .attr('class', 'y-axis no-axis-line')
                .call(this.yAxisScale)
                .selectAll('line')
                .attr('stroke', PLOT_COLORS.lineColor);

            innerSpace.select('.no-axis-line')
                .select('path')
                .attr('stroke-width', '0');

            innerSpace.append('g')
                .attr('class', 'x-axis')
                .call(this.xAxisScale)
                .attr('transform', `translate(0,${chartHeight})`)
                .selectAll('path, line')
                .attr('stroke', PLOT_COLORS.lineColor);

            this.addTooltip();
            this.addClipPath(innerSpace, chartHeight);
            this.addBrush(innerSpace, chartHeight);

            barsContainer = innerSpace.append('g')
                .attr('id', 'bars')
                .attr('clip-path', this.clipPathURL);

            plotLinesContainer = innerSpace.append('g')
                .attr('class', 'line-container')
                .attr('clip-path', this.clipPathURL);
        }

        innerSpace.select('.x-axis')
            .selectAll('.tick text')
            .call(wrap(rotatedTicksMargin && this.width, ROTATED_LABELS_SHIFT, this.xAxisScale.tickValues()));

        this.drawPlotLines(plotLinesContainer, animate);
        const bars = barsContainer
            .selectAll(`.${BAR_CLASS}`)
            .data(series);
        this.enter(bars, animate);
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
                    value: d.y,
                    min: d.y0,
                    max: d.y + d.y0,
                }
            ]
        };
        this.selection(getSelectionRequest(event, selectionCords, event.offsetX, event.offsetY));
    }

    protected drawBar(source, animate = false) {
        source
            .attr('fill', d => (d.selected ? PLOT_COLORS.selectedBarColor : d.color))
            .transition()
            .duration(animate && this.animationTime)
            .attr('x', d => this.getXCoordinate(d))
            .attr('y', d => (this.yScale(Math.max(0, d.y0 + d.y))))
            .attr('width', () => (this.xScale.bandwidth()))
            .attr('height', d => (Math.abs(this.yScale(0) - this.yScale(d.y))));
    }
}
