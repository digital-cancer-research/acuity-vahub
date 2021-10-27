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
    getSelectionRequest,
    wrap,
    getReversedScaledValue,
    getScaledCoordinate,
    rotatedLabelsMargin,
    MARGINS,
    BAR_LINE_MARGINS,
    PLOT_COLORS,
    selectionProperties
} from '../utils/utils';
import {BarLineData, SeriesOptions, UserOptions} from '../types/interfaces';
import {BarChart, BAR_CLASS} from './BarChart';
import {Axis} from '../axis';
import {MICROANIMATION_TIME} from '../linechart/AbstractLineChart';

export const TOOLTIP = {right: 44, top: 30, bottom: 50};
export const FOCUS = {radius: 6, stroke: 10, strokeOpacity: 0.25};
export const RANGE_ENLARGER = 1;
export const MeasureNumberOptions = 'SUBJECTS';
export const Y_RIGHT_SCALE_RATIO = 1.1;
export const ROTATED_LABELS_SHIFT = {x: -6, y: -2};

export class BarLineChart extends BarChart {
    public margins = {...MARGINS, ...BAR_LINE_MARGINS};
    public yRightScale: d3.ScaleLinear<string>;
    public yAxisRightScale: d3.scaleType;

    public constructor(
        options: UserOptions
    ) {
        super(options);
        this.init(options);
    }

    protected initSeries(options: SeriesOptions<BarLineData>) {
        const newSeries = [...this.series];
        const data = [];
        let dataMax = 0, dataMin = Infinity;
        options.data.forEach(
            item => {
                data.push({
                    color: options.color,
                    name: options.name,
                    series: {
                        name: options.name
                    },
                    y0: this.axis[item[0]] || 0,
                    category: this.xAxis[0].categories[item[0]],
                    x: item[0],
                    y: item[1],
                    ...item,
                    ...selectionProperties
                });
                if (this.axis[item[0]]) {
                    this.axis[item[0]] += item[1];
                } else {
                    this.axis[item[0]] = item[1];
                }

                dataMax = item[1] > dataMax ? item[1] : dataMax;
                dataMin = item[1] < dataMin ? item[1] : dataMin;
            }
        );
        newSeries.push({
            data,
            dataMax,
            dataMin,
            name: options.name
        });
        return newSeries;
    }

    public setAxis(): void {
        this.xAxis = [new Axis(this.onZoomX)];
        this.yAxis = [new Axis(this.onZoomY), new Axis()];
    }

    public redraw(animate = false): void {
        const that = this;
        let innerSpace, rectContainer, lineContainer;

        const series = this.series
            .filter(item => item.name !== MeasureNumberOptions)
            .reduce((acc, val) => acc.concat(val.data), []);

        const subjectsNumberSeries = this.series
            .find(item => item.name  === MeasureNumberOptions);

        const subjectsNumber = subjectsNumberSeries.data;
        const subjectsDataMax = subjectsNumberSeries.dataMax;
        const subjectsData = subjectsNumberSeries.data;

        this.xScale = d3.scaleBand()
            .domain(this.xAxis[0].getFilteredCategories(this.limits.x))
            .range([0, this.width]);

        this.xAxisScale = d3.axisBottom(this.xScale)
            .tickValues(getCategoricalAxisValues(this.xScale.domain(), this.width));

        const rotatedTicksMargin = rotatedLabelsMargin(this.xAxis[0], this.xScale, this.xAxisScale);
        const chartHeight = this.height - rotatedTicksMargin;

        const yDomain = () => {
            if (this.limits.y[0] === this.limits.y[1]) {
                if (this.limits.y[0] > RANGE_ENLARGER) {
                    return [this.limits.y[0] - RANGE_ENLARGER, this.limits.y[1] + RANGE_ENLARGER];
                }
                return [0, this.limits.y[1] + RANGE_ENLARGER];
            }
            return [this.limits.y[0], this.limits.y[1]];
        };

        this.yScale = d3.scaleLinear()
            .domain(yDomain())
            .range([chartHeight, 0]);

        this.yRightScale = d3.scaleLinear()
            .domain([0, subjectsDataMax * Y_RIGHT_SCALE_RATIO])
            .range([chartHeight, 0]);

        this.yAxisScale  = d3.axisLeft(this.yScale)
            .tickSize(0);

        this.yAxisRightScale = d3.axisRight(this.yRightScale)
            .tickSize(0);

        if (animate) {
            innerSpace = this.svg.select('g');
            innerSpace.select('.y-axis')
                .transition()
                .duration(this.animationTime)
                .call(this.yAxisScale);
            innerSpace.select('.x-axis')
                .transition()
                .duration(this.animationTime)
                .attr('transform', `translate(0, ${chartHeight})`)
                .call(this.xAxisScale)
                .on('end', () => {
                    innerSpace.select('.x-axis')
                        .selectAll('.tick text')
                        .call(wrap(rotatedTicksMargin && this.width, ROTATED_LABELS_SHIFT, this.xAxisScale.tickValues()));
                });
            rectContainer = innerSpace.select('#bars');
            lineContainer = innerSpace.select('#line');
            this.resizeClipPath(chartHeight);
        } else {
            this.svg.selectAll('g').remove();

            innerSpace = this.svg.append('g')
                .attr('transform', `translate(${this.margins.left}, ${this.margins.top})`);

            this.addClipPath(innerSpace);
            this.addTooltip();

            innerSpace.append('g')
                .attr('class', 'y-axis no-axis line')
                .call(this.yAxisScale)
                .select('path')
                .attr('stroke-width', '0');

            innerSpace.append('g')
                .attr('class', 'y-axis-right no-axis line')
                .attr('transform', `translate(${this.width}, 0)`)
                .call(this.yAxisRightScale)
                .select('path')
                .attr('stroke-width', '0');

            innerSpace.append('g')
                .attr('class', 'x-axis')
                .attr('transform', `translate(0, ${chartHeight})`)
                .call(this.xAxisScale)
                .select('path')
                .attr('stroke', PLOT_COLORS.lineColor);

            innerSpace.selectAll('text')
                .style('font-size', '11px');

            innerSpace.append('g').append('text')
                .attr('class', 'axis-label')
                .attr('x', 0)
                .attr('y', -this.width - this.margins.left)
                .attr('text-anchor', 'middle')
                .attr('font-size', '12px')
                .attr('color', PLOT_COLORS.axisLabelColor)
                .text(this.yAxis[1].title)
                .attr('style', `transform: rotate(90deg) translate(${that.height / 2}px)`);

            innerSpace
                .append('g')
                .attr('class', 'brush')
                .call(d3.brush()
                    .extent([[0, 0], [this.width, chartHeight]])
                    .filter(() => true)
                    .on('end', (event) => {
                        if (!event.selection) {
                            that.removeSelection();
                            this.update();
                            return;
                        }
                        const xMin = getReversedScaledValue(this.xAxis[0], this.xScale, event.selection[0][0]);
                        const xMax = getReversedScaledValue(this.xAxis[0], this.xScale, event.selection[1][0]);
                        const yMin = this.yScale.invert(event.selection[0][1]);
                        const yMax = this.yScale.invert(event.selection[1][1]);
                        const chartX = event.selection[1][0];
                        const chartY = event.selection[1][1];

                        const selectionCords = {
                            xAxis: [
                                {
                                    min: Math.min(xMin, xMax),
                                    max: Math.max(xMin, xMax),
                                    axis: {
                                        categories: that.xAxis[0].categories
                                    }
                                }
                            ],
                            yAxis: [
                                {
                                    min: Math.floor(Math.min(yMin, yMax)),
                                    max: Math.ceil(Math.max(yMin, yMax)),
                                }
                            ]
                        };
                        const originalEvent = {
                            ...event,
                            offsetX: chartX,
                            offsetY: chartY,
                            ctrlKey: event.sourceEvent.ctrlKey
                        };
                        that.selection(getSelectionRequest(event, selectionCords, chartX, chartY, originalEvent));
                        innerSpace.select('.brush').call(d3.brush().clear);
                    })
                );
            innerSpace.select('.brush .selection')
                .attr('fill', PLOT_COLORS.selectedBarColor)
                .attr('stroke', PLOT_COLORS.brushSelection)
                .attr('stroke-width', .5)
                .attr('id', 'brushSelection')
                .attr('pointer', 'crosshair')
                .attr('pointer-events', 'none');
            rectContainer =  innerSpace.append('g')
                .attr('id', 'bars')
                .attr('clip-path', this.clipPathURL);
            lineContainer = innerSpace
                .append('path')
                .attr('id', 'line')
                .attr('clip-path', this.clipPathURL);
            innerSpace.append('g')
                .append('circle')
                .attr('class', 'focus')
                .attr('r', 0)
                .style('fill', PLOT_COLORS.whisker)
                .style('stroke', PLOT_COLORS.whisker)
                .style('stroke-opacity', FOCUS.strokeOpacity)
                .style('stroke-width', FOCUS.stroke)
                .style('transition', `r ${MICROANIMATION_TIME}s`);
            innerSpace.append('use').attr('xlink:href', '#brushSelection');
        }

        innerSpace.select('.x-axis')
            .selectAll('.tick text')
            .call(wrap(rotatedTicksMargin && this.width, ROTATED_LABELS_SHIFT));

        const bars = rectContainer
            .selectAll(`.${BAR_CLASS}`)
            .data(series);

        const line = lineContainer
            .datum(subjectsNumber);

        this.enter(bars, animate, series, subjectsData);
        this.drawLine(line, subjectsNumber, series, subjectsData, animate);
    }

    protected drawLine(source: d3.Selection<SVGElement>, subjectsNumber, series, subjects, animate: boolean) {
        const that = this;
        const lineGenerator = d3.line()
            .x(d => this.getX(d) + this.xScale.bandwidth() / 2)
            .y(d => this.yRightScale(d.y));

        if (!animate) {
            source
                .attr('class', 'subjects-number-line')
                .attr('d', lineGenerator)
                .style('stroke-dasharray', '1')
                .attr('stroke', PLOT_COLORS.whisker)
                .attr('stroke-width', 1)
                .attr('fill', 'none')
                .attr('pointer-events', 'stroke')
                .on('mouseover', function (event) {
                    const xMouseOver = event.offsetX - that.margins.left;
                    const step = that.xScale.step();
                    const startIndex = that.limits.x[0];
                    const eventIndex = Math.floor(xMouseOver / step) + startIndex;
                    const rectX = +that.xScale(that.xAxis[0].categories[eventIndex]).toFixed(4);
                    const cx = rectX + step / 2;
                    const cy = that.yRightScale(subjects.find(el => el.x === eventIndex).y);
                    d3.selectAll(`.${BAR_CLASS}`).style('opacity', 1);
                    that.focusOn(cx, cy, rectX);
                    d3.select(this).style('stroke-width', 2);

                    const tooltipText = that.tooltip.formatter.call({
                        x: that.xAxis[0].categories[eventIndex],
                        points: series,
                        subjects: subjects
                    });
                    that.showTooltip(event, null, tooltipText, cx);
                    clearTimeout(that.tooltipTimeout);
                })
                .on('mouseout', () => {
                    that.tooltipTimeout = window.setTimeout(() => {
                        d3.select('.subjects-number-line').style('stroke-width', 1);
                        this.focusOut();
                        this.handleMouseOut();
                    }, 1000);
                });
            return;
        }
        d3.selectAll('.subjects-number-line')
            .transition()
            .duration(animate && this.animationTime)
            .attr('d', lineGenerator);
    }
    protected focusOn(cx: number, cy: number, rectX: number): void {
        d3.select('.focus')
            .attr('cx', cx )
            .attr('cy', cy)
            .attr('r', FOCUS.radius)
            .style('fill-opacity', 1)
            .style('pointer-events', 'none');

        d3.selectAll(`.${BAR_CLASS}[x="${rectX}"]`)
            .style('opacity', .7);
    }
    protected focusOut(): void {
        d3.selectAll('rect')
            .style('opacity', 1);

        d3.select('.focus')
            .attr('r', 0);
    }
    protected showTooltip(event, d, tooltipText = null, cx) {
        const yCord = d3.select('.focus').attr('cy') - event.offsetY + event.pageY - this.margins.bottom;
        const xCord = cx - event.offsetX + event.pageX;
        this.tooltipContainer.html(tooltipText || this.tooltip.formatter.call(d))
            .style('border', `1px solid ${PLOT_COLORS.tooltipBorderColor}`)
            .style('left', xCord + TOOLTIP.right + 'px')
            .style('top', yCord + 'px')
            .style('opacity', 1)
            .style('pointer-events', 'none');
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
                    max: d.y + d.y0
                }
            ]
        };
        this.selection(getSelectionRequest(event, selectionCords, event.offsetX, event.offsetY + TOOLTIP.top ));
    }
    protected drawBar(source, animate = false) {
        source
            .transition()
            .duration(animate && this.animationTime)
            .attr('x', d => this.getX(d))
            .attr('y', d => this.yScale(d.y0 + d.y))
            .attr('width', () => this.xScale.bandwidth())
            .attr('height', d => Math.abs(this.yScale(0) - this.yScale(d.y)));
    }
    protected enter(source, animate, series, subjects): void {
        const that = this;
        source.enter()
            .append('rect')
            .attr('class', BAR_CLASS)
            .attr('x', d => this.getX(d))
            .attr('y', d => this.yScale(d.y0 + d.y))
            .attr('width', () => this.xScale.bandwidth())
            .attr('height', d => Math.abs(this.yScale(0) - this.yScale(d.y)))
            .attr('fill', d => d.selected ? PLOT_COLORS.selectedBarColor : d.color)
            .attr('pointer-events', 'all')
            .style('transition', 'opacity .5s')
            .style('opacity', 1)
            .on('mouseover', function (event, d) {
                const cx = +d3.select(this).attr('x') + (d3.select(this).attr('width') / 2);
                const cy = that.yRightScale(subjects.find(el => el.x === d.x).y);
                const rectX = d3.select(this).attr('x');
                that.focusOn(cx, cy, rectX);

                const tooltipText = that.tooltip.formatter.call({
                    ...d,
                    x: that.xAxis[0].categories[d.x],
                    points: series,
                    subjects: subjects
                });
                that.showTooltip(event, d, tooltipText, cx);
            })
            .on('mouseout', function () {
                that.handleMouseOut();
                that.focusOut();
            })
            .on('click', (event, d) => that.handleClickOnElement(event, d));
        this.drawBar(source, animate);
    }

    private getX(d: BarLineData): number {
        return +(getScaledCoordinate(this.xAxis[0], this.xScale, this.limits.x, this.width, d.x, d.category)
            - this.xScale.bandwidth() / 2).toFixed(4);
    }
}
