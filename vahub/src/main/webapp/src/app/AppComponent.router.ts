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
