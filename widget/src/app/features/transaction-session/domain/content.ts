export function isHtmlContent(value: unknown): value is string {
    if (typeof value !== 'string') return false;
    const v = value.trimStart();
    return v.startsWith('<!DOCTYPE') || v.startsWith('<html') || v.startsWith('<');
}
