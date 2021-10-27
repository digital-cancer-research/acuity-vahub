import {NgModule} from '@angular/core';
import {NavigatorComponent} from './NavigatorComponent';
import {HelpService} from '../help/HelpService';
import {TimelineHelpService} from '../help/TimelineHelpService';
import {AdminService} from '../../common/AdminService';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ModalMessageComponentModule, ProgressComponentModule} from '../../common/module';
import {DatasetNamesComponent} from './datasetNames/DatasetNamesComponent';

@NgModule({
    imports: [CommonModule, FormsModule, ProgressComponentModule, ModalMessageComponentModule],
    exports: [NavigatorComponent],
    declarations: [NavigatorComponent, DatasetNamesComponent],
    providers: [HelpService, TimelineHelpService, AdminService]
})
export class NavigatorComponentModule { }
