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

import {BrowserModule} from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {HashLocationStrategy, Location, LocationStrategy} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CookieModule} from 'ngx-cookie';
import {ExportUtils} from './common/utils/ExportUtils';
import {environment} from '../environments/environment';
import {EffectsModule} from '@ngrx/effects';

import {AppComponent} from './AppComponent';
import {appRoutes, routingComponents, routingImports} from './AppComponent.router';
import {SessionHttpService} from './session/http/SessionHttpService';
import {SessionEventService} from './session/event/SessionEventService';
import {EnvService} from './env/module';
import {
    ChoiceModalComponentModule,
    GovernanceStatementModalComponent,
    ModalMessageComponentModule,
    ProgressComponentModule,
    StudyService
} from './common/module';
import {TimeoutService} from './session/timeout/TimeoutService';
import {DatasetViews, UserPermissions} from './security/module';
import {NavigatorComponentModule, PluginsSideNavBarComponent, StudySelectionSideNavBarComponent} from './nav/module';
import {canActivateServiceImports, canDeactivateServiceImports} from './plugins/PluginsComponent.routing';
import {CanActivatePlugins, UrlParcerService} from './plugins/activate';
import {CanDeactivatePlugins} from './plugins/deactivate/CanDeactivatePlugins';
import {FiltersService} from './data/FiltersService';
import {CanActivateAppService} from './CanActivateAppService';
import {UserActivityService} from './UserActivityService';
import {FiltersUtils} from './filters/utils/FiltersUtils';
import {UserActivityHttpService} from './UserActivityHttpService';
import {DateUtilsService} from './common/utils/DateUtilsService';
import {filtersProviders} from './filters/FiltersBarComponent.module';
import {reducers} from './common/store/reducers';
import {AboutComponentContainerModule} from './about/AboutComponent.module';
import {StudySelectionComponentModule} from './studyselection/StudySelectionComponent.module';
import {DetailsTableComponentModule} from './detailstable/DetailsTableComponent.module';
import {CommonDirectivesModule} from './common/directives/directives.module';
import {PluginsService} from './plugins/PluginsService';
import {ConfigurationService} from './configuration/module';

import {AddDefaultHeadersInterceptor, ErrorHandlingInterceptor} from './common/http';
import {PropertiesResolver} from './resolvers/PropertiesResolver';
import {BaseComponent} from './base/BaseComponent';

/** Http interceptor providers in outside-in order */
export const httpInterceptorProviders = [
    { provide: HTTP_INTERCEPTORS, useClass: AddDefaultHeadersInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorHandlingInterceptor, multi: true }
];

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        HttpClientXsrfModule,
        FormsModule,
        RouterModule.forRoot(appRoutes, {useHash: true}),
        StoreModule.forRoot(reducers),
        !environment.production ? StoreDevtoolsModule.instrument({ maxAge: 99 }) : [],
        CookieModule.forRoot(),
        ProgressComponentModule,
        ModalMessageComponentModule,
        NavigatorComponentModule,
        ChoiceModalComponentModule,
        AboutComponentContainerModule,
        StudySelectionComponentModule,
        DetailsTableComponentModule,
        CommonDirectivesModule,
        EffectsModule.forRoot([]),
        ...routingImports
    ],
    declarations: [AppComponent,
        BaseComponent,
        PluginsSideNavBarComponent,
        StudySelectionSideNavBarComponent,
        PluginsSideNavBarComponent,
        GovernanceStatementModalComponent,
        ...routingComponents
    ],
    providers: [
        httpInterceptorProviders,
        HttpClient,
        Location,
        TimeoutService,
        {provide: LocationStrategy, useClass: HashLocationStrategy},
        SessionHttpService,
        SessionEventService,
        EnvService,
        UserPermissions,
        DatasetViews,
        StudyService,
        FiltersService,
        UrlParcerService,
        CanActivatePlugins,
        CanActivateAppService,
        PropertiesResolver,
        CanDeactivatePlugins,
        UserActivityService,
        UserActivityHttpService,
        DateUtilsService,
        ...canDeactivateServiceImports,
        ...canActivateServiceImports,
        ...filtersProviders,
        FiltersUtils,
        ExportUtils,
        PluginsService,
        ConfigurationService
    ],
    bootstrap: [AppComponent],
})
export class AppModule {
}
