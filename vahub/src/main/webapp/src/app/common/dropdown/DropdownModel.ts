export class DropdownModel {
    public isOpen: boolean;
    public isClicked: boolean;
    public index: string;

    constructor() {
        this.isClicked = false;
        this.isOpen = false;
    }
}
