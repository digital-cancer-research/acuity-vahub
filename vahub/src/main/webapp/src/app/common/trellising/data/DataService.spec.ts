import {async, inject, TestBed} from '@angular/core/testing';
import {DataService} from './DataService';
import {HttpServiceFactory} from '../../../data/HttpServiceFactory';
import {MockHttpServiceFactory, MockSessionEventService, MockStudyService} from '../../MockClasses';
import {TabId} from '../store';
import {Observable} from 'rxjs/Observable';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {StudyService} from '../../StudyService';
import {HttpClientModule} from '@angular/common/http';

describe('GIVEN DataService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                {
                    provide: HttpServiceFactory,
                    useClass: MockHttpServiceFactory
                },
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: StudyService, useClass: MockStudyService},
                DataService
            ],
            imports: [HttpClientModule]
        });

    });

    describe('WHEN DataService is getting details on demand data', () => {
        describe('AND eventIds are empty', () => {

            it('SHOULD not make a request', async(inject([DataService, HttpServiceFactory], (dataService: DataService,
                                                                                              httpServiceFactory: HttpServiceFactory) => {
                spyOn(httpServiceFactory, 'getHttpService');

                const result = dataService.getDetailsOnDemandData(TabId.POPULATION_BARCHART, [], 0, 1000, '', '');

                expect(httpServiceFactory.getHttpService).not.toHaveBeenCalled();
            })));

            it('SHOULD return empty observable',  async(inject([DataService], (dataService: DataService) => {
                const result = dataService.getDetailsOnDemandData(TabId.POPULATION_BARCHART, [], 0, 1000, '', '');
                expect(result).toEqual(Observable.of([]));
            })));

        });
    });
});
