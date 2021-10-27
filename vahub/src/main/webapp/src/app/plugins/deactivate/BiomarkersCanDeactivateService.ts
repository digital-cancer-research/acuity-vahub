import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanDeactivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {BiomarkersComponent} from '../module';
import {PopulationFiltersModel} from '../../filters/module';
import {CanDeactivateBase} from './CanDeactivateBase';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';
import {BiomarkersFiltersModel} from '../../filters/dataTypes/biomarkers/BiomarkersFiltersModel';

@Injectable()
export class CanDeactivateBiomarkers extends CanDeactivateBase implements CanDeactivate<BiomarkersComponent> {

    constructor(biomarkersFiltersModel: BiomarkersFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(biomarkersFiltersModel, FilterId.BIOMARKERS, filtersService, populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: BiomarkersComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
