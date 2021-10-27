import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';

import {
    ExposureFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {ExposureComponent} from '../exposure';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateExposure extends CanDeactivateBase implements CanDeactivate<ExposureComponent> {

    constructor(exposureFiltersModel: ExposureFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(exposureFiltersModel, FilterId.EXPOSURE, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: ExposureComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
