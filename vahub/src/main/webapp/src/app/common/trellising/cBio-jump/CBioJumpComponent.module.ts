import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {NgModule} from '@angular/core';
import {CBioJumpComponent} from './CBioJumpComponent';


@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [CBioJumpComponent],
    declarations: [CBioJumpComponent],
    providers: []
})
export class CBioJumpComponentModule {
}
