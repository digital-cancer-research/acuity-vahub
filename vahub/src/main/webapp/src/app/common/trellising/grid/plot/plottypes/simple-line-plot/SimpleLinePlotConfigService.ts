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

import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {IPlotConfigService} from '../../IPlotConfigService';
import {AggregationType, PlotSettings, ScaleTypes, TabId} from '../../../../store';
import {SimpleLinePlotService} from './SimpleLinePlotService';
import {SentenceCasePipe} from '../../../../../pipes';
import {StudyService} from '../../../../../StudyService';
import {ExportChartService} from '../../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../../configuration/ConfigurationService';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class SimpleLinePlotConfigService extends AbstractPlotConfigService implements IPlotConfigService {
    private static readonly NO_MUTATIONS_LINE = 0.2;
    private noMutationScaleTick: number;
    constructor(private sentenceCasePipe: SentenceCasePipe,
                private studyService: StudyService,
                private exportChartsService: ExportChartService,
                private configService: ConfigurationService) {
        super(exportChartsService, configService);
    }

    createPlotConfig(title: string, height: number, selectedSubject?: string): UserOptions {
        const customConfig: CustomPlotConfig = {
            chart: {
                type: 'simple-line',
                animationTime: 500
            }
        };
        const formattedTitle = selectedSubject ? `${selectedSubject.split(' ')[0]}: ${title}` : title;
        return super.createDefaultPlotConfig(customConfig, height, formattedTitle);
    }

    additionalOptions(tabId: TabId, globalYAxisLabel: string, globalXAxisLabel: string,
                      plotSettings: PlotSettings, scaleType?: ScaleTypes): CustomPlotConfig {
        const that = this;
        switch (tabId) {
            case TabId.ANALYTE_CONCENTRATION:
                return {
                    tooltip: {
                        formatter: function (): string {
                            return that.buildExposureTooltip(plotSettings, this.point);
                        }
                    }
                };
            case TabId.CTDNA_PLOT:
                const noMutationsLineValue = SimpleLinePlotService.hasFraction(globalYAxisLabel)
                    ? SimpleLinePlotConfigService.NO_MUTATIONS_LINE  / 100 : SimpleLinePlotConfigService.NO_MUTATIONS_LINE;
                this.noMutationScaleTick = scaleType === ScaleTypes.LOGARITHMIC_SCALE ? Math.log10(noMutationsLineValue)
                    : noMutationsLineValue;
                return {
                    plotLines: [{
                        width: 1,
                        color: 'black',
                        value: noMutationsLineValue,
                        formatLabel: (value) => `No mutation detected ${value}`,
                        axis: 'y',
                        styles: {
                            'stroke-dasharray': '4, 3',
                            fill: 'none'
                        }
                    }],
                    tooltip: {
                        formatter: function (): string {
                            const name = this.name;
                            const yValueWithUnit = globalYAxisLabel.indexOf('percentage') > -1
                                ? name.vafPercent
                                : name.vaf;
                            return `
                                <div>
                                    <span>Subject ID: ${name.subjectCode}</span><br/>
                                    <span>Gene: ${name.gene}</span><br/>
                                    <span>Mutation: ${name.mutation}</span><br/>
                                    <span>${globalYAxisLabel}: ${yValueWithUnit}</span><br/>
                                    <span>${globalXAxisLabel}: ${this.category || this.x}</span><br/>
                                </div>
                            `;
                        }
                    }
                };
            case TabId.TL_DIAMETERS_PLOT:
            case TabId.TL_DIAMETERS_PER_SUBJECT_PLOT:
                return {
                    chart: {
                        animationTime: tabId === TabId.TL_DIAMETERS_PER_SUBJECT_PLOT ? 0 : 500
                    },
                    plotOptions: {
                        markerSymbol: 'diamond'
                    },
                    tooltip: {
                        formatter: function (): string {
                            return `
                                <div>
                                    <span>Subject Number: ${this.series.name}</span>
                                    <br/>
                                    <span>${globalXAxisLabel}: ${this.category || this.x}</span>
                                    <br/>
                                    <span>${globalYAxisLabel}: ${this.y}</span>
                                    <br/>
                                    <span>Overall visit response: ${this.point.name}</span>
                                </div>
                            `;
                        }
                    }
                };
            default:
                return {};
        }
    }

    private buildExposureTooltip(plotSettings: PlotSettings, point: any) {

        const aggregationType = plotSettings.get('trellisedBy');
        const aggregationName = this.sentenceCasePipe.transform(aggregationType);
        const exposureData = point.name.exposureData;
        // TODO: think of nicer way of handling tooltip data
        let result = `<div>`;
        if (aggregationType === AggregationType.SUBJECT_CYCLE || aggregationType === AggregationType.SUBJECT) {
            result += this.buildTooltipLine('Subject ID', exposureData.subject);
        }
        if (aggregationType !== AggregationType.SUBJECT_CYCLE) {
            result += this.buildTooltipLine('Amean analyte concentration',
                //round to 2 digits then make fraction = 2
                this.round(parseFloat(point.y), 2));
            result += this.buildTooltipLine('Number of data points', point.name.dataPoints);
        } else {
            result += this.buildTooltipLine('Analyte concentration',
                this.round(parseFloat(point.y), 2));
        }
        if (SimpleLinePlotService.shouldErrorBarsBeShown(plotSettings)) {
            result += this.buildTooltipLine('Standard deviation',
                this.round(point.standardDeviation, 2));
        }
        if (aggregationType === AggregationType.DOSE_PER_CYCLE) {
            result += this.buildTooltipLine(aggregationName, `${exposureData.dose}, ${exposureData.treatmentCycle}`, false, false);
        } else {
            result += this.buildTooltipLine('Cycle', exposureData.treatmentCycle, false, false);
        }
        if (aggregationType === AggregationType.DOSE) {
            result += this.buildTooltipLine(aggregationName, exposureData.dose, true, false);
        }
        if (aggregationType === AggregationType.VISIT) {
            result += this.buildTooltipLine(aggregationName, exposureData.visit, true, false);
        }
        if (aggregationType === AggregationType.DOSE_PER_VISIT) {
            result += this.buildTooltipLine(aggregationName, `${exposureData.dose}, visit ${exposureData.visit}`, true, false);
        }
        if (aggregationType === AggregationType.SUBJECT_CYCLE) {
            const exposureMetadata = this.studyService.metadataInfo['exposure'];
            if (exposureMetadata['hasVisitsMapped']) {
                result += this.buildTooltipLine('Visit', exposureData.visit, true, false);
            }
            if (exposureMetadata['hasDaysMapped']) {
                result += this.buildTooltipLine('Day', exposureData.day, true, false);
            }
            if (exposureMetadata['hasDosesMapped']) {
                result += this.buildTooltipLine('Nominal dose', exposureData.dose, true, false);
            }
        }
        result += `</div>`;

        return result;
    }

    private buildTooltipLine(name: string, value: string, newLineBefore = false, newLineAfter = true) {
        let tooltipLine = `<span>${name}: ${value}</span>`;
        if (newLineBefore) {
            tooltipLine = `<br/>${tooltipLine}`;
        }
        if (newLineAfter) {
            tooltipLine += `<br/>`;
        }
        return tooltipLine;
    }
}
