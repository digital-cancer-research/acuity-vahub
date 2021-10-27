import { NgModule } from '@angular/core';
import {CollapseTabsDirective} from './CollapseTabsDirective';
import {BetaTooltipDirective} from './BetaTooltipDirective';
import {RoundDirective} from './RoundDirective';

/**
 * Module for all common directives.
 */
@NgModule({
    exports: [CollapseTabsDirective, BetaTooltipDirective, RoundDirective],
    declarations: [CollapseTabsDirective, BetaTooltipDirective, RoundDirective],
})

export class CommonDirectivesModule { }
