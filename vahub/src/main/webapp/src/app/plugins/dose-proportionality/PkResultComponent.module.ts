import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {CommonDirectivesModule} from '../../common/directives/directives.module';

import {TrellisingComponentModule} from '../../common/trellising/trellising';
import {PkResultComponent} from './PkResultComponent';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        TrellisingComponentModule,
        CommonDirectivesModule,
    ],
    declarations: [PkResultComponent],
    exports: [PkResultComponent]
})
export class PkResultComponentModule {

}
