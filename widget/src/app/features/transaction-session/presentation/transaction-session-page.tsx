import { ErrorScreen } from '../../../page/ErrorScreen';
import { LoadingScreen } from '../../../page/LoadingScreen';
import { SurveillanceOverlay } from './components/surveillance-overlay';
import { useSessionToken } from './hooks/use-session-token';
import { useTransactionSessionController } from './hooks/use-transaction-session-controller';
import { TransactionSessionReady } from './transaction-session-ready';
import type React from 'react';

export function TransactionSessionPage() {
    const sessionToken = useSessionToken();
    const controller = useTransactionSessionController(sessionToken);

    let content: React.ReactNode;
    switch (controller.state.kind) {
        case 'securityIncident':
            content = (
                <ErrorScreen
                    title='Security Alert'
                    illustration='dataThief'
                    message='Suspicious activity detected. This session has been closed for your safety.'
                />
            );
            break;
        case 'securityRisk':
            content = (
                <ErrorScreen
                    title='Security Notice'
                    illustration='security'
                    message='We could not continue due to a security validation failure.'
                />
            );
            break;
        case 'missingToken':
            content = (
                <ErrorScreen
                    title='Transaction Not Found'
                    illustration='notFound'
                    message='Missing session token in URL.'
                />
            );
            break;
        case 'redirecting':
            content = <LoadingScreen message='Redirecting…' />;
            break;
        case 'loadingSession':
            content = <LoadingScreen message='Loading transaction session…' />;
            break;
        case 'fetchError':
            content = (
                <ErrorScreen
                    title={controller.state.title}
                    illustration={controller.state.illustration}
                    error={controller.state.error}
                    message='Unable to load transaction session.'
                />
            );
            break;
        case 'gone':
            content = (
                <ErrorScreen
                    title='Session Expired'
                    illustration='cancel'
                    message='This transaction session has expired. Please start again.'
                />
            );
            break;
        case 'notFound':
            content = (
                <ErrorScreen
                    title='Transaction Not Found'
                    illustration='notFound'
                    message='We could not find this transaction session.'
                />
            );
            break;
        case 'noData':
            content = (
                <ErrorScreen
                    title='No Data'
                    illustration='noData'
                    message='Transaction data is not available at the moment.'
                />
            );
            break;
        case 'missingTxnData':
            content = (
                <ErrorScreen
                    title='Transaction Not Found'
                    illustration='notFound'
                    message='Session response is missing txnData.'
                />
            );
            break;
        case 'txnFailure':
            content = (
                <ErrorScreen
                    title={controller.state.title}
                    illustration={controller.state.illustration}
                    message={controller.state.message}
                />
            );
            break;
        case 'ready': {
            const navigation = controller.state.navigation;
            content = (
                <TransactionSessionReady
                    statusLabel={controller.state.statusLabel}
                    navigation={navigation}
                    isProcessing={controller.isProcessing}
                    onOpenPayment={() => controller.openPayment(navigation)}
                    iframe={controller.iframe}
                    onCloseIframe={() => controller.setIframeOpen(false)}
                />
            );
            break;
        }
        default:
            content = (
                <ErrorScreen
                    title='Unknown State'
                    illustration='warnings'
                    message='Something went wrong. Please try again.'
                />
            );
    }

    return (
        <>
            <SurveillanceOverlay isOpen={controller.showSurveillance} />
            {content}
        </>
    );
}
