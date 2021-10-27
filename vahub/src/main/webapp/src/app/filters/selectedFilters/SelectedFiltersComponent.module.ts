import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SelectedFiltersComponent} from './SelectedFiltersComponent';
import {ModalMessageComponentModule} from '../../common/modals/modalMessage/ModalMessageComponent.module';
import {SelectedFiltersModel} from './SelectedFiltersModel';
import {BrowserModule} from '@angular/platform-browser';
import {FilterPipesModule} from '../pipes/FilterPipes.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ModalMessageComponentModule,
        BrowserModule,
        FilterPipesModule
    ],
    exports: [SelectedFiltersComponent],
    declarations: [SelectedFiltersComponent],
    providers: [SelectedFiltersModel],
})
export class SelectedFiltersComponentModule {
}
