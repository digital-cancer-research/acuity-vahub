import {Injectable} from '@angular/core';

import {
    CIEventsComponent
} from '../module';
import {
    CIEventsFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateCIEvents extends CanDeactivateBase implements CanDeactivate<CIEventsComponent> {

    constructor(cieventsFiltersModel: CIEventsFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(cieventsFiltersModel, FilterId.CIEVENTS, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: CIEventsComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
