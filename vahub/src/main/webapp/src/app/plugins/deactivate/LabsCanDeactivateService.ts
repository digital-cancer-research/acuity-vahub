import {Injectable} from '@angular/core';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {
    LabsComponent
} from '../module';
import {
    LabsFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateLabs extends CanDeactivateBase implements CanDeactivate<LabsComponent> {

    constructor(labsFiltersModel: LabsFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(labsFiltersModel, FilterId.LAB, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: LabsComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
