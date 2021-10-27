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
import {ITrack, ITrackDataPoint, LabsYAxisValue} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {LabsTrackUtils} from './LabsTrackUtils';
import {AbstractLabsTrackModel} from './AbstractLabsTrackModel';
import {ITrackPlotDetail} from '../ITrackPlotDetail';
import {PlotLine, PlotExtreme} from '../../chart/IChartEvent';
import * as  _ from 'lodash';

@Injectable()
export class LabsDetailWithThresholdTrackModel extends AbstractLabsTrackModel {

    private labsYAxisValue: LabsYAxisValue = LabsYAxisValue.RAW;

    constructor(protected lineChartPlotEventService: LineChartEventService) {
        super(lineChartPlotEventService);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return true;
    }

    setYAxisPlotValue(plotValue: LabsYAxisValue): void {
        this.labsYAxisValue = plotValue;
    }

    createTrackPlotDetail(subjectId: string, track: ITrack): ITrackPlotDetail[] {
        const plotDetails: ITrackPlotDetail[] = super.createTrackPlotDetail(subjectId, track);

        if (this.labsYAxisValue === LabsYAxisValue.RAW) {
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
            track: LabsTrackUtils.LABS_TRACK_NAME,
            level: LabsTrackUtils.DETAIL_WITH_THRESHOLD_SUB_TRACK_EXPANSION_LEVEL
        };

        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.code;
        event.plotOptions = {};
        if (dataPoint.metadata.baselineFlag) {
            event.plotOptions = {
                markerSymbol: 'star',
                radius: 7
            };
        }
        event.metadata = {
            code: dataPoint.metadata.code,
            visitNumber: dataPoint.metadata.code,
            baselineValue: dataPoint.metadata.baselineValue,
            baselineFlag: dataPoint.metadata.baselineFlag,
            valueRaw: dataPoint.metadata.valueRaw,
            unit: dataPoint.metadata.unit,
            valueChangeFromBaseline: dataPoint.metadata.valueChangeFromBaseline,
            unitChangeFromBaseline: dataPoint.metadata.unitChangeFromBaseline,
            valuePercentChangeFromBaseline: dataPoint.metadata.valuePercentChangeFromBaseline,
            unitPercentChangeFromBaseline: dataPoint.metadata.unitPercentChangeFromBaseline,
            referenceRangeHigherLimit: dataPoint.metadata.referenceRangeHigherLimit,
            referenceRangeLowerLimit: dataPoint.metadata.referenceRangeLowerLimit,
            numAboveReferenceRange: dataPoint.metadata.numAboveReferenceRange,
            numBelowReferenceRange: dataPoint.metadata.numBelowReferenceRange,
            numAboveSeverityThreshold: dataPoint.metadata.numAboveSeverityThreshold,
            numBelowSeverityThreshold: dataPoint.metadata.numBelowSeverityThreshold,
            tooltip: this.createTooltip(dataPoint)
        };
        // event.metadata.tooltip = this.createTooltip(dataPoint);

        switch (this.labsYAxisValue) {
            case LabsYAxisValue.RAW:
                return this.transformNoneDurationalDataPointWithRawValue(event);
            case LabsYAxisValue.CHANGE_FROM_BASELINE:
                return this.transformNoneDurationalDataPointWithChangeFromBaseline(event);
            case LabsYAxisValue.PERCENT_CHANGE_FROM_BASELINE:
                return this.transformNoneDurationalDataPointWithPercentChangeFromBaseline(event);
            case LabsYAxisValue.REF_RANGE_NORM:
                return this.transformNoneDurationalDataPointWithRefRangeNorm(event);
            case LabsYAxisValue.TIMES_UPPER_REF:
                return this.transformNoneDurationalDataPointWithTimeUpperRef(event);
            case LabsYAxisValue.TIMES_LOWER_REF:
                return this.transformNoneDurationalDataPointWithTimeLowerRef(event);
            default:
                return event;
        }
    }

    private transformNoneDurationalDataPointWithRawValue(event: RawEvent): RawEvent {
        event.metadata.value = event.metadata.valueRaw;
        event.metadata.color = LabsTrackUtils.assignColour(event.metadata.value,
            event.metadata.referenceRangeLowerLimit,
            event.metadata.referenceRangeHigherLimit);
        return event;
    }

    private transformNoneDurationalDataPointWithChangeFromBaseline(event: RawEvent): RawEvent {
        event.metadata.value = event.metadata.valueChangeFromBaseline;
        event.metadata.color = null;
        return event;
    }

    private transformNoneDurationalDataPointWithPercentChangeFromBaseline(event: RawEvent): RawEvent {
        event.metadata.value = event.metadata.valuePercentChangeFromBaseline;
        event.metadata.color = null;
        return event;
    }

    private transformNoneDurationalDataPointWithRefRangeNorm(event: RawEvent): RawEvent {
        event.metadata.value = event.metadata.referenceRangeHigherLimit - event.metadata.referenceRangeLowerLimit === 0 ?
            null : (event.metadata.valueRaw - event.metadata.referenceRangeLowerLimit)
        / (event.metadata.referenceRangeHigherLimit - event.metadata.referenceRangeLowerLimit );
        event.metadata.baselineFlag = false;
        event.metadata.color = null;
        return event;
    }

    private transformNoneDurationalDataPointWithTimeUpperRef(event: RawEvent): RawEvent {
        event.metadata.value = event.metadata.referenceRangeHigherLimit === 0 ?
            null : event.metadata.valueRaw / event.metadata.referenceRangeHigherLimit;
        event.metadata.baselineFlag = false;
        event.metadata.color = null;
        return event;
    }

    private transformNoneDurationalDataPointWithTimeLowerRef(event: RawEvent): RawEvent {
        event.metadata.value = event.metadata.referenceRangeLowerLimit === 0 ?
            null : event.metadata.valueRaw / event.metadata.referenceRangeLowerLimit;
        event.metadata.baselineFlag = false;
        event.metadata.color = null;
        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        // lab code
        let tooltip = 'Labcode: <b>' + dataPoint.metadata.code + '</b>';
        // event
        tooltip += '<br />Event: <b> Visit</b>';
        // visit number
        tooltip += '<br />Visit #: <b>' + dataPoint.metadata.visitNumber + '</b>';
        // Result value
        tooltip += '<br />Raw value: <b> '
            + this.roundNumberToTwoDecimal(dataPoint.metadata.valueRaw) + ' '
            + (dataPoint.metadata.unit || '') + '</b>';
        // baseline flag
        tooltip += '<br />Baseline flag: <b> '
            + dataPoint.metadata.baselineFlag + '</b>';
        // baseline
        tooltip += '<br />Baseline value: <b> '
            + this.roundNumberToTwoDecimal(dataPoint.metadata.baselineValue) + ' '
            + (dataPoint.metadata.unit || '') + '</b>';
        // change from baseline
        const changeFromBaseline = !_.isNull(dataPoint.metadata.valueChangeFromBaseline)
            ? (this.roundNumberToTwoDecimal(dataPoint.metadata.valueChangeFromBaseline) + ' '
               + dataPoint.metadata.unitChangeFromBaseline)
            : 'N/A';
        tooltip += '<br />Change from baseline: <b> ' + changeFromBaseline + '</b>';
        // percent change from baseline
        const percentChangeFromBaseline = !_.isNull(dataPoint.metadata.valuePercentChangeFromBaseline)
            ? (dataPoint.metadata.valuePercentChangeFromBaseline + ' '
               + dataPoint.metadata.unitPercentChangeFromBaseline)
            : 'N/A';
        tooltip += '<br />% change from baseline: <b> ' + percentChangeFromBaseline + '</b>';

        return tooltip;
    }

    private roundNumberToTwoDecimal(num: number): number {
        return Math.round(num * 100) / 100;
    }

    private createExtremesAndLines(sereies: any): any {
        const result: any = {};
        const sereiesData: any[] = sereies.data;

        if (sereiesData.length > 0) {
            const lowerRefRange: number = sereiesData[0].metadata.referenceRangeLowerLimit;
            const higherRefRange: number = sereiesData[0].metadata.referenceRangeHigherLimit;

            const minLabEventValue: any = _.minBy(sereiesData, event => event.metadata.value).metadata.value;
            const maxLabEventValue: any = _.maxBy(sereiesData, event => event.metadata.value).metadata.value;

            const defaultDiff: number = _.mean([lowerRefRange, higherRefRange, minLabEventValue, maxLabEventValue]) * 0.5;
            const chartMax: number = Math.max(higherRefRange, maxLabEventValue) + defaultDiff;
            const chartMin: number = Math.min(lowerRefRange, minLabEventValue) - defaultDiff;

            result.extremes = <PlotExtreme>{
                min: chartMin,
                max: chartMax
            };

            result.plotLines = [];

            if (lowerRefRange || lowerRefRange === 0) {
                result.plotLines.push(<PlotLine>{
                    value: lowerRefRange,
                    color: LabsTrackUtils.OUT_OF_REFERENCE_RANGE_COLOUR
                });
            }

            if (higherRefRange || higherRefRange === 0) {
                result.plotLines.push(<PlotLine>{
                    value: higherRefRange,
                    color: LabsTrackUtils.OUT_OF_REFERENCE_RANGE_COLOUR
                });
            }
        }

        return result;
    }
}
