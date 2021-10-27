import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';
import {TrellisingObservables} from '../../../common/trellising/store/observable/TrellisingObservables';

@Component({
    template: `
        <trellising-component [tabId]="tabId.TUMOUR_RESPONSE_WATERFALL_PLOT"></trellising-component>
        <trellising-component *ngIf="waterfallHasOneBarSelection()"
                              [tabId]="tabId.TL_DIAMETERS_PER_SUBJECT_PLOT" [isSubPlot]="true">
        </trellising-component>
    `
})
export class TumourRespWaterfallComponent extends TChartComponent {

    constructor(public trellisingObservables: TrellisingObservables) {
        super();
    }

    waterfallHasOneBarSelection(): boolean {
        const waterfallSelections = this.trellisingObservables.getSelectionByTabId(this.tabId.TUMOUR_RESPONSE_WATERFALL_PLOT);
        return waterfallSelections && waterfallSelections.size > 0 && waterfallSelections.first().get('bars').size === 1;
    }
}
