import {Injectable} from '@angular/core';
import {
    RespiratoryComponent
} from '../module';
import {
    LungFunctionFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateRespiratory extends CanDeactivateBase implements CanDeactivate<RespiratoryComponent> {

    constructor(lungFunctionFiltersModel: LungFunctionFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(lungFunctionFiltersModel, FilterId.LUNG_FUNCTION, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: RespiratoryComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
