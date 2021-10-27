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

import {Injectable} from '@angular/core';
import {ITrack, ITrackDataPoint, VitalsYAxisValue} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {VitalsTrackUtils} from './VitalsTrackUtils';
import {AbstractVitalsTrackModel} from './AbstractVitalsTrackModel';
import {ITrackPlotDetail} from '../ITrackPlotDetail';
import {PlotLine, PlotExtreme} from '../../chart/IChartEvent';
import * as  _ from 'lodash';

@Injectable()
export class VitalsDetailTrackModel extends AbstractVitalsTrackModel {
    private vitalsYAxisValue: VitalsYAxisValue = VitalsYAxisValue.RAW;

    constructor(protected lineChartPlotEventService: LineChartEventService) {
        super(lineChartPlotEventService);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return true;
    }

    setYAxisPlotValue(plotValue: VitalsYAxisValue): void {
        this.vitalsYAxisValue = plotValue;
    }

    createTrackPlotDetail(subjectId: string, track: ITrack): ITrackPlotDetail[] {
        const plotDetails: ITrackPlotDetail[] = super.createTrackPlotDetail(subjectId, track);

        if (this.vitalsYAxisValue === VitalsYAxisValue.RAW) {
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
            track: VitalsTrackUtils.VITALS_TRACK_NAME,
            level: VitalsTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL
        };

        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.testName;
        event.plotOptions = {};
        if (dataPoint.metadata.baselineFlag) {
            event.plotOptions = {
                markerSymbol: 'star',
                radius: 7
            };
        }
        event.metadata = {
            category: dataPoint.metadata.category,
            baseline: dataPoint.metadata.baseline,
            sex: dataPoint.metadata.sex,
            visitNumber: dataPoint.metadata.visitNumber,
            testName: dataPoint.metadata.testName,
            baselineValue: dataPoint.metadata.baselineValue,
            baselineFlag: dataPoint.metadata.baselineFlag,
            unitChangeFromBaseline: dataPoint.metadata.unitChangeFromBaseline,
            unitPercentChangeFromBaseline: dataPoint.metadata.unitPercentChangeFromBaseline,
            unitRaw: dataPoint.metadata.unitRaw,
            valueChangeFromBaseline: dataPoint.metadata.valueChangeFromBaseline,
            valuePercentChangeFromBaseline: dataPoint.metadata.valuePercentChangeFromBaseline,
            valueRaw: dataPoint.metadata.valueRaw,
            value: dataPoint.metadata.value,
            tooltip: this.createTooltip(dataPoint)
        };

        switch (this.vitalsYAxisValue) {
            case VitalsYAxisValue.RAW:
                return this.transformNoneDurationalDataPointWithRawValue(event);
            case VitalsYAxisValue.CHANGE_FROM_BASELINE:
                return this.transformNoneDurationalDataPointWithChangeFromBaseline(event);
            case VitalsYAxisValue.PERCENT_CHANGE_FROM_BASELINE:
                return this.transformNoneDurationalDataPointWithPercentChangeFromBaseline(event);
            default:
                return event;
        }
    }

    private transformNoneDurationalDataPointWithRawValue(event: RawEvent): RawEvent {
        event.metadata.value = event.metadata.valueRaw;
        return event;
    }

    private transformNoneDurationalDataPointWithChangeFromBaseline(event: RawEvent): RawEvent {
        event.metadata.value = event.metadata.valueChangeFromBaseline;
        return event;
    }

    private transformNoneDurationalDataPointWithPercentChangeFromBaseline(event: RawEvent): RawEvent {
        event.metadata.value = event.metadata.valuePercentChangeFromBaseline;
        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = super.createTooltip(dataPoint);
        // test name
        tooltip += 'Test name: <b>' + dataPoint.metadata.testName + '</b><br/>';
        tooltip += 'Baseline value: <b>'
            + (!_.isNull(dataPoint.metadata.baselineValue)
               ? (dataPoint.metadata.baselineValue + ' ' + dataPoint.metadata.unitRaw)
               : 'N/A')
            + '</b><br />';
        const changeFromBaseline = !_.isNull(dataPoint.metadata.valueChangeFromBaseline)
            ? (dataPoint.metadata.valueChangeFromBaseline + ' ' + dataPoint.metadata.unitChangeFromBaseline)
            : 'N/A';
        tooltip += 'Change from baseline: <b>' + changeFromBaseline + '</b><br />';
        const percentChangeFromBaseline = !_.isNull(dataPoint.metadata.valuePercentChangeFromBaseline)
            ? (dataPoint.metadata.valuePercentChangeFromBaseline + ' '
               + dataPoint.metadata.unitPercentChangeFromBaseline)
            : 'N/A';
        tooltip += '% change from baseline: <b>' + percentChangeFromBaseline + '</b><br />';
        const raw = !_.isNull(dataPoint.metadata.valueRaw)
            ? (dataPoint.metadata.valueRaw + ' ' + dataPoint.metadata.unitRaw)
            : 'N/A';
        tooltip += 'Raw value: <b>' + raw + '</b>';
        return tooltip;
    }

    private createExtremesAndLines(sereies: any): any {
        const result: any = {};

        const sereiesData: any[] = sereies.data;

        if (sereiesData.length > 0) {

            const minVitalEvent: any = _.minBy(sereiesData, (event) => {
                return event.metadata.valueRaw;
            });

            const maxVitalEvent: any = _.maxBy(sereiesData, (event) => {
                return event.metadata.valueRaw;
            });

            const baseLineValue = sereiesData[0].metadata.baselineValue;
            let chartMin: number = baseLineValue ? baseLineValue : 0;
            let chartMax: number = baseLineValue ? baseLineValue : 0;

            if (minVitalEvent.metadata.value < chartMin) {
                const minDiff = Math.abs(minVitalEvent.metadata.valueRaw - chartMin);
                chartMin -= minDiff;
                chartMax += minDiff;
            }

            if (maxVitalEvent.metadata.value > chartMax) {
                const maxDiff = Math.abs(maxVitalEvent.metadata.valueRaw - chartMax);
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
                    color: VitalsTrackUtils.REFERENCE_LINE_COLOUR
                });
            }
        }

        return result;
    }
}
