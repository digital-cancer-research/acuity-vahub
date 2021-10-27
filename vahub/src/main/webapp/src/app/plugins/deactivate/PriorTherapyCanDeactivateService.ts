import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ActivatedRouteSnapshot, CanDeactivate, Router, RouterStateSnapshot} from '@angular/router';

import {TumourResponseFiltersModel, PopulationFiltersModel} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';
import {TumourTherapyComponent} from '../tumour-therapy/index';

@Injectable()
export class CanDeactivatePriorTherapy extends CanDeactivateBase implements CanDeactivate<TumourTherapyComponent> {

    constructor(tumourResponseFiltersModel: TumourResponseFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(tumourResponseFiltersModel, FilterId.TUMOUR_RESPONSE, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: TumourTherapyComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
