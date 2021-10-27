import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {CommonDirectivesModule} from '../../common/directives/directives.module';

import {TrellisingComponentModule} from '../../common/trellising/trellising';
import {CanActivatePkResultOverallResponse} from './pk-overall-response-plot/CanActivatePkResultOverallResponse';
import {PkResultWithResponseComponent} from './PkResultWithResponseComponent';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        TrellisingComponentModule,
        CommonDirectivesModule,
    ],
    declarations: [PkResultWithResponseComponent],
    exports: [PkResultWithResponseComponent],
    providers: [CanActivatePkResultOverallResponse]
})
export class PkResultWithResponseComponentModule {

}
