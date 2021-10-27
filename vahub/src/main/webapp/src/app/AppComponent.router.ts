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

import {Routes} from '@angular/router';

import {
    pluginsComponentImports,
    pluginsComponentRoutes,
    pluginsComponentRoutingComponents
} from './plugins/PluginsComponent.routing';
import {HomeComponent} from './home/module';
import {SupportComponent} from './support/module';
import {StudySelectionComponent} from './studyselection/StudySelectionComponent';
import {CanActivatePlugins} from './plugins/activate';
import {CanDeactivatePlugins} from './plugins/deactivate/CanDeactivatePlugins';
import {CanActivateAppService} from './CanActivateAppService';
import {PluginsComponent} from './plugins/PluginsComponent';
import {AboutContainerComponent} from './about/containers/AboutContainerComponent';
import {aboutComponentRoutes} from './about/AboutComponent.router';
import {PropertiesResolver} from './resolvers/PropertiesResolver';
import {BaseComponent} from './base/BaseComponent';
import {ManualComponent} from './manual/ManualComponent';

export const appRoutes: Routes = [
    {
        path: '',
        canActivate: [CanActivateAppService],
        resolve: {properties: PropertiesResolver},
        component: BaseComponent,
        children: [
            {
                path: '',
                component: StudySelectionComponent
            },
            {
                path: 'about',
                component: AboutContainerComponent,
                children: aboutComponentRoutes
            },
            {
                path: 'support',
                component: SupportComponent
            },
            {
                path: 'manual',
                component: ManualComponent
            },
            {
                path: 'home',
                component: HomeComponent
            },
            {
                path: 'plugins',
                component: PluginsComponent,
                canActivate: [CanActivatePlugins],
                canDeactivate: [CanDeactivatePlugins],
                children: pluginsComponentRoutes
            }
        ]
    },
    {path: '**', redirectTo: ''}
];

export const routingComponents = [
    SupportComponent,
    HomeComponent,
    PluginsComponent,
    ManualComponent,
    ...pluginsComponentRoutingComponents
];

export const routingImports = pluginsComponentImports;
