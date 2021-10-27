import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {AbstractPluginComponent} from '../AbstractPluginComponent';
import {CollapseTabsDirective} from '../../common/directives/CollapseTabsDirective';
import {PluginsService} from '../PluginsService';

@Component({
    templateUrl: 'AEsComponent.html'
})
export class AEsComponent extends AbstractPluginComponent implements AfterViewInit {
    @ViewChild(CollapseTabsDirective) collapseTabsDirective;

    dropdownOpen = false;

    constructor(public pluginsService: PluginsService) {
        super();
    }

    ngAfterViewInit(): void {
        this.collapseTabsDirective.hideOverflowedTabs();
        this.subscribeToTabChanges();
    }

    subscribeToTabChanges(): void {
        this.pluginsService.selectedTab.subscribe(() => {
            this.collapseTabsDirective.hideOverflowedTabs(true);
            this.dropdownOpen = false;
        });
    }

    toggleDropdown(): void {
        this.dropdownOpen = !this.dropdownOpen;
    }

    onResize(): void {
        this.collapseTabsDirective.hideOverflowedTabs(true);
    }

}
