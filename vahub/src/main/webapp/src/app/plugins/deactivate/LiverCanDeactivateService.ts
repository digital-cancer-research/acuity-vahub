import {Injectable} from '@angular/core';
import {
    LiverFunctionComponent
} from '../module';
import {
    LiverFunctionFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateLiverFunction extends CanDeactivateBase implements CanDeactivate<LiverFunctionComponent> {

    constructor(liverFunctionFiltersModel: LiverFunctionFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(liverFunctionFiltersModel, FilterId.LIVER, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: LiverFunctionComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
