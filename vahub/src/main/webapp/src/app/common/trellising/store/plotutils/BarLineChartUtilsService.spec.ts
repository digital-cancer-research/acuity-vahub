import {TestBed, inject} from '@angular/core/testing';
import {BarLineChartUtilsService} from './BarLineChartUtilsService';

describe('GIVEN a BarLineChartUtilsService class', () => {
    let utils: BarLineChartUtilsService;
    beforeEach(() => {
        utils = new BarLineChartUtilsService();
        TestBed.configureTestingModule({
            providers: [{provide: BarLineChartUtilsService, useValue: utils}],
            declarations: []
        });
    });

    beforeEach(inject([BarLineChartUtilsService], (_barLineChartUtilsService) => {
        utils = _barLineChartUtilsService;
    }));
    describe('WHEN zoom is requested', () => {
    });

});
