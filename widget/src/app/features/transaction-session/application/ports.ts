import type { ApiResponse } from '../../../api/types';
import type { TransactionSession } from '../domain/types';

export interface TransactionSessionGateway {
    fetch(sessionToken: string): Promise<ApiResponse<{ data: TransactionSession }>>;
}
