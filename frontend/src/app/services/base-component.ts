import {Directive, OnInit} from '@angular/core';
import {RemoteService} from './remoteService';
import {Observable} from 'rxjs';
import {LoaderService} from './loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';

@Directive({
  selector: '[appBaseComponent]'
})
export abstract class BaseComponent implements OnInit {
  ngOnInit() {
    //Placeholder
  }

  constructor(
    protected helper: RemoteService,
    public loaderService: LoaderService,
    protected dialogService: DialogService,
    protected confirmationService: ConfirmationService,
    protected messageService: MessageService) {
    this.loaderService.status.subscribe((val: boolean) => {
      this.showLoader = val;
    });
  }

  public showLoader: boolean = false;

  confirmDialogSettings = {
    header: 'Confirmation',
    icon: 'pi pi-exclamation-triangle',
  };
  private readonly BASE_URL: string = 'http://localhost:7071/api/v1/';

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
    this.confirmDialogSettings.icon = confirmationRequest.icon;
    if (confirmationRequest.icon) {
      this.confirmDialogSettings.icon = confirmationRequest.icon;
    }

    //Replace \n with <br> in message
    if (confirmationRequest.message) {
      confirmationRequest.message = confirmationRequest.message.replaceAll(
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
      messageRequest.message = messageRequest.message.replaceAll(
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

  protected sendGetOrPostRequestToServer(
    url: string,
    requestData: any,
    blockUi: boolean,
    responseHandler: any,
    authenticated: boolean = true,
    errorHandler: any = null) {
    if (blockUi) {
      this.showLoading();
    }
    let requestOperation: Observable<any>;
    if (requestData){
      requestOperation = this.helper.sendPostToServer(
        this.BASE_URL + url,
        requestData,
        authenticated
      );
    }else {
      requestOperation = this.helper.sendGetToServer(
        this.BASE_URL + url,
      )
    }

    requestOperation.subscribe({
      next: (response: any) => {
        this.hideLoading();
        //The below will catch responses with no response codes and response messages
        if (Number.isNaN(response.returnCode)) {
          response.returnCode = 9999;
        }
        if (!response.returnMessage) {
          response.returnMessage =
            'The operation completed at the server, but the server did not return a response as expected.';
        }

        if (responseHandler) {
          responseHandler(response);
        }
      },
      error: (err: any) => {
        // Log errors if any
        this.hideLoading();
        if (!errorHandler) {
          this.showError('Failure connecting to server. ' + err);
          console.log(err);
        } else {
          errorHandler(err);
        }
      },
    });
  }

}
