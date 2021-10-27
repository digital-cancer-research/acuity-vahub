import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {async, inject, TestBed} from '@angular/core/testing';
import {FilterEventService, FilterHttpService, LabsFiltersModel, PopulationFiltersModel} from '../../filters/module';

import {MockFilterModel, MockHttpClient} from '../../common/MockClasses';
import {Observable} from 'rxjs/Observable';
import {PlotType} from 'app/common/module';
import {LabsBoxPlotHttpService} from './LabsBoxPlotHttpService';

describe('GIVEN LabsBoxPlotHttpService', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: HttpClient, useClass: MockHttpClient},
                {provide: PopulationFiltersModel, useValue: new MockFilterModel()},
                {provide: LabsFiltersModel, useValue: new MockFilterModel()},
                FilterEventService,
                FilterHttpService,
                {
                    provide: LabsBoxPlotHttpService,
                    useClass: LabsBoxPlotHttpService,
                    deps: [HttpClient, PopulationFiltersModel, LabsFiltersModel]
                }
            ]
        });
    });

    describe('WHEN we get trellis options', () => {

        it('THEN the data is returned', async(inject([HttpClient, LabsBoxPlotHttpService], (httpClient, httpService) => {
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

            httpService.getTrellisOptions(currentDatasets, new Map([['groupByOption', 'yAxisOption']])).subscribe((res) => {
                expect(res).toEqual(mockResponse);
            });
        })));
    });
});
