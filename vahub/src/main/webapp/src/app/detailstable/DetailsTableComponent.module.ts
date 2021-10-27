import {NgModule} from '@angular/core';
import {DetailsTableComponent} from './DetailsTableComponent';
import {DetailsTableService} from './DetailsTableService';
import {DetailsOnDemandHeightService} from '../common/trellising/detailsondemand/services/DetailsOnDemandHeightService';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [DetailsTableComponent],
    declarations: [DetailsTableComponent],
    providers: [DetailsTableService, DetailsOnDemandHeightService],
})
export class DetailsTableComponentModule { }
