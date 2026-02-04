import { apiClient } from '../../../api';
import { API_ENDPOINTS } from '../../../api/endpoints';
import type { ApiResponse } from '../../../api/types';
import type { TransactionSessionGateway } from '../application/ports';
import type { TransactionSession } from '../domain/types';

export class HttpTransactionSessionGateway implements TransactionSessionGateway {
    async fetch(sessionToken: string): Promise<ApiResponse<{ data: TransactionSession }>> {
        const url = API_ENDPOINTS.transactionSession.get(sessionToken);
        return apiClient.get<{ data: TransactionSession }>(url, { skipAuth: true });
    }
}
