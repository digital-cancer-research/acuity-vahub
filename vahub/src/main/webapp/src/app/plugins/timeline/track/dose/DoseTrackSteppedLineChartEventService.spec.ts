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

import {DoseTrackSteppedLineChartEventService} from './DoseTrackSteppedLineChartEventService';
import {TestBed, inject} from '@angular/core/testing';
import {SessionEventService} from '../../../../session/event/SessionEventService';
import {MockSessionEventService, MockStudyService} from '../../../../common/MockClasses';
import {StudyService} from '../../../../common/StudyService';

describe('GIVEN a DoseTrackSteppedLineChartEventService', () => {
    let service: DoseTrackSteppedLineChartEventService;
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                DoseTrackSteppedLineChartEventService,
                {
                    provide: SessionEventService,
                    useClass: MockSessionEventService
                },
                {provide: StudyService, useClass: MockStudyService}
            ]
        });
    });

    beforeEach(inject([DoseTrackSteppedLineChartEventService], (_doseTrackSteppedLineChartEventService: DoseTrackSteppedLineChartEventService) => {
        service = _doseTrackSteppedLineChartEventService;
        spyOn(service, 'isOngoing').and.returnValue(false);
    }));

    describe('WHEN plot data series are requested', () => {
        it('THEN should return correct plot data series', () => {
            const data: any[] = [{
                'group': 'dose_1',
                'start': {
                    'date': '2013-10-19T11:15:00',
                    'dayHour': 0.46875,
                    'dayHourAsString': '0d 11:15',
                    'studyDayHourAsString': '1d 11:15'
                },
                'end': {
                    'date': '2014-12-19T23:59:59',
                    'dayHour': 426.99999,
                    'dayHourAsString': '426d 23:59',
                    'studyDayHourAsString': '427d 23:59'
                },
                'plotOptions': {'color': '#00007A'},
                'metadata': {
                    'duration': 427,
                    'ongoing': false,
                    'imputedEndDate': false,
                    'active': true,
                    'drugDoses': [{
                        'drug': 'dose_1',
                        'dose': 700,
                        'doseUnit': 'mg',
                        'frequency': {'name': '2 Times per Week', 'rank': 0.2857}
                    }],
                    'periodType': 'ACTIVE',
                    'subsequentPeriodType': 'INACTIVE'
                }
            }, {
                'group': 'dose_1',
                'start': {
                    'date': '2014-12-19T23:59:59',
                    'dayHour': 426.99999,
                    'dayHourAsString': '426d 23:59',
                    'studyDayHourAsString': '427d 23:59'
                },
                'end': {
                    'date': '2014-12-31T12:12:00',
                    'dayHour': 438.50833,
                    'dayHourAsString': '438d 12:12',
                    'studyDayHourAsString': '439d 12:12'
                },
                'plotOptions': {'color': '#CFCFCF'},
                'metadata': {
                    'duration': 13,
                    'ongoing': false,
                    'imputedEndDate': false,
                    'active': false,
                    'drugDoses': [{
                        'drug': 'dose_1',
                        'dose': 0,
                        'doseUnit': null,
                        'frequency': {'name': 'N/A', 'rank': 0}
                    }],
                    'periodType': 'INACTIVE',
                    'subsequentPeriodType': 'DISCONTINUED'
                }
            }];

            const categories = [];

            const plotDataSeries = service.createPlotDataSeries(data, categories);
            const expectedDataSeries = [{
                'name': 'dosingEvents',
                'color': 'green',
                'marker': {'enabled': true, 'states': {'hover': {'radiusPlus': 2}, 'select': {'fillColor': 'red'}}},
                'data': [{
                    'x': 0.46875,
                    'y': 0,
                    'marker': {'enabled': false, 'states': {'hover': {'enabled': false}}}
                }, {
                    'x': 0.46875,
                    'y': 700,
                    'tooltip': '<br/>Event: <b>Dose Event</b><br/>Dose: <b>700 mg 2 Times per Week</b><br/>Start: <b>0d 11:15</b><br/>Study Day Start: <b>1d 11:15</b><br/> End: <b>426d 23:59</b><br/>Study Day End: <b> 427d 23:59</b> <br/>Ongoing: <b>false</b>',
                    'marker': {'symbol': 'circle', 'enabled': false}
                }, {
                    'x': 426.99999,
                    'y': 700,
                    'tooltip': '<br/>Event: <b>Dose Event</b><br/>Dose: <b>700 mg 2 Times per Week</b><br/>Start: <b>0d 11:15</b><br/>Study Day Start: <b>1d 11:15</b><br/> End: <b>426d 23:59</b><br/>Study Day End: <b> 427d 23:59</b> <br/>Ongoing: <b>false</b>',
                    'marker': {'symbol': 'circle', 'enabled': false}
                }, {
                    'x': 426.99999,
                    'y': 0,
                    'marker': {'enabled': false, 'states': {'hover': {'enabled': false}}}
                }, {
                    'x': 426.99999,
                    'y': 0,
                    'tooltip': '<br/>Event: <b>Dose Event</b><br/>Dose: <b>0  N/A</b><br/>Start: <b>426d 23:59</b><br/>Study Day Start: <b>427d 23:59</b><br/> End: <b>438d 12:12</b><br/>Study Day End: <b> 439d 12:12</b> <br/>Ongoing: <b>false</b>',
                    'marker': {'symbol': 'circle', 'enabled': false}
                }, {
                    'x': 438.50833,
                    'y': 0,
                    'yAsString': '426d 23:59',
                    'tooltip': '<br/><b>Discontinued</b> at <b>438d 12:12</b><br/><b>Discontinued</b> at <b> study day 439d 12:12</b>',
                    'marker': {'symbol': 'circle', 'height': 10, 'width': 20}
                }, null],
                'zoneAxis': 'x',
                'zones': [],
                'step': 'right'
            }];

            expect(JSON.stringify(plotDataSeries)).toEqual(JSON.stringify(expectedDataSeries));
        });
    });
});
