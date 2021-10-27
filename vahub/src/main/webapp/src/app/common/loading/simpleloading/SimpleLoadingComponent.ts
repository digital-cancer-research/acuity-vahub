import {Component, Input} from '@angular/core';

/**
 * Small loading component only with circle loader (e.g. when only a table is loading on the page)
 * @property {string[]} messages
 * @property {boolean} loading - input parameter. Loader is shown only if true.
 */
@Component({
    selector: 'simple-loading',
    templateUrl: 'SimpleLoadingComponent.html',
    styleUrls: ['SimpleLoadingComponent.css']
})
export class SimpleLoadingComponent {
    @Input() loading: boolean;

    constructor() {
    }

}
