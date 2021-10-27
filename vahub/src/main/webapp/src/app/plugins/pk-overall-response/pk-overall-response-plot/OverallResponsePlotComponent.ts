import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.PK_RESULT_OVERALL_RESPONSE"></trellising-component>',
    styles: [
        `:host /deep/ new-trellis-xaxis .axis-label {
            padding: 6px 0;
        }

        :host /deep/ new-trellis-xaxis .axis-container {
            width: 100%;
        }`
    ]
})

export class OverallResponsePlotComponent extends TChartComponent {

}
