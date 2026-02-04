export type NavigationType = 'redirect' | 'iframe' | 'form' | 'none' | 'object';
export type NavigationContentType = 'url' | 'html' | 'string' | 'object';

export type Navigation = {
    type: NavigationType;
    contentType: NavigationContentType;
    value: string | Record<string, unknown>;
};

export type SessionTxnPayload = {
    navigation?: Navigation;
    status?: string;
    orderId?: string;
    userId?: string;
};

export type TxnData = {
    __type: 'success' | 'failure' | 'redirect';
    data?: SessionTxnPayload | { data?: SessionTxnPayload };
    url?: string;
    message?: string;
    status?: number;
};

export type TransactionSession = {
    txnId: string;
    txnData: TxnData;
};
