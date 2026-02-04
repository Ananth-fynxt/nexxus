function isLocalhost(hostname: string): boolean {
    return hostname === 'localhost' || hostname === '127.0.0.1';
}

export function isLikelySessionToken(token: string): boolean {
    // Prevent obvious injection/abuse and keep URL size reasonable.
    if (token.length < 8) return false;
    if (token.length > 2048) return false;
    return /^[A-Za-z0-9_-]+$/.test(token);
}

export function isSafeExternalUrl(raw: string): boolean {
    try {
        const url = new URL(raw);
        if (url.protocol === 'https:') return true;
        if (url.protocol === 'http:') return isLocalhost(url.hostname);
        return false;
    } catch {
        return false;
    }
}
