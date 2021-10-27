import {Injectable} from '@angular/core';

import {
    CerebrovascularEventsComponent
} from '../module';
import {
    CerebrovascularFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateCerebrovascular extends CanDeactivateBase implements CanDeactivate<CerebrovascularEventsComponent> {

    constructor(cerebrovascularFiltersModel: CerebrovascularFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(cerebrovascularFiltersModel, FilterId.CEREBROVASCULAR, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: CerebrovascularEventsComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
