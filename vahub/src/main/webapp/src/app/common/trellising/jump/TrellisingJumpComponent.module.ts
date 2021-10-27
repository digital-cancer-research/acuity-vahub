import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TrellisingJumpService} from './TrellisingJumpService';
import {TrellisingJumpComponent} from './TrellisingJumpComponent';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [TrellisingJumpComponent],
    declarations: [TrellisingJumpComponent],
    providers: [TrellisingJumpService]
})
export class TrellisingJumpComponentModule {
}
