import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: `<trellising-component [tabId]="tabId.TL_DIAMETERS_PLOT"></trellising-component>`
})
export class TLDiametersComponent extends TChartComponent { }
