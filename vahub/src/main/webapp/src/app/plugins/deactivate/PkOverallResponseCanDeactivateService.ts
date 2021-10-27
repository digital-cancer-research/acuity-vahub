import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanDeactivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {FilterId} from '../../common/module';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';
import {FiltersService} from '../../data/FiltersService';

import {PopulationFiltersModel, PkOverallResponseFiltersModel} from '../../filters/module';
import {OverallResponsePlotComponent} from '../pk-overall-response/pk-overall-response-plot/OverallResponsePlotComponent';
import {CanDeactivateBase} from './CanDeactivateBase';

@Injectable()
export class CanDeactivatePkOverallResponse extends CanDeactivateBase
    implements CanDeactivate<OverallResponsePlotComponent> {
    constructor(pkOverallResponseFiltersModel: PkOverallResponseFiltersModel,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        super(pkOverallResponseFiltersModel, FilterId.PK_RESULT_OVERALL_RESPONSE, filtersService,
            populationFiltersModel, router, timelineConfigService);
    }

    canDeactivate(component: OverallResponsePlotComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.canDeactivateBase();
    }
}
