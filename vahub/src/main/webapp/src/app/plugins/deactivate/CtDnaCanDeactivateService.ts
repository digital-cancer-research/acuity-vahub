import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {CtDNAComponent} from '../module';
import {CtDnaFiltersModel, PopulationFiltersModel} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {ActivatedRouteSnapshot, CanDeactivate, Router, RouterStateSnapshot} from '@angular/router';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateCtDna extends CanDeactivateBase implements CanDeactivate<CtDNAComponent> {

    constructor(ctDnaFiltersModel: CtDnaFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(ctDnaFiltersModel, FilterId.CTDNA, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: CtDNAComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
