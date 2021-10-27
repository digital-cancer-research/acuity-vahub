import {Injectable} from '@angular/core';
import {
    CardiacComponent
} from '../module';
import {
    CardiacFiltersModel,
    PopulationFiltersModel
} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';

@Injectable()
export class CanDeactivateCardiac extends CanDeactivateBase implements CanDeactivate<CardiacComponent> {

    constructor(cardiacFiltersModel: CardiacFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(cardiacFiltersModel, FilterId.CARDIAC, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: CardiacComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
