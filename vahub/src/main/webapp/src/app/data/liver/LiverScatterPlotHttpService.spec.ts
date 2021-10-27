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

import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {async, inject, TestBed} from '@angular/core/testing';

import {
    FilterEventService,
    FilterHttpService,
    LiverFunctionFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';

import {MockFilterModel, MockHttpClient} from '../../common/MockClasses';
import {Observable} from 'rxjs/Observable';
import {PlotType} from '../../common/trellising/store';
import {LiverScatterPlotHttpService} from './LiverScatterPlotHttpService';
import {translateTrellisingFromServer, translateValueFromServer} from './LiverCompatibility';

describe('GIVEN LiverScatterPlotHttpService', () => {

    const mockSelection = {
        maxX: 1.4224283305227656,
        maxY: 22,
        minX: -0.4662731871838111,
        minY: 0
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: HttpClient, useClass: MockHttpClient},
                {provide: PopulationFiltersModel, useValue: new MockFilterModel()},
                {provide: LiverFunctionFiltersModel, useValue: new MockFilterModel()},
                FilterEventService,
                FilterHttpService,
                {
                    provide: LiverScatterPlotHttpService,
                    useClass: LiverScatterPlotHttpService,
                    deps: [HttpClient, PopulationFiltersModel, LiverFunctionFiltersModel]
                },
            ]
        });
    });

    describe('WHEN we get scatter plot data', () => {

        it('THEN the data is returned', async(inject([HttpClient, LiverScatterPlotHttpService], (httpClient, httpService) => {
            const mockResponse = [{
                'trellisedBy': [{
                    'trellisedBy': 'MEASUREMENT',
                    'category': 'MANDATORY_TRELLIS',
                    'trellisOption': 'ALT'
                }, {
                    'trellisedBy': 'ARM',
                    'category': 'NON_MANDATORY_TRELLIS',
                    'trellisOption': 'Placebo'
                }],
                'data': {
                    'data': [{
                        'x': 0.96,
                        'y': 1.24,
                        'color': '#27ae60',
                        'name': 'DummyTFL-1676642218'
                    }, {
                        'x': 1.1416666666666666,
                        'y': 0.6,
                        'color': '#27ae60',
                        'name': 'DummyTFL-3455912650'
                    }],
                    'yaxisLabel': 'Max. normalised ALT',
                    'xaxisLabel': 'Max. normalised bilirubin'
                }
            }];
            const currentDatasets = [{id: 123, type: 'DetectDataset', name: 'DummyData'}];

            const trellising = [
                {category: 'MANDATORY_TRELLIS', trellisedBy: 'MEASUREMENT', trellisOptions: ['AST', 'ALT']},
                {
                    category: 'NON_MANDATORY_TRELLIS',
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'SuperDex 10 mg', 'SuperDex 20 mg']
                }
            ];
            spyOn(httpClient, 'post').and.returnValue(Observable.of(mockResponse));
            httpService.getData(currentDatasets, 'VISIT_NUM', 'ACTUAL_VALUE', trellising).subscribe((res) => {
                const expectedResponse = [{
                    plotType: PlotType.SCATTERPLOT,
                    trellising: translateTrellisingFromServer(mockResponse[0].trellisedBy),
                    data: translateValueFromServer(mockResponse[0])
                }];
                expect(res.toJS()).toEqual(expectedResponse);
            });
        })));
    });

    describe('WHEN we get scatter plot selection data', () => {

        it('THEN the selected items data is returned', async(inject([HttpClient, LiverScatterPlotHttpService],
            (httpClient, httpService) => {
                const mockResponse = {
                    'eventIds': ['DummyTFL-5854760196'],
                    'subjectIds': ['DummyTFL-5854760196'],
                    'totalEvents': 2897,
                    'totalSubjects': 197
                };
                const currentDatasets = [{id: 123, type: 'DetectDataset', name: 'DummyData'}];

                const trellising = [
                    {category: 'MANDATORY_TRELLIS', trellisedBy: 'MEASUREMENT', trellisOptions: ['AST', 'ALT']},
                    {
                        category: 'NON_MANDATORY_TRELLIS',
                        trellisedBy: 'ARM',
                        trellisOptions: ['Placebo', 'SuperDex 10 mg', 'SuperDex 20 mg']
                    }
                ];

                spyOn(httpClient, 'post').and.returnValue(Observable.of(mockResponse));

                httpService.getSelectionDetail(currentDatasets, null, null, trellising, null, mockSelection).subscribe((res) => {
                    expect(res).toEqual(mockResponse);
                });
            })));
    });

    describe('WHEN we get trellis options', () => {

        it('THEN the data is returned', async(inject([HttpClient, LiverScatterPlotHttpService], (httpClient, httpService) => {
            const mockResponse = [{
                trellisedBy: 'MEASUREMENT',
                category: 'MANDATORY_TRELLIS',
                trellisOptions: ['PH-HYPO', 'CREATININE (MG/DL)']
            }, {
                trellisedBy: 'ARM',
                category: 'NON_MANDATORY_TRELLIS',
                trellisOptions: ['Placebo', 'SuperDex 10 mg', 'SuperDex 20 mg']
            }];
            const currentDatasets = [{id: 123, type: 'DetectDataset', name: 'DummyData'}];

            spyOn(httpClient, 'post').and.returnValue(Observable.of(mockResponse));

            httpService.getTrellisOptions(currentDatasets).subscribe((res) => {
                expect(res).toEqual(mockResponse);
            });
        })));
    });
});
