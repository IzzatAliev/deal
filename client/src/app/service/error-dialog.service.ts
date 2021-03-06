import { Injectable } from '@angular/core';
import { ErrorComponent } from "../pages/error/error.component";
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";

@Injectable()
export class ErrorDialogService {

  public isDialogOpen: Boolean = false;

  constructor(public dialog: MatDialog) { }

  openDialog(data: {message: string, status: string}): any {
    if (this.isDialogOpen) {
      return false;
    }
    this.isDialogOpen = true;
    const dialogRef = this.dialog.open(ErrorComponent, {
      width: '300px',
      data: data
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.isDialogOpen = false;
    });
  }
}
