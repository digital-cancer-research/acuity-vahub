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
    groupBy,
    moveTicks,
    wrap,
    rotatedLabelsMargin,
    PLOT_COLORS,
    selectionProperties
} from '../utils/utils';
import {SeriesItem, SeriesOptions, UserOptions} from '../types/interfaces';
import {BAR_CLASS, BarChart, TICK_GAP} from './BarChart';


export class GroupedBarChart extends BarChart {
    public xSubScale: d3.ScaleBand<string>;
    public subCategories = [];
    public ticksOnEdges = true;

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
    }

    protected initSeries(options: SeriesOptions<SeriesItem>) {
        const newSeries = [...this.series];
        const data = [];
        options.data.forEach(
            item => {
                data.push({
                    ...item,
                    color: options.color,
                    name: options.name,
                    series: {
                        name: options.name
                    },
                    category: this.xAxis[0].categories[item.x],
                    ...selectionProperties
                });
                if (this.axis[item.x]) {
                    this.axis[item.x] += item.y;
                } else {
                    this.axis[item.x] = item.y;
                }
            });
        newSeries.push({
            data,
            points: data,
        });
        return newSeries;
    }

    protected drawBar(rect, animate = false) {
        rect
            .attr('fill', d => (d.selected ? PLOT_COLORS.selectedBarColor : d.color))
            .transition()
            .duration(animate && this.animationTime)
            .attr('x', d => (this.xSubScale(d.name) + this.getXCoordinate(d)))
            .attr('y', d => this.yScale(d.y))
            .attr('width', () => this.xSubScale.bandwidth())
            .attr('height', d => (this.yScale(0) - this.yScale(d.y)));
    }

    protected setXAxis() {
        this.xScale = d3.scaleBand()
            .domain(this.xAxis[0].getFilteredCategories(this.limits.x))
            .padding(0)
            .range([0, this.width]);

        this.xSubScale = d3.scaleBand()
            .domain(this.subCategories)
            .rangeRound([0, this.xScale.bandwidth()])
            .padding([0.2]);

        this.xAxisScale = d3.axisBottom(this.xScale)
            .tickValues(getCategoricalAxisValues(this.xScale.domain(), this.width));
    }

    protected drawAxis(innerSpace: d3.Selection<SVGElement>, chartHeight: number, rotatedTicksMargin: number, animate = false) {
        innerSpace.select('.y-axis')
            .transition()
            .duration(animate && this.animationTime)
            .call(this.yAxisScale);

        innerSpace.select('.x-axis')
            .call(this.xAxisScale)
            .transition()
            .duration(animate && this.animationTime)
            .attr('transform', `translate(0,${chartHeight})`);

        this.svg.select('.x-axis')
            .selectAll('.tick text')
            .call(wrap(rotatedTicksMargin && this.width));
        if (this.ticksOnEdges) {
            this.svg.select('.x-axis').selectAll('.tick')
                .call(moveTicks, this.xScale.bandwidth());
        }
    }

    protected setYAxis(chartHeight: number) {
        this.yScale = d3.scaleLinear()
            .domain(this.limits.y)
            .range([chartHeight, 0]);

        this.yAxisScale = d3.axisLeft(this.yScale)
            .tickSize(-this.width)
            .ticks(Math.floor(this.height / TICK_GAP));
    }

    public redraw(animate = false): void {
        const series = this.series.reduce((acc, val) => acc.concat(val.data), []);
        const groupedSeries = groupBy('x')(series);
        groupedSeries.forEach(el => {
            el.forEach(item => {
                if (this.subCategories.indexOf(item.name) === -1) {
                    this.subCategories.push(item.name);
                }
            });
        });

        this.setXAxis();
        const rotatedTicksMargin = rotatedLabelsMargin(this.xAxis[0], this.xScale, this.xAxisScale, this.width);
        const chartHeight = this.height - rotatedTicksMargin;
        this.setYAxis(chartHeight);
        let innerSpace, barsContainer;

        if (animate) {
            innerSpace = this.svg.select('g');
            this.resizeClipPath(chartHeight);
            barsContainer = innerSpace.select('#bars');
        } else {
            this.svg.selectAll('g').remove();
            innerSpace = this.drawInnerSpace();

            // handle click outside bars
            this.svg.on('click', (e) => {
                const tooltipWithContent = d3.selectAll(`.${BAR_CLASS}, .x-axis, .y-axis, .x-axis *, .y-axis * .${BAR_CLASS} *`);

                function equalToEventTarget() {
                    return this === e.target;
                }

                const outside = tooltipWithContent.filter(equalToEventTarget).empty();
                if (outside) {
                    this.removeSelection();
                }
            });

            innerSpace.append('g')
                .attr('class', 'x-axis')
                .attr('transform', `translate(0,${chartHeight})`);
            innerSpace.append('g')
                .attr('class', 'y-axis no-axis-line')
                .select('path')
                .attr('stroke-width', '0');

            this.addTooltip();
            this.addClipPath(innerSpace, chartHeight);

            barsContainer = innerSpace.append('g')
                .attr('id', 'bars')
                .attr('clip-path', this.clipPathURL);
        }

        this.drawAxis(innerSpace, chartHeight, rotatedTicksMargin, animate);

        const bars = barsContainer
            .selectAll(`.${BAR_CLASS}`)
            .data(series);
        this.enter(bars, animate);
    }

    protected handleSingleClickOnElement(event, d) {
        const request = Object.assign(event, {
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
                    min: 0,
                    max: d.y,
                }
            ],
            chartX: event.offsetX,
            chartY: event.offsetY,
            originalEvent: event,
            point: d,
        });
        this.click(request);
    }
}
