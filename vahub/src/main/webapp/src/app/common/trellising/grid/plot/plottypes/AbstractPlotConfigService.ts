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
import {TabId} from '../../../store';

import * as _ from 'lodash';
import {ExportChartService} from '../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../configuration/ConfigurationService';
import Dataset = Request.Dataset;
import {CustomPlotConfig, PlotLine, UserOptions} from '../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class AbstractPlotConfigService {
    constructor(private exportChartService: ExportChartService,
                private configurationService: ConfigurationService) {
    }

    currentDatasets = '';
    REF_RANGE_NORM_VALUE = 'Ref range norm. value';

    public setCurrentDatasets(dataset: Dataset): void {
        if ((<any>dataset).type.toLowerCase().indexOf('detect') !== -1) {
            this.currentDatasets = (<any>dataset).clinicalStudyName + ' End of study';
        } else if ((<any>dataset).type.toLowerCase().indexOf('acuity') !== -1) {
            this.currentDatasets = (<any>dataset).clinicalStudyName + ' Ongoing';
        }
    }

    public isOngoingStudy(type: string): boolean {
        return type.toLowerCase().indexOf('ongoing') !== -1;
    }

    protected createDefaultPlotConfig(customPlotConfig: CustomPlotConfig, height: number, title: string): UserOptions {
        const configService = this;
        const defaultPlotConfig: UserOptions = {
            chart: {
                renderTo: null,
                height: height,
                type: null,
            },
            title: {
                text: title
            },
            exporting: {
                buttons: [
                    {
                        text: 'Save as PNG',
                        onclick: function () {
                            configService.exportChartService.requestImageDownload(
                                this,
                                this.height / this.width,
                                'png',
                                configService.getTitleForExportedImage(this.title.text || this.title).text);
                        }
                    },
                    {
                        text: 'Save as JPG',
                        onclick(): void {
                            configService.exportChartService.requestImageDownload(
                                this,
                                this.height / this.width,
                                'jpg',
                                configService.getTitleForExportedImage(this.title.text || this.title).text);
                        }
                    },
                    {
                        text: 'Print chart',
                        onclick(): void {
                            if (this.tooltip) {
                                this.tooltip.enabled = false;
                            }
                            setTimeout(() => {
                                configService.exportChartService.requestImageDownload(
                                    this,
                                    this.height / this.width,
                                    'pdf',
                                    configService.getTitleForExportedImage(this.title.text || this.title).text);
                                if (this.tooltip) {
                                    this.tooltip.enabled = true;
                                }
                            }, 500);
                        },
                    }
                ]
            }
        };

        return _.merge(defaultPlotConfig, customPlotConfig);
    }

    protected isSingleSubjectTab(tabId: TabId): boolean {
        switch (tabId) {
            case TabId.SINGLE_SUBJECT_LIVER_HYSLAW:
            case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
            case TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT:
            case TabId.SINGLE_SUBJECT_VITALS_LINEPLOT:
            case TabId.SINGLE_SUBJECT_LUNG_LINEPLOT:
            case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
                return true;
            default:
                return false;
        }
    }

    protected getTitleForExportedImage(titleText: string): {text: string} {
        const date = new Date(Date.now()).toLocaleString();
        return {text: titleText + '; Date: ' + date + '; Study name: ' + this.currentDatasets};
    }


    protected addReferenceRangeLines(globalYAxisLabel: string): PlotLine[] {
        return globalYAxisLabel === this.REF_RANGE_NORM_VALUE ? [{
            axis: 'y',
            value: 0,
            color: 'red',
            width: 1
        }, {
            axis: 'y',
            value: 1,
            color: 'red',
            width: 1
        }] : null;
    }

    /**
     * Evaluates rounded-up value (0,25 -> 0.3)
     *
     * @param {number} value number to round
     * @param {number} fraction number of digits in the fractional part of the value
     * @returns {string} rounded value as string
     */
    public round(value: number, fraction: number): string {
        return (Math.round(value * 100) / 100).toFixed(fraction);
    }
}
