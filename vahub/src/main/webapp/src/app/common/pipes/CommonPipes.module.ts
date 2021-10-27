/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
