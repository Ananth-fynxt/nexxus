import type { SessionTxnPayload } from './types';

export function normalizeTxnPayload(
    raw: SessionTxnPayload | { data?: SessionTxnPayload } | undefined,
): SessionTxnPayload | undefined {
    if (!raw) return undefined;
    if (typeof raw === 'object' && 'data' in raw) {
        const wrapped = raw as { data?: SessionTxnPayload };
        return wrapped.data;
    }
    return raw as SessionTxnPayload;
}
