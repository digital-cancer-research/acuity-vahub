import {Injectable} from '@angular/core';
import {
    VitalsComponent
} from '../module';
import {
    VitalsFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateVitals extends CanDeactivateBase implements CanDeactivate<VitalsComponent> {

    constructor(vitalsFiltersModel: VitalsFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(vitalsFiltersModel, FilterId.VITALS, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: VitalsComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
