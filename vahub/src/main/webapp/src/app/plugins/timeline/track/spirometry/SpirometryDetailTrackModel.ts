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

import { Injectable } from '@angular/core';
import {ITrack, ITrackDataPoint, SpirometryYAxisValue} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {SpirometryTrackUtils} from './SpirometryTrackUtils';
import {AbstractSpirometryTrackModel} from './AbstractSpirometryTrackModel';
import {ITrackPlotDetail} from '../ITrackPlotDetail';
import {PlotLine, PlotExtreme} from '../../chart/IChartEvent';
import * as  _ from 'lodash';

@Injectable()
export class SpirometryDetailTrackModel extends AbstractSpirometryTrackModel {

    private spirometryYAxisValue: SpirometryYAxisValue = SpirometryYAxisValue.RAW;

    constructor(protected lineChartPlotEventService: LineChartEventService) {
        super(lineChartPlotEventService);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return true;
    }

    setYAxisPlotValue(plotValue: SpirometryYAxisValue): void {
        this.spirometryYAxisValue = plotValue;
    }

    createTrackPlotDetail(subjectId: string, track: ITrack): ITrackPlotDetail[] {
        const plotDetails: ITrackPlotDetail[] = super.createTrackPlotDetail(subjectId, track);

        if (this.spirometryYAxisValue === SpirometryYAxisValue.RAW) {
            plotDetails.forEach(plotDetail => {
                const extremesAndLines = this.createExtremesAndLines(plotDetail.plotData[0]);
                plotDetail.extremes = extremesAndLines.extremes;
                plotDetail.yAxisPlotLines = extremesAndLines.plotLines;
            });
        }

        return plotDetails;
    }

    protected createPlotConfig(subjectId: string): any {
        const plotConfig: any = {};
        plotConfig.id = {
            subject: subjectId,
            track: SpirometryTrackUtils.SPIROMETRY_TRACK_NAME,
            level: SpirometryTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL
        };

        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.code;
        event.plotOptions = {};
        event.metadata = {
            category: dataPoint.metadata.category,
            code: dataPoint.metadata.code,
            baseline: dataPoint.metadata.baseline,
            sex: dataPoint.metadata.sex,
            visitNumber: dataPoint.metadata.visitNumber,
            baselineValue: dataPoint.metadata.baselineValue,
            baselineFlag: dataPoint.metadata.baselineFlag,
            value: dataPoint.metadata.value,
            valueRaw: dataPoint.metadata.valueRaw,
            unit: dataPoint.metadata.unit,
            valueChangeFromBaseline: dataPoint.metadata.valueChangeFromBaseline,
            valuePercentChangeFromBaseline: dataPoint.metadata.valuePercentChangeFromBaseline,
            unitPercentChangeFromBaseline: dataPoint.metadata.unitPercentChangeFromBaseline,
            unitChangeFromBaseline: dataPoint.metadata.unitChangeFromBaseline,
            tooltip: this.createTooltip(dataPoint)
        };

        switch (this.spirometryYAxisValue) {
            case SpirometryYAxisValue.RAW:
                event.metadata.value = event.metadata.valueRaw;
                break;
            case SpirometryYAxisValue.CHANGE_FROM_BASELINE:
                event.metadata.value = event.metadata.valueChangeFromBaseline;
                break;
            case SpirometryYAxisValue.PERCENT_CHANGE_FROM_BASELINE:
                event.metadata.value = event.metadata.valuePercentChangeFromBaseline;
                break;
            default:
                break;
        }

        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = super.createTooltip(dataPoint);
        // test name
        tooltip += 'Lung function code: <b>' + dataPoint.metadata.code + '</b><br/>';
        tooltip += 'Baseline value: <b>' + (!_.isNull(dataPoint.metadata.baselineValue) ? (dataPoint.metadata.baselineValue + ' ' + dataPoint.metadata.unit) : 'N/A') + '</b><br />';
        const changeFromBaseline = !_.isNull(dataPoint.metadata.valueChangeFromBaseline) ? (dataPoint.metadata.valueChangeFromBaseline + ' ' + dataPoint.metadata.unitChangeFromBaseline) : 'N/A';
        tooltip += 'Change from baseline: <b>' + changeFromBaseline + '</b><br />';
        const percentChangeFromBaseline = !_.isNull(dataPoint.metadata.valuePercentChangeFromBaseline) ? (dataPoint.metadata.valuePercentChangeFromBaseline + ' ' + dataPoint.metadata.unitPercentChangeFromBaseline) : 'N/A';
        tooltip += '% change from baseline: <b>' + percentChangeFromBaseline + '</b><br />';
        const raw = !_.isNull(dataPoint.metadata.value) ? (dataPoint.metadata.valueRaw + ' ' + dataPoint.metadata.unit) : 'N/A';
        tooltip += 'Raw value: <b>' + raw + '</b>';
        return tooltip;
    }

    private createExtremesAndLines(sereies: any): any {
        const result: any = {};

        const sereiesData: any[] = sereies.data;

        if (sereiesData.length > 0) {
            const lowerRefRange: number = sereiesData[0].metadata.referenceRangeLowerLimit;
            const higherRefRange: number = sereiesData[0].metadata.referenceRangeHigherLimit;

            const minSpirometryEvent: any = _.minBy(sereiesData, (event) => {
                return event.metadata.value;
            });

            const maxSpirometryEvent: any = _.maxBy(sereiesData, (event) => {
                return event.metadata.value;
            });

            const baseLineValue = sereiesData[0].metadata.baselineValue;
            let chartMin: number = baseLineValue ? baseLineValue : 0;
            let chartMax: number = baseLineValue ? baseLineValue : 0;

            if (minSpirometryEvent.metadata.value < chartMin) {
                const minDiff = Math.abs(minSpirometryEvent.metadata.value - chartMin);
                chartMin -= minDiff;
                chartMax += minDiff;
            }

            if (maxSpirometryEvent.metadata.value > chartMax) {
                const maxDiff = Math.abs(maxSpirometryEvent.metadata.value - chartMax);
                chartMin -= maxDiff;
                chartMax += maxDiff;
            }

            result.extremes = <PlotExtreme>{
                min: chartMin,
                max: chartMax
            };

            result.plotLines = [];
            if (baseLineValue || baseLineValue === 0) {
                result.plotLines.push(<PlotLine>{
                    value: baseLineValue,
                    color: SpirometryTrackUtils.REFERENCE_LINE_COLOUR
                });
            }
        }
        return result;
    }
}
