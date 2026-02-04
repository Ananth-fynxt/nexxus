import type { SvgIllustrationKey } from './svg-illustration';

export type FintechScenario =
    | 'loading'
    | 'processing'
    | 'success'
    | 'transactionNotFound'
    | 'sessionExpired'
    | 'accessDenied'
    | 'securityRisk'
    | 'connectionLost'
    | 'serverDown'
    | 'serverError'
    | 'warning'
    | 'noData'
    | 'maintenance'
    | 'void';

export const FINTECH_SCENARIO_ILLUSTRATION: Record<FintechScenario, SvgIllustrationKey> = {
    loading: 'loading',
    processing: 'processing',
    success: 'success',
    transactionNotFound: 'notFound',
    sessionExpired: 'cancel',
    accessDenied: 'accessDenied',
    securityRisk: 'security',
    connectionLost: 'connectionLost',
    serverDown: 'serverDown',
    serverError: 'serverError',
    warning: 'warnings',
    noData: 'noData',
    maintenance: 'maintenance',
    void: 'void',
};
