import {async, inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {HttpClientModule} from '@angular/common/http';

import {AesSummaryHttpService} from './AesSummaryHttpService';

describe('GIVEN AEsSummaryAnyDataService', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientModule, HttpClientTestingModule],
            providers: [AesSummaryHttpService]
        });
    });

    describe('When get data for any category', () => {
        it('should match /aes/any url', async(inject([AesSummaryHttpService, HttpTestingController],
            (service: AesSummaryHttpService, backend: HttpTestingController) => {
                service.getData(null, 'any').subscribe();
                backend.match('/aes/any');
            })));
    });


});
