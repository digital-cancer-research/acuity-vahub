import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {CommonPipesModule} from '../../../common/pipes';
import { FilterCollectionComponent } from './FilterCollectionComponent';
import {
    ListFilterComponent,
    CheckListFilterComponent,
    RangeFilterComponentModule,
    RangeDateFilterComponent,
    MapListFilterComponent,
    MapRangeDateFilterComponent,
    MapRangeFilterComponent,
    StudySpecificFilterComponent,
    UnselectedCheckListFilterComponent
} from '../../components/module';

import {FilterPipesModule} from '../../pipes/FilterPipes.module';

@NgModule({
    imports: [CommonModule, CommonPipesModule, FormsModule, RangeFilterComponentModule, FilterPipesModule],
    exports: [FilterCollectionComponent],
    declarations: [
        FilterCollectionComponent,
        ListFilterComponent,
        CheckListFilterComponent,
        RangeDateFilterComponent,
        MapListFilterComponent,
        MapRangeDateFilterComponent,
        MapRangeFilterComponent,
        StudySpecificFilterComponent,
        UnselectedCheckListFilterComponent
    ],
    providers: [],
})
export class FilterCollectionComponentModule { }
