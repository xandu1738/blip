import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection
} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';

import {routes} from './app.routes';
import Nora from '@primeuix/themes/nora';
import {providePrimeNG} from 'primeng/config';
import {definePreset} from '@primeuix/themes';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {authInterceptor} from './interceptors/auth.interceptor';


const CustomPreset = definePreset(Nora, {
  primitive: {
    // override primitive colors
    blue: {
      500: '#123456',
      600: '#0f2a45',
    },
    primary: {
      500: '#123456'
    }
  },
  semantic: {
    primary: {
      50: '{indigo.50}',
      100: '{indigo.100}',
      200: '{indigo.200}',
      300: '{indigo.300}',
      400: '{indigo.400}',
      500: '{indigo.500}',
      600: '{indigo.600}',
      700: '{indigo.700}',
      800: '{indigo.800}',
      900: '{indigo.900}',
      950: '{indigo.950}'
    },
    color: '#2563eb',
    contrastColor: '#ffffff',
    hoverColor: '#1e40af',
    activeColor: '#1e3a8a',
    surface: {
      background: '#fafafa'
    }
  },
  components: {
    button: {
      root: {
        primary: {
          background: '#123456',
          color: '#ffffff'
        }
      }
    }
  }
});


export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({
      eventCoalescing: true
    }),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: CustomPreset,
        options: {
          darkModeSelector: '.dark-mode'
        }
      },
      ripple: true
    }),
    provideRouter(routes),
    DialogService,
    ConfirmationService,
    MessageService
  ]
};
