import { isPlatformBrowser } from '@angular/common';
import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import posthog, { type PostHogConfig } from 'posthog-js';

@Injectable({ providedIn: 'root' })
export class PostHogService {
  private readonly platformId = inject(PLATFORM_ID);
  private initialized = false;

  get posthog(): typeof posthog {
    if (isPlatformBrowser(this.platformId) && this.initialized) {
      return posthog;
    }

    return new Proxy({} as typeof posthog, {
      get: (_target, property) =>
        property === 'logger'
          ? new Proxy({}, { get: () => () => undefined })
          : () => undefined,
    });
  }

  init(apiKey: string, options: Partial<PostHogConfig>, production: boolean): void {
    if (!isPlatformBrowser(this.platformId) || this.initialized) {
      return;
    }

    const missingVariable = !apiKey
      ? 'NG_APP_POSTHOG_PROJECT_TOKEN'
      : !options.api_host
        ? 'NG_APP_POSTHOG_HOST'
        : null;

    if (missingVariable) {
      if (!production) {
        console.error(
          `${missingVariable} variable required by PostHog is missing or un-configured, this causes events to be silently missed. This error stops appearing once ${missingVariable} is configured`,
        );
      }
      return;
    }

    posthog.init(apiKey, options);
    this.initialized = true;
  }
}
