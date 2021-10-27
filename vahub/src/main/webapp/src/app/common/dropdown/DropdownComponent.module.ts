import { NgModule } from '@angular/core';

import { DropdownComponent } from './DropdownComponent';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {DropdownService} from './DropdownService';
/**
 * @module for dropdowns
 */
@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [DropdownComponent],
    declarations: [DropdownComponent],
    providers: [DropdownService],
})
export class DropdownComponentModule { }
