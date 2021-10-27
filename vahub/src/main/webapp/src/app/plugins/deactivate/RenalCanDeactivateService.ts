import {Injectable} from '@angular/core';
import {RenalComponent} from '../module';
import {RenalFiltersModel, PopulationFiltersModel} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateRenal extends CanDeactivateBase implements CanDeactivate<RenalComponent> {

    constructor(renalFiltersModel: RenalFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(renalFiltersModel, FilterId.RENAL, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: RenalComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
