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
import {getScaledCoordinate, getSelectionRequest, MARGINS, PLOT_COLORS} from '../utils/utils';
import {Chart} from '../Chart';
import {
    ChartMouseEvent,
    ErrorBarData,
    RangePlotPoint,
    Series,
    SimpleLinePlotData,
    UserOptions
} from '../types/interfaces';

export const TOOLTIP_MARGIN = 15;
export const CIRCLE_DIAMETERS = {
    active: 6,
    inactive: 4
};
export const bottomWithRotatedLabels = 30;
export const leftWithAdditionalLabels = 160;
export const MICROANIMATION_TIME = 0.25;
export const TOOLTIP_BOTTOM_MARGIN = 200;

export abstract class AbstractLineChart extends Chart {
    public margins = {...MARGINS, bottomWithRotatedLabels, leftWithAdditionalLabels};
    public activePoint: RangePlotPoint & SimpleLinePlotData;
    public markerSymbol: string;

    public constructor(
        options: UserOptions,
    ) {
        super(options);
        this.init(options);
    }

    public getXCoordinate = (point: RangePlotPoint | SimpleLinePlotData | ErrorBarData): number => {
        return getScaledCoordinate(this.xAxis[0], this.xScale, this.limits.x, this.width, point.x, point.category);
    }

    public getYCoordinate = (y: number): number => {
        return getScaledCoordinate(this.yAxis[0], this.yScale, this.limits.y, this.height, y, null, true);
    }

    public handleMouseMovement (event: ChartMouseEvent, calculateByXAxisOnly = false) {
        const layerX = event.layerX - this.margins.left;
        const layerY = event.layerY - this.margins.top;
        let minimumCoordinateDistance = Infinity;
        let newActivePoint;

        this.series.forEach((serie: Series) => {
            serie.data.forEach(point => {
                const px = this.getXCoordinate(point);
                const py = this.yScale(point.y);
                const pointCoordinateDistance = calculateByXAxisOnly
                    ? Math.abs(px - layerX)
                    : Math.sqrt((px - layerX) ** 2 + (py - layerY) ** 2);
                if (pointCoordinateDistance < minimumCoordinateDistance) {
                    minimumCoordinateDistance = pointCoordinateDistance;
                    newActivePoint = point;
                }
            });
        });

        if (newActivePoint !== this.activePoint) {
            if (this.activePoint) {
                this.activePoint.setIsActive(false);
            }
            clearTimeout(this.tooltipTimeout);
            this.activePoint = newActivePoint;
            this.activePoint.setIsActive(true);
            this.tooltipTimeout = window.setTimeout(() => {
                this.callTooltip();
            }, 500, event);
            this.update();
        }
    }

    public clearActivePoint (): void {
        clearTimeout(this.tooltipTimeout);
        if (!this.activePoint) {
            return;
        }
        this.activePoint.setIsActive(false);
        this.activePoint = null;
        this.update();
        this.tooltipContainer.transition().duration(250)
            .style('opacity', 0);
    }

    public drawMarkers (container: d3.Selection<HTMLDivElement>, serie: Series, animate = false, index = 0): void {
        if (animate) {
            this.updateMarker(serie, index);
            return;
        }
        const dots = container
            .selectAll('.marker')
            .data(serie.data);
        this.enter(dots, index);
    }

    private getLinePath = (): string => {
        return d3.line()
            .x((d) => this.getXCoordinate(d))
            .y((d) => this.getYCoordinate(d.y));
    }

    public drawLine (container: d3.Selection<HTMLDivElement>, serie: Series, animate = false, index = 0) {
        if (!animate) {
            container
                .append('path')
                .attr('class', `line-${index}`)
                .datum(serie.data)
                .attr('fill', 'none')
                .attr('stroke', serie.color)
                .attr('stroke-width', 1);
        }
        this.svg.select(`.line-${index}`).datum(serie.data)
            .transition()
            .duration(animate && this.animationTime)
            .attr('d', this.getLinePath());
    }

    public drawLineWithPoints(container: d3.Selection<HTMLDivElement>, serie: Series, animate = false, index = 0): void {
        const lineContainer = animate ? null
            : container.append('g').attr('class', `line-with-points line-with-points-${serie.lineNumber || index}`);
        this.drawLine(lineContainer, serie, animate, index);
        this.drawMarkers(lineContainer, serie, animate, index);
    }

    private findActiveMarkerPosition() {
        let activePoint = null;
        const position = {x: window.scrollX, y: window.scrollY};
        d3.selectAll('.marker').each(function (d) {
            if (d && d.isActive) {
                activePoint = this;
            }
        });
        if (activePoint) {
            const activePointPosition = activePoint.getClientRects()[0];
            position.x += activePointPosition.x;
            position.y += activePointPosition.y;
        }
        return position;
    }

    public callTooltip () {
        const { x, y } = this.findActiveMarkerPosition();
        const maxY = window.scrollY + window.innerHeight - TOOLTIP_BOTTOM_MARGIN;
        const actualY = y > maxY ? maxY : y;
        const tooltipText = this.tooltip.formatter.call(this.activePoint);
        const borderColor = this.activePoint.marker ? this.activePoint.marker.lineColor : this.activePoint.color;
        this.tooltipContainer.html(tooltipText)
            .transition().duration(1000 * MICROANIMATION_TIME)
            .style('left', x + TOOLTIP_MARGIN + 'px')
            .style('top', actualY + TOOLTIP_MARGIN + 'px')
            .style('pointer-events', 'none')
            .style('background-color', PLOT_COLORS.tooltipTransparentBackground)
            .style('border-color', borderColor)
            .transition().duration(500)
            .style('opacity', 1);
    }

    public update = (): void => {};

    public updateMarker = (serie: Series, index: number): void => {
        this.svg.selectAll(`.markers-${index}`).data(serie.data)
            .transition()
            .duration(this.animationTime)
            .attr('cx', d => this.getXCoordinate(d))
            .attr('cy', d => this.getYCoordinate(d.y));
    }

    protected handleSingleClickOnElement(event, d) {
        if (!this.selection) {
            return;
        }
        const selectionCords = {
            xAxis: [
                {
                    value: d.x,
                    min: d.x,
                    max: d.x
                }
            ],
            yAxis: [
                {
                    value: d.y,
                    min: d.y,
                    max: d.y
                }
            ]
        };
        this.selection(getSelectionRequest(event, selectionCords, event.offsetX, event.offsetY));
    }

    protected abstract enter(source, index?): void;
}
