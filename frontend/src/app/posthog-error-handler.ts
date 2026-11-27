import { ErrorHandler, Injectable, inject } from '@angular/core';
import { PostHogService } from './services/posthog.service';

@Injectable({ providedIn: 'root' })
export class PostHogErrorHandler implements ErrorHandler {
  private readonly posthogService = inject(PostHogService);

  handleError(error: unknown): void {
    const exception = this.unwrapError(error);

    this.posthogService.posthog.captureException(exception);
    console.error(error);
  }

  private unwrapError(error: unknown): Error | string {
    if (
      error !== null &&
      typeof error === 'object' &&
      'ngOriginalError' in error &&
      (error as { ngOriginalError?: unknown }).ngOriginalError
    ) {
      return this.unwrapError((error as { ngOriginalError: unknown }).ngOriginalError);
    }

    if (error instanceof Error || typeof error === 'string') {
      return error;
    }

    return new Error('Unknown error');
  }
}
