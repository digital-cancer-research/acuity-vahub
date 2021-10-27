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

import {SentenceCasePipe} from '../common/pipes';
import {TrellisingObservables} from '../common/trellising/store/observable/TrellisingObservables';
import {Injectable} from '@angular/core';
import {DateUtilsService} from '../common/utils/DateUtilsService';
import {StudyService} from '../common/StudyService';
import {SessionEventService} from '../session/event/SessionEventService';
import {downloadFile, getPluginSummary} from '../common/utils/Utils';
import {DisplayLabelPipe} from '../common/pipes';
import {StateUtilsService} from '../common/utils/StateUtilsService';
import * as html2canvas from 'html2canvas';
import {template, isArray} from 'lodash';
import {html} from './export-template';
import {TabRecords} from '../common/trellising';

import {SingleSubjectModel} from '../plugins/refactored-singlesubject/SingleSubjectModel';
import {ConfigurationService} from '../configuration/ConfigurationService';

export const MIN_EXPORT_WIDTH = 700;

@Injectable()
export class ExportChartService {
    private tabRecords = new TabRecords();

    constructor(private dateUtilService: DateUtilsService,
                private studyService: StudyService,
                private trellisingObservables: TrellisingObservables,
                private sessionEventService: SessionEventService,
                private toDisplayPipe: DisplayLabelPipe,
                private sentenceCase: SentenceCasePipe,
                private singleSubjectModel: SingleSubjectModel,
                private stateUtilsService: StateUtilsService,
                private configurationService: ConfigurationService) {
    }

    getTitle() {
        const {module, tab} = this.stateUtilsService.getCurrentTab();
        const options = getPluginSummary(module, tab);
        const tabName = this.tabRecords.get(tab).name;
        return options.eventWidgetName ? `${options.eventWidgetName}: ${tabName}` : tabName;
    }

    getExportingWidthAndMargin(chartData) {
        let chartWidth, plotWidth, yAxisMargin, translateX = 0;

        if (isArray(chartData)) {
            chartWidth = chartData[0].width;
            plotWidth = chartData[0].plotWidth;
            yAxisMargin = chartData[0].margins.left + chartData[0].margins.right || 0;
        } else {
            chartWidth = chartData.width;
            plotWidth = chartData.plotWidth;
            yAxisMargin = chartData.margins.left + chartData.margins.right || 0;
        }
        if (yAxisMargin) {
            chartWidth += yAxisMargin;
        }
        if (chartWidth > plotWidth) {
            translateX += (chartWidth - plotWidth) / 2;
        }
        return { chartWidth, translateX };
    }

    requestImageDownload(chartData: any | any[],
                         ratio: number,
                         type: string,
                         subtitle: string,
                         titlesArray?: string[],
                         title?: string) {
        const orientation = 'portrait';
        // tslint:disable-next-line:prefer-const
        let { chartWidth, translateX } = this.getExportingWidthAndMargin(chartData);
        const marginTop = 10;
        const scale = chartWidth > MIN_EXPORT_WIDTH ? 1 : MIN_EXPORT_WIDTH / chartWidth;
        chartWidth *= scale;

        const additionalOptions = {
            title: null,
            chart: {
                width: chartWidth,
            }
        };

        const iframe = document.createElement('iframe');
        iframe.width = `${chartWidth}px`;

        const notification = StudyService.isOngoingStudy(this.sessionEventService.currentSelectedDatasets[0].type) ?
            `ACUITY: unclean data in an unvalidated system` :
            `ACUITY: analysis data in an unvalidated system`;
        let chart;
        if (!chartData.image) {
            chart = isArray(chartData) ? chartData.map(c => c.getSVG(additionalOptions)) : chartData.getSVG(additionalOptions);
        }

        iframe['srcdoc'] = template(html)({
            title: title || this.getTitle(),
            subtitle,
            chart,
            chartWidth,
            orientation,
            notification,
            titlesArray,
        });
        iframe.style.visibility = 'hidden';
        document.body.appendChild(iframe);
        iframe.addEventListener('load', () => {
            if (chartData && translateX) {
                iframe.contentDocument.querySelector('svg > g')
                    .setAttribute('transform', `translate(${translateX}, ${marginTop})`);
            }
            if (scale > 1) {
                const innerSpace = iframe.contentDocument.querySelector(chartData.innerSpaceSelectorForExport);
                if (innerSpace) {
                    const svg = iframe.contentDocument.querySelector('svg');
                    const height = +svg.getAttribute('height');
                    svg.setAttribute('height', `${height * scale}`);
                    innerSpace.setAttribute('transform', `translate(
                        ${(translateX || chartData.margins.left) * scale},
                        ${marginTop || chartData.margins.top}) scale(${scale}
                    )`);
                }
            }
            if (chartData && chartData.image) {
                const iframeDocument = iframe.contentDocument || iframe.contentWindow.document;
                const before = iframeDocument.getElementById('notification');
                iframeDocument.getElementById('chart').insertBefore(chartData.image, before);
            }
            if (type === 'pdf') {
                iframe.contentWindow.focus();
                const oldTitle = document.title;
                document.title = 'chart.pdf';
                iframe.contentWindow.print();
                iframe.contentWindow.addEventListener('afterprint', function remove () {
                    document.title = oldTitle;
                    iframe.contentWindow.removeEventListener('afterprint', remove);
                    document.body.removeChild(iframe);
                });
            } else {
                // @ts-ignore
                html2canvas(iframe.contentDocument.body, {
                    scale: 1,
                }).then(function (canvas) {
                canvas.toBlob(function(blob) {
                    const url = URL.createObjectURL(blob);
                    switch (type) {
                        case 'png':
                            downloadFile('chart', 'png', url);
                            break;
                        case 'jpg':
                            downloadFile('chart', 'jpg', url);
                            break;
                    }
                    URL.revokeObjectURL(url);
                });
                    document.body.removeChild(iframe);
                });
            }
        });
    }
}
