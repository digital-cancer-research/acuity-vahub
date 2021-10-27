import {NgModule} from '@angular/core';

import {SentenceCasePipe} from './SentenceCasePipe';
import {LabelPipe} from './LabelPipe';
import {MonthsOrderByPipe} from './MonthsOrderByPipe';
import {RoundValuePipe} from './RoundValuePipe';
import {IntervalsOrderByPipe} from './IntervalsOrderByPipe';
import {CapitalizePipe} from './CapitalizePipe';
import {UtcPipe} from './UtcPipe';
import {ToLowerCasePipe} from './ToLowerCasePipe';
import {LogarithmicScalePipe} from './LogarithmicScalePipe';
import {DodTitlePipe} from './DodTitlePipe';
import {SettingsPipe} from './SettingsPipe';
import {RemoveParenthesesPipe} from './RemoveParenthesesPipe';
import {GroupByOptionPipe} from './GroupByOptionPipe';
import {AlphabeticOrderByPipe} from './AlphabeticOrderByPipe';
import {DisplayLabelPipe} from './DisplayLabelPipe';

const pipes = [
    AlphabeticOrderByPipe,
    IntervalsOrderByPipe,
    SentenceCasePipe,
    LabelPipe,
    MonthsOrderByPipe,
    RoundValuePipe,
    UtcPipe,
    CapitalizePipe,
    ToLowerCasePipe,
    LogarithmicScalePipe,
    DodTitlePipe,
    SettingsPipe,
    RemoveParenthesesPipe,
    GroupByOptionPipe,
    DisplayLabelPipe,
];

@NgModule({
    imports: [],
    exports: [...pipes],
    declarations: [...pipes],
    providers: [...pipes],
})
export class CommonPipesModule {
}
