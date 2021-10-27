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
import {ITrack, ITrackDataPoint, EcgYAxisValue} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {EcgTrackUtils} from './EcgTrackUtils';
import {AbstractEcgTrackModel} from './AbstractEcgTrackModel';
import {ITrackPlotDetail} from '../ITrackPlotDetail';
import {PlotLine, PlotExtreme} from '../../chart/IChartEvent';
import * as _ from 'lodash';

@Injectable()
export class EcgDetailTrackModel extends AbstractEcgTrackModel {
    private ecgYAxisValue: EcgYAxisValue = EcgYAxisValue.RAW;

    constructor(protected lineChartPlotEventService: LineChartEventService) {
        super(lineChartPlotEventService);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return true;
    }

    setYAxisPlotValue(plotValue: EcgYAxisValue): void {
        this.ecgYAxisValue = plotValue;
    }

    createTrackPlotDetail(subjectId: string, track: ITrack): ITrackPlotDetail[] {
        const plotDetails: ITrackPlotDetail[] = super.createTrackPlotDetail(subjectId, track);

        if (this.ecgYAxisValue === EcgYAxisValue.RAW) {
            plotDetails.forEach(plotDetail => {
                plotDetail.yAxisPlotLines = [];
                //if (plotDetail.subTrackName === 'QTCF') {
                const extremesAndLines = this.createExtremesAndLines(plotDetail.plotData[0], plotDetail.subTrackName);
                plotDetail.extremes = extremesAndLines.extremes;
                plotDetail.yAxisPlotLines = extremesAndLines.plotLines;
                //}
                if (plotDetail.plotData[0].data && (plotDetail.plotData[0].data[0].metadata.baselineValue || plotDetail.plotData[0].data[0].metadata.baselineValue === 0)) {
                    plotDetail.yAxisPlotLines.push(<PlotLine> {
                        value: plotDetail.plotData[0].data[0].metadata.baselineValue,
                        color: EcgTrackUtils.BASELINE_COLOUR
                    });
                }
            });
        }

        return plotDetails;
    }

    protected createPlotConfig(subjectId: string): any {
        const plotConfig: any = {};
        plotConfig.id = {
            subject: subjectId,
            track: EcgTrackUtils.ECG_TRACK_NAME,
            level: EcgTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL
        };

        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.testName;
        event.plotOptions = EcgTrackUtils.assignDetailTrackSymbol(dataPoint.metadata);
        event.metadata = {
            baseline: dataPoint.metadata.baseline,
            sex: dataPoint.metadata.sex,
            abnormality: dataPoint.metadata.abnormality,
            significant: dataPoint.metadata.significant,
            visitNumber: dataPoint.metadata.visitNumber,
            testName: dataPoint.metadata.testName,
            baselineFlag: dataPoint.metadata.baselineFlag,
            baselineValue: dataPoint.metadata.baselineValue,
            unitChangeFromBaseline: dataPoint.metadata.unitChangeFromBaseline,
            unitPercentChangeFromBaseline: dataPoint.metadata.unitPercentChangeFromBaseline,
            unitRaw: dataPoint.metadata.unitRaw,
            valueChangeFromBaseline: dataPoint.metadata.valueChangeFromBaseline,
            valuePercentChangeFromBaseline: dataPoint.metadata.valuePercentChangeFromBaseline,
            valueRaw: dataPoint.metadata.valueRaw,
            value: dataPoint.metadata.value,
            tooltip: this.createTooltip(dataPoint)
        };

        switch (this.ecgYAxisValue) {
            case EcgYAxisValue.RAW:
                return this.transformNoneDurationalDataPointWithRawValue(event);
            case EcgYAxisValue.CHANGE_FROM_BASELINE:
                return this.transformNoneDurationalDataPointWithChangeFromBaseline(event);
            case EcgYAxisValue.PERCENT_CHANGE_FROM_BASELINE:
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

        const baseline = (dataPoint.metadata.baselineValue || dataPoint.metadata.baselineValue === 0) ? (dataPoint.metadata.baselineValue + ' ' + dataPoint.metadata.unitRaw) : 'N/A';
        tooltip += 'Baseline value: <b>' + baseline + '</b><br />';

        const changeFromBaseline = (dataPoint.metadata.valueChangeFromBaseline || dataPoint.metadata.valueChangeFromBaseline === 0) ? (dataPoint.metadata.valueChangeFromBaseline + ' ' + dataPoint.metadata.unitChangeFromBaseline) : 'N/A';
        tooltip += 'Change from baseline: <b>' + changeFromBaseline + '</b><br />';

        const percentChangeFromBaseline = (dataPoint.metadata.valuePercentChangeFromBaseline || dataPoint.metadata.valuePercentChangeFromBaseline === 0) ? (dataPoint.metadata.valuePercentChangeFromBaseline + ' ' + dataPoint.metadata.unitPercentChangeFromBaseline) : 'N/A';
        tooltip += '% change from baseline: <b>' + percentChangeFromBaseline + '</b><br />';

        const raw = dataPoint.metadata.valueRaw ? (dataPoint.metadata.valueRaw + ' ' + dataPoint.metadata.unitRaw) : 'N/A';
        tooltip += 'Raw value: <b>' + raw + '</b>';

        return tooltip;
    }

    private createExtremesAndLines(sereies: any, trackName: string): any {
        const result: any = {};

        const sereiesData: any[] = sereies.data;

        if (sereiesData.length > 0) {
            const lowerRefRange: number = EcgTrackUtils.LOWER_REFERENCE_LINE;
            const middleRefRange: number = EcgTrackUtils.MIDDLE_REFERENCE_LINE;
            const higherRefRange: number = EcgTrackUtils.UPPER_REFERENCE_LINE;

            const minLabEvent: any = _.minBy(sereiesData, (event) => {
                return event.metadata.value;
            });

            const maxLabEvent: any = _.maxBy(sereiesData, (event) => {
                return event.metadata.value;
            });

            let chartMin, chartMax: number;
            if (trackName.toUpperCase().indexOf('QTCF') !== -1) {
                chartMin = lowerRefRange - (higherRefRange - lowerRefRange) * 0.5;
                chartMax = higherRefRange + (higherRefRange - lowerRefRange) * 0.5;
            } else {
                chartMin = minLabEvent.metadata.value;
                chartMax = maxLabEvent.metadata.value;
            }

            if (sereiesData[0].metadata.baselineValue) {
                if (sereiesData[0].metadata.baselineValue < chartMin) {
                    const minDiff = Math.abs(sereiesData[0].metadata.baselineValue - chartMin);
                    chartMin -= minDiff;
                    chartMax += minDiff;
                }
                if (sereiesData[0].metadata.baselineValue > chartMax) {
                    const maxDiff = Math.abs(sereiesData[0].metadata.baselineValue - chartMax);
                    chartMin -= maxDiff;
                    chartMax += maxDiff;
                }
            }

            if (minLabEvent.metadata.value < chartMin) {
                const minDiff = Math.abs(minLabEvent.metadata.value - chartMin);
                chartMin -= minDiff;
                chartMax += minDiff;
            }

            if (maxLabEvent.metadata.value > chartMax) {
                const maxDiff = Math.abs(maxLabEvent.metadata.value - chartMax);
                chartMin -= maxDiff;
                chartMax += maxDiff;
            }

            result.extremes = <PlotExtreme>{
                min: chartMin,
                max: chartMax
            };
            result.plotLines = [];
            if (trackName.toUpperCase().indexOf('QTCF') !== -1) {
                result.plotLines.push(<PlotLine> {
                    value: lowerRefRange,
                    color: EcgTrackUtils.REFERENCE_LINE_COLOUR
                });
                result.plotLines.push(<PlotLine> {
                    value: middleRefRange,
                    color: EcgTrackUtils.REFERENCE_LINE_COLOUR
                });
                result.plotLines.push(<PlotLine> {
                    value: higherRefRange,
                    color: EcgTrackUtils.REFERENCE_LINE_COLOUR
                });
            }
            if (sereiesData[0].metadata.baselineValue) {
                result.plotLines.push(<PlotLine> {
                    value: sereiesData[0].metadata.baselineValue,
                    color: EcgTrackUtils.BASELINE_COLOUR
                });
            }
        }
        return result;
    }
}
