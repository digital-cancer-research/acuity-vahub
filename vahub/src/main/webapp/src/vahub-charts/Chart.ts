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

import {
    Series,
    SeriesItemType,
    SeriesOptions,
    Margins,
    PlotLine,
    ZoneSelection,
    UserOptions,
    PlotAxisConfig,
    ChartTooltip,
    ChartMouseEvent
} from './types/interfaces';
import * as d3 from 'd3';
import {Axis} from './axis';
import {MARGINS, PLOT_COLORS, preformPlotLineData} from './utils/utils';

let global_id = 0;
const MIN_WIDTH = 100;
const MIN_HEIGHT = 50;
const ROTATED_LABELS_TO_CHART_RATIO = 0.4;
const LABELS_TO_CHART_RATIO = 0.1;
export const tooltip_margin = 10;
export const dbClickDelay = 400;

export abstract class Chart {

    public container: HTMLElement;
    public series: Array<Series>;
    public xAxis: Array<Axis>;
    public yAxis: Array<Axis>;
    public title: string;
    public height: number;
    public width: number;
    public svg: d3.Selection<SVGElement>;
    public margins: Margins = MARGINS;
    public isInverted = false;
    public innerSpaceSelectorForExport = 'svg > g';
    public yScale:  d3.scaleType;
    public xScale:  d3.scaleType;
    public limits = {
        x: [0, 0],
        y: [0, 0]
    };
    protected tooltip: ChartTooltip;
    protected plotLines: PlotLine[];
    protected animationTime = 0;
    protected id: string;
    protected click: Function;
    protected yAxisScale:  d3.axisType;
    protected xAxisScale:  d3.axisType;
    protected chartType: string;
    protected selection: Function;
    protected removeSelection: Function;
    protected displayMarkingDialogue: Function;
    protected tooltipContainer: d3.Selection<HTMLDivElement>;
    protected tooltipTimeout: number;
    protected minHeight: number = MIN_HEIGHT;
    protected resizeTimeout: number;
    protected selectionZones: ZoneSelection[] = [];
    protected noDataMessage: string;
    private dbClickTimeout: number = null;

    public constructor(
        options: UserOptions,
    ) {
    }

    public setTitle(title: string): void {
        this.title = title;
    }

    public setNoDataMessage(message: string): void {
        this.noDataMessage = message;
    }

    public destroy(): void {
        d3.selectAll(`.chart-tooltip-${this.id}`).remove();
        d3.select(`#${this.id}`).remove();
    }

    public onZoomX = (borders: number[]): void => {
        this.limits.x = borders;
        this.redraw(this.animateOnZoom);
    }

    public onZoomY = (borders: number[]): void => {
        this.limits.y = borders;
        this.redraw(this.animateOnZoom);
    }

    protected addTooltip(): void {
        if (!this.tooltipContainer) {       // add tooltip if not any
            this.tooltipContainer = d3.select('body')  // add tooltip
                .append('div')
                .attr('class', `chart-tooltip chart-tooltip-${this.id}`)
                .style('pointer-events', 'none')
                .style('opacity', '0')
                .style('border', `1px solid transparent`);
        }
    }

    public setSize (width: number, height: number): void {
        this.calculateSize(height, width);
    }

    protected setAxis(): void {
        this.xAxis = [new Axis(this.onZoomX)];
        this.yAxis = [new Axis(this.onZoomY)];
    }

    private calculateSize(height, width): void {
        const calculatedHeight = height - this.margins.bottom - this.margins.top ;
        if (calculatedHeight - this.margins.bottomWithRotatedLabels < this.minHeight) {
            this.margins.bottom =  (height - this.margins.top) * LABELS_TO_CHART_RATIO;
            this.margins.bottomWithRotatedLabels = (height - this.margins.top) * ROTATED_LABELS_TO_CHART_RATIO;
            this.height = height - this.margins.bottom - this.margins.top;
        } else {
            this.height = calculatedHeight;
        }
        const calculatedWidth = width - this.margins.left - this.margins.right;
        this.width = calculatedWidth > MIN_WIDTH ? calculatedWidth : MIN_WIDTH;
    }

    protected get animateOnZoom(): boolean {
        return Boolean(this.animationTime);
    }

    private setAxisProperties(configList: PlotAxisConfig[], axis): void {
        configList.forEach((config, i) => {
            if (axis[i]) {
                const {categories, type, title} = config;
                if (categories) {
                    axis[i].setCategories(categories);
                }
                if (type) {
                    axis[i].setType(type);
                }
                if (title) {
                    axis[i].setTitle(title);
                }
            }
        });
    }

    protected init(
        userOptions: UserOptions,
    ): void {
        const svg = d3.select(userOptions.chart.renderTo);
        this.series = [];
        this.container = userOptions.chart.renderTo;
        if (userOptions.chart.events) {
            this.click = userOptions.chart.events.click;
            this.selection = userOptions.chart.events.selection;
            this.removeSelection = userOptions.chart.events.removeSelection;
            this.displayMarkingDialogue = userOptions.chart.events.displayMarkingDialogue;
        }
        this.animationTime = userOptions.chart.animationTime || 0;
        this.title = userOptions.title.text;
        this.chartType = userOptions.chart.type;
        this.tooltip = userOptions.tooltip;
        this.calculateSize(userOptions.chart.height, svg.node().offsetWidth);
        this.id = `svg_${userOptions.chart.id || global_id}`;
        this.setAxis();
        if (userOptions.xAxis) {
            this.setAxisProperties(userOptions.xAxis, this.xAxis);
        }
        if (userOptions.yAxis) {
            this.setAxisProperties(userOptions.yAxis, this.yAxis);
        }
        if (userOptions.chart.margins) {
            this.margins = {...this.margins, ...userOptions.chart.margins};
        }
        this.plotLines = userOptions.plotLines || [];
        this.isInverted = userOptions.chart.isInverted || false;
        global_id++;
        if (!userOptions.chart.disableExport) {
            const buttons = userOptions.exporting.buttons;
            svg.html('<div style="display: flex">' +
                '<div class="custom-export"></div>' +
                '<div class="chart-title">' + this.title + '</div>' +
                '</div>')
                .select('.custom-export')
                .html(
                    '<button class="chart-dropdown-toggle" type="button">\n' +
                    '<svg height="24" width="26" style="pointer-events: none">' +
                    '<path fill="#666666" d="M 6 6.5 L 20 6.5 M 6 11.5 L 20 11.5 M 6 16.5 L 20 16.5" ' +
                    'stroke="#666666" stroke-linecap="round" stroke-width="3" style="pointer-events: none">' +
                    '</path></svg></button>')
                .append('ul')
                .attr('id', 'export-list')
                .attr('class', 'chart-dropdown-list')
                .selectAll('li')
                .data(buttons)
                .enter()
                .append('li')
                .text((d) => (d.text))
                .on('click', (e, d) => {
                        const handleClick = d.onclick.bind(this);
                        handleClick();
                    }
                );

            svg.select('.custom-export')
                .style({'margin-left': this.margins.left});

            svg.select('.chart-dropdown-toggle')
                .on('click', () => {
                    if (svg.select('#export-list').style('display') === 'none') {
                        svg.select('#export-list')
                            .style('display', 'block');
                        // handle click outside
                        window.addEventListener('click', function handler(e) {
                            if (typeof (e.target as HTMLTextAreaElement).className !== 'string' ||
                                !(e.target as HTMLTextAreaElement).className.includes('chart-dropdown-toggle')) {
                                svg.select('#export-list')
                                    .style('display', 'none');
                                window.removeEventListener('click', handler);
                            }
                        });
                    } else {
                        svg.select('#export-list')
                            .style('display', 'none');
                    }
                });
        }
        svg.append('svg')
            .attr('id', this.id)
            .attr('width', '100%')
            .attr('height', this.height + this.margins.bottom + this.margins.top);

        this.svg = d3.select(this.container).select(`#${this.id}`);

        window.addEventListener('resize', () => this.handleResize());
        this.svg.on('mousedown', (e) => {
            if (e.target.tagName !== 'text' && e.target.tagName !== 'tspan') {
                e.preventDefault();
            }
        });
    }

    public addSeries(options: SeriesOptions<SeriesItemType>): void {
        this.series = this.initSeries(options);
    }

    public clearSeries(): void {
        this.series = [];
    }

    public getSVG(): string {
        const serializer = new XMLSerializer();
        return serializer.serializeToString(this.svg.node());
    }

    public addPlotLine(line: PlotLine): void {
        this.plotLines.push(line);
    }

    protected drawPlotLines(linesContainer: d3.Selection<SVGElement>, animation = false) {
        const lines = linesContainer
            .selectAll('.plot-line')
            .data(preformPlotLineData(this.plotLines, this));
        lines.exit().remove();

        if (animation) {
            lines
                .transition()
                .duration(this.animationTime)
                .attr('y1', line => line.y1)
                .attr('y2', line => line.y2)
                .attr('x1', line => line.x1)
                .attr('x2', line => line.x2);
        }

        lines.enter()
            .append('line')
            .attr('class', 'plot-line')
            .attr('y1', line => line.y1)
            .attr('y2', line => line.y2)
            .attr('x1', line => line.x1)
            .attr('x2', line => line.x2)
            .attr('stroke-width', line => line.width)
            .attr('stroke', line => line.color)
            .attr('clip-path', this.clipPathURL)
            .attr('style', line => {
                let style = '';
                if (line.styles) {
                    for (const attr of Object.keys(line.styles)) {
                        style += `${attr}: ${line.styles[attr]};`;
                    }
                }
                return style;
            });
    }

    protected handleMouseOver(event: ChartMouseEvent, d, tooltipText: string = null, movementAnimationTime = 0) {
        this.tooltipContainer.html(tooltipText || this.tooltip.formatter.call(d))
            .style('border', `1px solid ${d.color}`)
            .style('opacity', 1)
            .style('pointer-events', 'auto')
            .transition()
            .duration(movementAnimationTime)
            .style('left', event.pageX + tooltip_margin + 'px')
            .style('top', event.pageY + 'px');
    }

    protected handleMouseOut(): void {
        d3.selectAll('.chart-tooltip')
            .style('opacity', '0')
            .style('pointer-events', 'none');
    }

    protected get clipPathId(): string {
        return `clip_${this.id}`;
    }

    protected get clipPathURL(): string {
        return `url(#${this.clipPathId})`;
    }

    protected addClipPath(innerSpace: d3.Selection<SVGElement>, height = this.height, width = this.width): void {
        innerSpace.append('clipPath')
            .attr('id', this.clipPathId)
            .append('rect')
            .attr('x', 0)
            .attr('y', 0)
            .attr('width', width)
            .attr('height', height);
    }

    protected resizeClipPath(height: number): void {
        this.svg.select(`#${this.clipPathId} rect`)
            .attr('height', height);
    }

    protected handleResize(): void {
        clearTimeout(this.resizeTimeout);
        this.resizeTimeout = window.setTimeout(() => {
            const newWidth = this.container.offsetWidth - this.margins.left - this.margins.right;
            this.width = newWidth > MIN_WIDTH ? newWidth : MIN_WIDTH;
            this.resize();
        }, 250);
    }

    public resize(): void {
        this.redraw(false);
        this.svg.attr('height', this.height + this.margins.bottom + this.margins.top);
    }

    protected redrawSelectionZones(
        xScale: d3.ScaleType,
        yScale: d3.ScaleType,
        selectedZonesContainer: d3.Selection<SVGElement> = null,
        animate = false,
        additionalWidth = 0
    ): void {
        if (this.noDataMessage) {
            return;
        }
        const container = selectedZonesContainer || this.svg.select('.selection-zones');
        const zones = container.selectAll('rect')
            .data(this.selectionZones);
        zones.exit().remove();
        zones.enter()
            .append('rect')
            .attr('fill', PLOT_COLORS.selectionZoneColor)
            .attr('x', zone => xScale(zone.xMin))
            .attr('y', zone => yScale(zone.yMax))
            .attr('width', zone => Math.abs(xScale(zone.xMax) - xScale(zone.xMin)) + additionalWidth)
            .attr('height', zone => Math.abs(yScale(zone.yMax) - yScale(zone.yMin)))
            .on('click', (event) => {
                this.displayMarkingDialogue(event);
            });
        zones.transition()
            .duration(animate && this.animationTime)
            .attr('x', zone => xScale(zone.xMin))
            .attr('y', zone => yScale(zone.yMax))
            .attr('width', zone => Math.abs(xScale(zone.xMax) - xScale(zone.xMin)) + additionalWidth)
            .attr('height', zone => Math.abs(yScale(zone.yMax) - yScale(zone.yMin)));
    }

    protected showNoData(): void {
        const message = this.noDataMessage || 'No data to display';
        this.svg.select('g').remove();
        this.svg.append('g').append('text')
            .attr('class', 'no-data')
            .attr('x', this.margins.left + this.width / 2 - this.margins.right)
            .attr('y', this.margins.top + this.height / 2)
            .attr('text-anchor', 'middle')
            .attr('font-size', '24px')
            .attr('color', PLOT_COLORS.lineColor)
            .text(message);
    }

    protected handleClickOnElement(event: ChartMouseEvent, element): void {
        if (this.dbClickTimeout) {
            clearTimeout(this.dbClickTimeout);
            this.dbClickTimeout = null;
            return;
        }
        this.dbClickTimeout = window.setTimeout(() => {
            this.handleSingleClickOnElement(event, element);
            this.dbClickTimeout = null;
        }, dbClickDelay);
    }

    protected handleSingleClickOnElement(event: ChartMouseEvent, element): void {
        throw new Error('Method not implemented');
    }

    protected abstract initSeries(options: SeriesOptions<SeriesItemType>): Series[];
    public abstract redraw(animate?: boolean): void;
}
