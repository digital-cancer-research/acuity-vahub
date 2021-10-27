import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanDeactivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {FilterId} from '../../common/module';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';
import {FiltersService} from '../../data/FiltersService';

import {DoseProportionalityFiltersModel, PopulationFiltersModel} from '../../filters/module';
import {DoseProportionalityPlotComponent} from '../dose-proportionality/dose-proportionality-plot/DoseProportionalityPlotComponent';
import {CanDeactivateBase} from './CanDeactivateBase';

@Injectable()
export class CanDeactivateDoseProportionality extends CanDeactivateBase
    implements CanDeactivate<DoseProportionalityPlotComponent> {
    constructor(doseProportionalityFiltersModel: DoseProportionalityFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(doseProportionalityFiltersModel, FilterId.DOSE_PROPORTIONALITY, filtersService,
            populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: DoseProportionalityPlotComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
