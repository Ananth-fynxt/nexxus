import { isHtmlContent } from './content';
import type { Navigation } from './types';

export type PaymentRender = { isHtml: boolean; content: string } | null;

function readUrlFromValue(value: Navigation['value']): string | undefined {
    if (typeof value === 'string') return value;
    const url = value.url;
    return typeof url === 'string' ? url : undefined;
}

function readHtmlFromValue(value: Navigation['value']): string | undefined {
    if (typeof value === 'string') return value;
    const html = value.html;
    return typeof html === 'string' ? html : undefined;
}

function readStringFromValue(value: Navigation['value']): string | undefined {
    if (typeof value === 'string') return value;
    const v = value.value;
    return typeof v === 'string' ? v : undefined;
}

export function getRedirectUrl(navigation: Navigation): string | undefined {
    if (navigation.type !== 'redirect') return undefined;
    return readUrlFromValue(navigation.value);
}

export function getPaymentRenderFromNavigation(navigation: Navigation): PaymentRender {
    if (navigation.type !== 'iframe' && navigation.type !== 'form') return null;

    const url = readUrlFromValue(navigation.value);
    if (navigation.contentType === 'url' && url) {
        return { isHtml: false, content: url };
    }

    const html = readHtmlFromValue(navigation.value);
    if (navigation.contentType === 'html' && html) {
        return { isHtml: true, content: html };
    }

    const str =
        readStringFromValue(navigation.value) ??
        (typeof navigation.value === 'string' ? navigation.value : undefined);
    if (navigation.contentType === 'string' && str) {
        const trimmed = str.trim();
        if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
            return { isHtml: false, content: trimmed };
        }
        return { isHtml: isHtmlContent(trimmed), content: trimmed };
    }

    const fallback = html || url;
    if (!fallback) return null;
    return { isHtml: isHtmlContent(fallback), content: fallback };
}
