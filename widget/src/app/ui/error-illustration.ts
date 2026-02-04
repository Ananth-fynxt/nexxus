import { ApiError } from '../api/errors';
import type { SvgIllustrationKey } from './svg-illustration';

export function getIllustrationForError(error: unknown): SvgIllustrationKey {
    if (error instanceof ApiError) {
        if (error.isAuthError() || error.isForbiddenError()) return 'accessDenied';
        if (error.isNotFoundError()) return 'notFound';
        if (error.isNetworkError()) {
            if (typeof navigator !== 'undefined' && navigator.onLine === false)
                return 'connectionLost';
            return 'serverDown';
        }
        if (error.isServerError()) return 'serverError';
        if (error.isClientError()) return 'warnings';
    }

    return 'warnings';
}
