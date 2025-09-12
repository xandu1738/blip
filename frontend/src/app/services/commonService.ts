import {Injectable, OnInit} from '@angular/core';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {LoaderService} from './loader.service';

@Injectable({
  providedIn: 'root'
})
export class CommonService implements OnInit {
  constructor(
    public loaderService: LoaderService,
    private dialogService: DialogService,
    public confirmationService: ConfirmationService,
    private messageService: MessageService) {

  }

  ngOnInit() {
    this.loaderService.status.subscribe((val: boolean) => {
      this.showLoader = val;
    });
  }

  public showLoader: boolean = false;

  confirmDialogSettings = {
    header: 'Confirmation',
    icon: 'pi pi-exclamation-triangle',
  };

  openDialog(dialogRequest: any, closable = true) {
    let component = dialogRequest.component;
    let config = dialogRequest.config;

    return this.dialogService.open(component, {
      ...config,
      dismissableMask: false,
      closeOnEscape: false,
      closable: closable,
      modal: true,
    });
  }

  confirmDialog(confirmationRequest: any) {
    //Update the heading and icon if passed
    this.confirmDialogSettings.header =
      confirmationRequest.heading ? confirmationRequest.heading : 'Confirm';
    this.confirmDialogSettings.icon = confirmationRequest.icon
      ? confirmationRequest.icon
      : 'pi pi-exclamation-triangle';
    if (confirmationRequest.icon) {
      this.confirmDialogSettings.icon = confirmationRequest.icon;
    }

    //Replace \n with <br> in message
    if (confirmationRequest.message) {
      confirmationRequest.message = confirmationRequest.message.replace(
        /\r\n|\r|\n/g,
        '<br>'
      );
    }

    this.confirmationService.confirm({
      message: confirmationRequest.message,
      header: this.confirmDialogSettings.header,
      icon: this.confirmDialogSettings.icon,
      accept: () => {
        //Actual logic to perform a confirmation
        if (confirmationRequest.onConfirm) {
          confirmationRequest.onConfirm();
        }
      },
    });
  }

  messageDialog(messageRequest: any) {
    //Update the heading and icon if passed
    let header = messageRequest.heading
      ? messageRequest.heading
      : 'Information';
    let icon = messageRequest.icon ? messageRequest.icon : 'pi pi-check-circle';
    let acceptLabel = messageRequest.acceptLabel
      ? messageRequest.okButton
      : 'OK';

    //Replace \n with <br> in message
    if (messageRequest.message) {
      messageRequest.message = messageRequest.message.replace(
        /\r\n|\r|\n/g,
        '<br>'
      );
    }

    this.confirmationService.confirm({
      message: messageRequest.message,
      header: header,
      icon: icon,
      acceptLabel: acceptLabel,
      rejectVisible: false,
      accept: () => {
        //Actual logic to perform a confirmation
        if (messageRequest.onConfirm) {
          messageRequest.onConfirm();
        }
      },
      reject: () => {
        //Actual logic to perform a confirmation
        if (messageRequest.onConfirm) {
          messageRequest.onConfirm();
        }
      },
    });
  }

  showSuccess(message: string) {
    this.messageService.add({
      severity: 'success',
      summary: 'Success',
      detail: message
    });
  }

  showWarning(message: string) {
    this.messageService.add({
      severity: 'warn',
      summary: 'Warning',
      detail: message
    });
  }

  showError(message: string) {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message
    });
  }


  /**
   * Show loading spinner
   */
  showLoading() {
    this.loaderService.display(true);
  }

  /**
   * Hide loading spinner
   */
  hideLoading() {
    this.loaderService.display(false);
  }
}
