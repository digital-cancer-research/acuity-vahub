import {Injectable} from '@angular/core';

import {
    ConmedsComponent
} from '../module';
import {
    ConmedsFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateConmeds extends CanDeactivateBase implements CanDeactivate<ConmedsComponent> {

    constructor(conmedsFiltersModel: ConmedsFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(conmedsFiltersModel, FilterId.CONMEDS, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: ConmedsComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
