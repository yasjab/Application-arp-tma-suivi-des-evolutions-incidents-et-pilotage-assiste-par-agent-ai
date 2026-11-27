export const environment = {
  production: import.meta.env.PROD,
  posthogKey: import.meta.env.NG_APP_POSTHOG_PROJECT_TOKEN || '',
  posthogHost: import.meta.env.NG_APP_POSTHOG_HOST || '',
};
