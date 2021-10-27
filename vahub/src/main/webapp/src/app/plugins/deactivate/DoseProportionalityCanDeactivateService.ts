/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
