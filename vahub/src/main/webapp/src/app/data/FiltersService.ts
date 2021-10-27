import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {SessionEventService} from '../session/module';
import {FilterId} from '../common/trellising/store/ITrellising';
import {HttpServiceFactory} from './HttpServiceFactory';

@Injectable()
export class FiltersService {
    constructor(private httpServiceFactory: HttpServiceFactory,
                private sessionEventService: SessionEventService) {
    }

    public getSubjectsInFilters(filterId: FilterId): Observable<string[]> {
        return this.httpServiceFactory.getHttpServiceForFilter(filterId).getSubjectsInFilters(this.sessionEventService.currentSelectedDatasets);
    }
}
