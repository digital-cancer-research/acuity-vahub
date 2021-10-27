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
import {PLOT_COLORS, selectionProperties} from '../utils/utils';
import {Chart} from '../Chart';
import {
    ChordDiagramData,
    ChordDiagramPie,
    D3ArcData,
    D3ChordData,
    ExtendedChordData,
    Series,
    UserOptions
} from '../types/interfaces';

export const MIN_RELATIVE_VALUE_FOR_LABELING = 0.020;
export const CHART_PADDING_COEFFICIENT = 0.9;
export const TOOLTIP_MARGIN = 15;
export const LABEL_RADIUS_COEFFICIENTS = {
    label: 1.05,
    line: 1.025
};
export const LABEL_FONT_SIZE = 10;
export const ARC_PADDING_COEFFICIENT = 0.25;

export class ChordDiagram extends Chart {
    private pieData: ChordDiagramPie;
    private matrix: number[][] = [];
    private isTooltipListCollapsed = true;
    private totalArchValues = 0;
    public chordData: ExtendedChordData;
    public chordMatrix: number[][] = [];

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
    }

    protected initSeries(options: ChordDiagramData): Series[] {
        if (options.type === 'pie') {
            this.pieData = options as ChordDiagramPie;
            this.totalArchValues = 0;
            this.createBlankChordMatrix();
            return this.series;
        }
        return [...this.series, {
            ...options,
            ...selectionProperties
        }];
    }

    private createBlankChordMatrix(): void {
        this.matrix = [];
        const matrixSize = this.pieData.data.length - 1;
        for (let i = 0; i <= matrixSize; i += 1) {
            this.matrix[i] = [];
            this.chordMatrix[i] = [];
            for (let j = 0; j <= matrixSize; j += 1) {
                this.matrix[i][j] = 0;
            }
        }
    }

    private fillChordMatrix(serieData): void {
        const {value, name} = serieData;
        this.totalArchValues += value;
        const from = this.pieData.data.findIndex(data => data.name === name.start);
        const to = this.pieData.data.findIndex(data => data.name === name.end);
        this.matrix[from][to] = value;
        this.matrix[to][from] = value;
        this.chordMatrix[from][to] = serieData;
    }

    private getChordData(d: D3ChordData): Series {
        const from = this.pieData.data[d.source.index].name;
        const to = this.pieData.data[d.target.index].name;
        return this.series.find(data => data.name.start === from && data.name.end === to);
    }

    private preformChordData(plotPieData: ChordDiagramPie) {
        this.chordData = plotPieData.map((serie: D3ChordData) => {
            return {
                chordData: this.getChordData(serie),
                ...serie
            };
        });
        this.chordData.groups = plotPieData.groups.map((group: D3ArcData) => {
            return {
                ...group,
                ...this.pieData.data[group.index],
                type: 'pie',
                angle: (group.startAngle + group.endAngle) / 2
            };
        });
    }

    private drawArcLabels(arcContainers: d3.Selection<HTMLDivElement>, outerRadius: number): void {
        const labelsRadius = outerRadius * LABEL_RADIUS_COEFFICIENTS.label;
        const labelLineRadius = outerRadius * LABEL_RADIUS_COEFFICIENTS.line;
        const {cos, sin, sign, PI, abs} = Math;
        const xPositionCoefficient = (d) => (0.25 * sign(sin(d.angle)) + 0.75 * sin(d.angle));
        let lastY = -this.height / 2;
        let lastAngle = 0;
        const getLabelY = (d) => {
            const newY = -labelsRadius * cos(d.angle);
            const yDiff = newY - lastY;
            const isHalfCirclePassed = lastAngle <= PI && d.angle > PI;
            if (abs(yDiff) < LABEL_FONT_SIZE && !isHalfCirclePassed) {
                lastY = newY + (sign(yDiff) * LABEL_FONT_SIZE - yDiff);
            } else {
                lastY = newY;
            }
            lastAngle = d.angle;
            return lastY;
        };
        arcContainers
            .append('text')
            .filter(d => d.value / this.totalArchValues > MIN_RELATIVE_VALUE_FOR_LABELING)
            .attr('x', d => labelsRadius * xPositionCoefficient(d))
            .attr('y', d => getLabelY(d))
            .attr('text-anchor', d => d.angle < PI ? 'start' : 'end')
            .attr('font-size', LABEL_FONT_SIZE + 'px')
            .attr('color', PLOT_COLORS.lineColor)
            .text(d => d.name);
        arcContainers
            .append('line')
            .filter(d => d.value / this.totalArchValues > MIN_RELATIVE_VALUE_FOR_LABELING)
            .attr('x1', d => outerRadius * sin(d.angle))
            .attr('x2', d => labelLineRadius * xPositionCoefficient(d))
            .attr('y1', d => -outerRadius * cos(d.angle))
            .attr('y2', d => getLabelY(d))
            .attr('stroke', d => d.color)
            .attr('stroke-width', 2);
    }

    redraw(): void {
        if (this.noDataMessage) {
            this.showNoData();
            return;
        }
        const that = this;
        this.totalArchValues = 0;
        this.series.forEach(serie => this.fillChordMatrix(serie));

        this.svg.selectAll('g').remove();
        this.svg.attr('cursor', 'crosshair');

        this.svg.on('click', function (e) {
            if (e.target === this) {
                that.removeSelection();
            }
        });

        const innerSpace = this.svg.append('g')
            .attr('transform', 'translate(' + this.margins.left + ',' + this.margins.top / 2 + ')');

        this.addTooltip();
        this.tooltipContainer
            .style('transition', 'opacity .6s')
            .on('mouseover', () => {
                clearTimeout(that.tooltipTimeout);
            })
            .on('click', that.tooltip.onclick);

        const plotPieData = d3.chord()
            .padAngle(ARC_PADDING_COEFFICIENT / this.pieData.data.length)
            (this.matrix);
        this.preformChordData(plotPieData);

        const innerRadius = CHART_PADDING_COEFFICIENT * this.height / 2 * parseInt(this.pieData.innerSize, 10) / 100;
        const outerRadius = CHART_PADDING_COEFFICIENT * this.height / 2 * parseInt(this.pieData.size, 10) / 100;
        const arcContainers = innerSpace
            .append('g')
            .attr('class', 'pie')
            .attr('transform', `translate(${this.width / 2}, ${this.height / 2})`)
            .datum(this.chordData)
            .selectAll('g')
            .data(d => d.groups)
            .enter()
            .append('g')
            .on('mouseover', function (event, d) {
                that.handleMouseOver(event, d);
                d3.select(this).select('path').attr('fill-opacity', .7);
            })
            .on('mousemove', function (event) {
                that.tooltipContainer
                    .style('left', event.pageX + TOOLTIP_MARGIN + 'px')
                    .style('top', event.pageY + 'px');
            })
            .on('mouseout', function () {
                d3.select(this).select('path').attr('fill-opacity', 1);
                that.handleMouseOut();
            });
        arcContainers
            .append('path')
            .attr('fill', d => d.color)
            .attr('fill-opacity', 1)
            .attr('d', d3.arc()
                .innerRadius(innerRadius)
                .outerRadius(outerRadius)
            );
        this.drawArcLabels(arcContainers, outerRadius);

        innerSpace
            .append('g')
            .attr('class', 'ribbons')
            .attr('transform', `translate(${this.width / 2}, ${this.height / 2})`)
            .datum(this.chordData)
            .append('g')
            .selectAll('path')
            .data(d => d)
            .enter()
            .append('path')
            .attr('d', d3.ribbon().radius(innerRadius))
            .attr('fill', d => d.chordData.color)
            .attr('fill-opacity', 1)
            .attr('stroke', d => d.chordData.color)
            .attr('stroke-width', .25)
            .on('mouseover', function (event, d) {
                clearTimeout(that.tooltipTimeout);
                that.tooltipTimeout = window.setTimeout(() => {
                    that.isTooltipListCollapsed = true;
                    that.handleMouseOver(event, d, null, 250);
                }, 600);

                d3.select(this).attr('fill-opacity', .7);
            })
            .on('mouseout', function () {
                clearTimeout(that.tooltipTimeout);
                that.tooltipTimeout = window.setTimeout(that.handleMouseOut, 1500);
                d3.select(this).attr('fill-opacity', 1);
            })
            .on('click', (event, d) => d.chordData.events.click({mouseEvent: event, ...d}));
    }

    public update = (): void => {
        this.svg.select('.ribbons').selectAll('path')
            .attr('fill', d => {
                const {selected, color} = d.chordData;
                return selected ? PLOT_COLORS.selectedBarColor : color;
            });
    }
}
