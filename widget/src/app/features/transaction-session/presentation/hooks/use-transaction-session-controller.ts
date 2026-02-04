import { useEffect, useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ApiError } from '../../../../api';
import { getIllustrationForError } from '../../../../ui/error-illustration';
import type { SvgIllustrationKey } from '../../../../ui/svg-illustration';
import { normalizeTxnPayload } from '../../domain/payload';
import { getPaymentRenderFromNavigation, getRedirectUrl } from '../../domain/navigation';
import { isLikelySessionToken, isSafeExternalUrl } from '../../domain/security';
import type { Navigation, TxnData } from '../../domain/types';
import { HttpTransactionSessionGateway } from '../../infrastructure/http-transaction-session-gateway';
import { useDevtoolsGuard } from './use-devtools-guard';
import { useSecurityLockdown, type SecurityIncident } from './use-security-lockdown';

type FailureUi = {
    title: string;
    illustration: SvgIllustrationKey;
    message: string;
};

function getFailureUi(txnData: TxnData): FailureUi {
    const failureMessage = (txnData.message || '').toLowerCase();
    const status = txnData.status;
    const illustration =
        status === 401 || status === 403 || failureMessage.includes('not allowed')
            ? 'accessDenied'
            : status === 503
              ? 'maintenance'
              : typeof status === 'number' && status >= 500
                ? 'serverError'
                : 'warnings';
    const title =
        illustration === 'accessDenied'
            ? 'Access Denied'
            : illustration === 'maintenance'
              ? 'Maintenance'
              : illustration === 'serverError'
                ? 'Server Error'
                : 'Transaction Failed';
    const message =
        illustration === 'accessDenied'
            ? 'Access denied. Please contact support or try again later.'
            : illustration === 'maintenance'
              ? 'Service is temporarily unavailable. Please try again later.'
              : illustration === 'serverError'
                ? 'We are unable to process your request right now. Please try again later.'
                : 'We could not process this transaction. Please try again later.';

    return { title, illustration, message };
}

function getErrorTitleFromIllustration(illustration: string, fallback: string): string {
    if (illustration === 'serverDown' || illustration === 'connectionLost') return 'Server Down';
    if (illustration === 'serverError') return 'Server Error';
    if (illustration === 'accessDenied') return 'Access Denied';
    return fallback;
}

type SessionEnvelope = {
    code?: string;
    message?: string;
    data?: unknown;
};

function readSessionEnvelope(raw: unknown): SessionEnvelope {
    if (typeof raw !== 'object' || raw === null) return {};
    const obj = raw as Record<string, unknown>;
    const code = typeof obj.code === 'string' ? obj.code : undefined;
    const message = typeof obj.message === 'string' ? obj.message : undefined;
    const data = obj.data;
    return { code, message, data };
}

export type TransactionSessionUiState =
    | { kind: 'securityIncident' }
    | { kind: 'securityRisk' }
    | { kind: 'missingToken' }
    | { kind: 'redirecting' }
    | { kind: 'loadingSession' }
    | { kind: 'fetchError'; title: string; illustration: SvgIllustrationKey; error: unknown }
    | { kind: 'gone' }
    | { kind: 'notFound' }
    | { kind: 'noData' }
    | { kind: 'missingTxnData' }
    | { kind: 'txnFailure'; title: string; illustration: SvgIllustrationKey; message: string }
    | {
          kind: 'ready';
          payload: ReturnType<typeof normalizeTxnPayload>;
          navigation: Navigation | undefined;
          statusLabel: string;
      };

type Return = {
    showSurveillance: boolean;
    iframe: { isOpen: boolean; url: string; isHtml: boolean };
    setIframeOpen: (open: boolean) => void;
    openPayment: (navigation: Navigation | undefined) => void;
    isProcessing: boolean;
    state: TransactionSessionUiState;
};

const gateway = new HttpTransactionSessionGateway();

export function useTransactionSessionController(sessionToken: string | null): Return {
    const showSurveillance = useDevtoolsGuard();

    const [iframeOpen, setIframeOpen] = useState(false);
    const [iframeUrl, setIframeUrl] = useState('');
    const [iframeIsHtml, setIframeIsHtml] = useState(false);
    const [isRedirecting, setIsRedirecting] = useState(false);
    const [securityIncident, setSecurityIncident] = useState<SecurityIncident | null>(null);
    const [securityRisk, setSecurityRisk] = useState(false);

    useSecurityLockdown(securityIncident);

    useEffect(() => {
        if (!sessionToken) return;
        if (!isLikelySessionToken(sessionToken)) {
            setSecurityIncident('dataThief');
        }
    }, [sessionToken]);

    const fetchQuery = useQuery({
        queryKey: ['transaction-session', 'fetch', sessionToken] as const,
        enabled: !!sessionToken,
        retry: false,
        queryFn: async () => {
            if (!sessionToken) throw new Error('Missing session token');
            return gateway.fetch(sessionToken);
        },
    });

    const envelope = readSessionEnvelope(fetchQuery.data?.data as unknown);
    const sessionCode = envelope.code;
    const sessionMessage = envelope.message;
    const sessionData =
        typeof envelope.data === 'object' && envelope.data !== null
            ? (envelope.data as { txnData?: unknown } | undefined)
            : undefined;

    const txnData = (sessionData?.txnData as TxnData | undefined) ?? undefined;
    const payload = useMemo(() => normalizeTxnPayload(txnData?.data), [txnData?.data]);

    const navigation: Navigation | undefined = useMemo(() => {
        if (!txnData) return undefined;
        if (txnData.__type === 'redirect' && txnData.url) {
            return { type: 'redirect', contentType: 'url', value: txnData.url };
        }
        return payload?.navigation;
    }, [txnData, payload?.navigation]);

    const isProcessing = useMemo(() => {
        const navigationType = navigation?.type;
        return navigationType === 'none' || navigationType === 'object' || !navigationType;
    }, [navigation?.type]);

    useEffect(() => {
        if (showSurveillance) return;
        if (!navigation) return;

        if (navigation.type === 'redirect') {
            if (navigation.contentType !== 'url') {
                setSecurityRisk(true);
                return;
            }
            const url = getRedirectUrl(navigation);
            if (!url) return;
            if (!isSafeExternalUrl(url)) {
                setSecurityIncident('dataThief');
                return;
            }
            setIsRedirecting(true);
            window.location.replace(url);
            return;
        }

        const render = getPaymentRenderFromNavigation(navigation);
        if (!render) return;
        if (!render.isHtml && !isSafeExternalUrl(render.content)) {
            setSecurityIncident('dataThief');
            return;
        }
        setIframeUrl(render.content);
        setIframeIsHtml(render.isHtml);
        setIframeOpen(true);
    }, [navigation, showSurveillance]);

    const openPayment = (nav: Navigation | undefined) => {
        if (!nav) return;
        const render = getPaymentRenderFromNavigation(nav);
        if (!render) return;
        setIframeUrl(render.content);
        setIframeIsHtml(render.isHtml);
        setIframeOpen(true);
    };

    const state: TransactionSessionUiState = useMemo(() => {
        if (securityIncident === 'dataThief') return { kind: 'securityIncident' };
        if (securityRisk) return { kind: 'securityRisk' };
        if (!sessionToken) return { kind: 'missingToken' };

        if (isRedirecting) return { kind: 'redirecting' };

        if (fetchQuery.isLoading) return { kind: 'loadingSession' };
        if (fetchQuery.isError) {
            if (fetchQuery.error instanceof ApiError) {
                if (fetchQuery.error.statusCode === 410) return { kind: 'gone' };
                if (fetchQuery.error.statusCode === 404) return { kind: 'notFound' };
            }
            const illustration = getIllustrationForError(fetchQuery.error);
            const title = getErrorTitleFromIllustration(illustration, 'Request Failed');
            return { kind: 'fetchError', title, illustration, error: fetchQuery.error };
        }

        if (sessionCode === '2071' || sessionMessage?.toLowerCase().includes('gone')) {
            return { kind: 'gone' };
        }
        if (sessionCode === '2070' || sessionMessage?.toLowerCase().includes('not found')) {
            return { kind: 'notFound' };
        }
        if (!sessionData) return { kind: 'noData' };
        if (!txnData) return { kind: 'missingTxnData' };

        if (txnData.__type === 'failure') {
            const ui = getFailureUi(txnData);
            return { kind: 'txnFailure', ...ui };
        }
        if (!payload) return { kind: 'noData' };

        const statusLabel = payload?.status || 'pending';
        return { kind: 'ready', payload, navigation, statusLabel };
    }, [
        securityIncident,
        securityRisk,
        sessionToken,
        isRedirecting,
        fetchQuery.isLoading,
        fetchQuery.isError,
        fetchQuery.error,
        sessionCode,
        sessionMessage,
        sessionData,
        txnData,
        payload,
        navigation,
    ]);

    return {
        showSurveillance,
        iframe: { isOpen: iframeOpen, url: iframeUrl, isHtml: iframeIsHtml },
        setIframeOpen,
        openPayment,
        isProcessing,
        state,
    };
}
