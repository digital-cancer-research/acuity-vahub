import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {AEsComponent} from '../aes/AEsComponent';
import {CollapseTabsDirective} from '../../common/directives/CollapseTabsDirective';
import {PluginsService} from '../PluginsService';

@Component({
    templateUrl: '../aes/AEsComponent.html'
})

export class CIEventsComponent extends AEsComponent implements AfterViewInit {
    @ViewChild(CollapseTabsDirective) collapseTabsDirective;

    constructor(public pluginsService: PluginsService) {
        super(pluginsService);
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();
    }
}
