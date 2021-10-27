import {Injectable} from '@angular/core';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {RecistFiltersModel} from '../../filters/dataTypes/recist/RecistFiltersModel';
import {PopulationFiltersModel} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';
import {TumourResponseComponent} from '../module';

@Injectable()
export class CanDeactivateRecist extends CanDeactivateBase implements CanDeactivate<TumourResponseComponent> {

    constructor(renalFiltersModel: RecistFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(renalFiltersModel, FilterId.RECIST, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: TumourResponseComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
