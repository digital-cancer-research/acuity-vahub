import {Injectable} from '@angular/core';
import {
    ExacerbationsComponent
} from '../module';
import {
    ExacerbationsFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateExacerbations extends CanDeactivateBase implements CanDeactivate<ExacerbationsComponent> {

    constructor(exacerbationsFiltersModel: ExacerbationsFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(exacerbationsFiltersModel, FilterId.EXACERBATIONS, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: ExacerbationsComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
