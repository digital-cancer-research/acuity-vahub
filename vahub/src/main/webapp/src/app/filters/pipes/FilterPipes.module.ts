import {NgModule} from '@angular/core';

import {FilterPipe} from './FilterPipe';
import {OrderByPipe} from './OrderByPipe';

@NgModule({
    imports: [],
    exports: [FilterPipe, OrderByPipe],
    declarations: [FilterPipe, OrderByPipe],
    providers: [],
})
export class FilterPipesModule {
}
