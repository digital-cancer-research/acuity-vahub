import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {DatasetViews} from '../../security/module';
import {AbstractPluginComponent} from '../AbstractPluginComponent';

@Component({
    templateUrl: '../tumour-response/TumourResponseComponent.html'
})
export class TumourTherapyComponent extends AbstractPluginComponent {

    constructor(public datasetViews: DatasetViews,
                private router: Router) {
        super();
    }

    isActive(value: string): boolean {
        return this.router.isActive(value, false);
    }
}
