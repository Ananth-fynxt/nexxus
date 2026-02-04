// Method 2: Alternative approach using recipes (Chakra UI v3 preferred method)
import {
    createSystem,
    defaultConfig,
    defineConfig,
    defineStyle,
    mergeConfigs,
} from '@chakra-ui/react';
import { buttonRecipe } from './button.recipe';

const headingRecipe = defineStyle({
    base: {
        fontFamily: 'heading',
        color: 'heading',
        fontWeight: 'semibold',
    },
});

const config = defineConfig({
    cssVarsRoot: ':where(:root, :host)',
    cssVarsPrefix: 'ck',
    globalCss: {
        '#widget-root': {
            margin: 0,
            padding: 0,
            fontFamily: 'body',
            lineHeight: 'base',
            bg: 'bg',
            color: 'fg',
            transition: 'background-color 0.2s, color 0.2s',
        },
        'html[dir="rtl"]': {
            direction: 'rtl',
        },
        'html[dir="ltr"]': {
            direction: 'ltr',
        },
        '*': {
            borderColor: 'border',
        },
        '*, *::before, *::after': {
            boxSizing: 'border-box',
        },
        // Ensure password inputs show dots properly
        'input[type="password"]': {
            fontFamily: 'monospace !important',
            letterSpacing: '0.1em',
            fontSize: '1.2em',
            '&::placeholder': {
                fontFamily: 'body !important',
                letterSpacing: 'normal',
                fontSize: 'inherit',
            },
        },
    },
    theme: {
        recipes: {
            button: buttonRecipe,
            heading: headingRecipe,
        },
        tokens: {
            fonts: {
                heading: { value: 'system-ui, sans-serif' },
                body: { value: 'system-ui, sans-serif' },
                mono: {
                    value: 'ui-monospace, SFMono-Regular, "SF Mono", Monaco, Consolas, "Liberation Mono", "Courier New", monospace',
                },
                accent: { value: 'system-ui, sans-serif' },
            },

            fontSizes: {
                // Display Sizes
                'display-xl': { value: '4.5rem' }, // 72px
                'display-l': { value: '3.75rem' }, // 60px
                'display-m': { value: '3rem' }, // 48px
                'display-s': { value: '2.25rem' }, // 36px
                'display-xs': { value: '1.875rem' }, // 30px

                // Text Sizes
                'text-l': { value: '1.125rem' }, // 18px
                'text-m': { value: '1rem' }, // 16px
                'text-s': { value: '0.875rem' }, // 14px
                'text-xs': { value: '0.75rem' }, // 12px

                // Button Sizes
                'button-l': { value: '1rem' }, // 16px
                'button-m': { value: '0.875rem' }, // 14px
                'button-s': { value: '0.75rem' }, // 12px

                // Caption
                caption: { value: '0.75rem' }, // 12px
            },

            fontWeights: {
                light: { value: '300' },
                regular: { value: '400' },
                medium: { value: '500' },
                'semi-bold': { value: '600' },
                bold: { value: '700' },
            },

            lineHeights: {
                // Display line heights (tight for large text)
                'display-xl': { value: '1.1' },
                'display-l': { value: '1.1' },
                'display-m': { value: '1.2' },
                'display-s': { value: '1.2' },
                'display-xs': { value: '1.3' },

                // Text line heights (comfortable for reading)
                'text-l': { value: '1.6' },
                'text-m': { value: '1.5' },
                'text-s': { value: '1.4' },
                'text-xs': { value: '1.4' },

                // Button line heights
                button: { value: '1.2' },

                // Caption line height
                caption: { value: '1.3' },
            },

            colors: {
                // Primary Deep Blue Palette
                primary: {
                    50: { value: '#f6f9ff' },
                    100: { value: '#ecf2ff' },
                    200: { value: '#dde9ff' },
                    300: { value: '#c6daff' },
                    400: { value: '#a7c6ff' },
                    500: { value: '#70a3ff' },
                    600: { value: '#528fff' },
                    700: { value: '#2d77ff' },
                    800: { value: '#1165ff' },
                    900: { value: '#0040b3' },
                    950: { value: '#002666' },
                },
                // Secondary Light Blue Palette
                secondary: {
                    50: { value: '#fafdff' },
                    100: { value: '#f0f9ff' },
                    200: { value: '#e4f4fe' },
                    300: { value: '#caeaff' },
                    400: { value: '#b2e0ff' },
                    500: { value: '#7acaff' },
                    600: { value: '#51b9ff' },
                    700: { value: '#0099ff' },
                    800: { value: '#0874c5' },
                    900: { value: '#0d629b' },
                    950: { value: '#0e3b5d' },
                },
                // Success Green Palette
                success: {
                    50: { value: '#f0fdf4' },
                    100: { value: '#dcfce7' },
                    200: { value: '#bbf7d0' },
                    300: { value: '#86efac' },
                    400: { value: '#4ade80' },
                    500: { value: '#22c55e' },
                    600: { value: '#16a34a' },
                    700: { value: '#15803d' },
                    800: { value: '#166534' },
                    900: { value: '#14532d' },
                    950: { value: '#052e16' },
                },
                // Warning Amber Palette
                warning: {
                    50: { value: '#fffbeb' },
                    100: { value: '#fef3c7' },
                    200: { value: '#fde68a' },
                    300: { value: '#fcd34d' },
                    400: { value: '#fbbf24' },
                    500: { value: '#f59e0b' },
                    600: { value: '#d97706' },
                    700: { value: '#b45309' },
                    800: { value: '#92400e' },
                    900: { value: '#78350f' },
                    950: { value: '#451a03' },
                },
                // Error Red Palette
                error: {
                    50: { value: '#fef2f2' },
                    100: { value: '#fee2e2' },
                    200: { value: '#fecaca' },
                    300: { value: '#fca5a5' },
                    400: { value: '#f87171' },
                    500: { value: '#ef4444' },
                    600: { value: '#dc2626' },
                    700: { value: '#b91c1c' },
                    800: { value: '#991b1b' },
                    900: { value: '#7f1d1d' },
                    950: { value: '#450a0a' },
                },
                // Neutral Gray Palette
                neutral: {
                    10: { value: '#ffffff' },
                    50: { value: '#fafafa' },
                    100: { value: '#f4f4f5' },
                    200: { value: '#e4e4e7' },
                    300: { value: '#d4d4d8' },
                    400: { value: '#c8c8c8' },
                    500: { value: '#71717a' },
                    600: { value: '#585858' },
                    700: { value: '#3f3f46' },
                    800: { value: '#27272a' },
                    900: { value: '#18181b' },
                    950: { value: '#09090b' },
                },
            },
        },
        semanticTokens: {
            colors: {
                // Theme-aware color system
                bg: {
                    value: { base: '{colors.neutral.10}', _dark: '{colors.neutral.900}' },
                },
                fg: {
                    value: { base: '{colors.neutral.900}', _dark: '{colors.neutral.50}' },
                },
                'fg.muted': {
                    value: {
                        base: '{colors.neutral.600}',
                        _dark: '{colors.neutral.400}',
                    },
                },
                'fg.subtle': {
                    value: {
                        base: '{colors.neutral.500}',
                        _dark: '{colors.neutral.500}',
                    },
                },
                'fg.error': {
                    value: {
                        base: '{colors.error.600}',
                        _dark: '{colors.error.400}',
                    },
                },
                'fg.warning': {
                    value: {
                        base: '{colors.warning.600}',
                        _dark: '{colors.warning.400}',
                    },
                },
                'fg.success': {
                    value: {
                        base: '{colors.success.600}',
                        _dark: '{colors.success.400}',
                    },
                },
                'fg.info': {
                    value: {
                        base: '{colors.info.600}',
                        _dark: '{colors.info.400}',
                    },
                },
                'fg.inverted': {
                    value: {
                        base: '{colors.neutral.50}',
                        _dark: '{colors.neutral.900}',
                    },
                },

                border: {
                    value: {
                        base: '{colors.neutral.200}',
                        _dark: '{colors.neutral.700}',
                    },
                },
                'border.inverted': {
                    value: {
                        base: '{colors.neutral.800}',
                        _dark: '{colors.neutral.200}',
                    },
                },
                'border.muted': {
                    value: {
                        base: '{colors.neutral.400}',
                        _dark: '{colors.neutral.800}',
                    },
                },
                'border.emphasized': {
                    value: {
                        base: '{colors.neutral.200}',
                        _dark: '{colors.neutral.800}',
                    },
                },
                'border.error': {
                    value: {
                        base: '{colors.error.500}',
                        _dark: '{colors.error.800}',
                    },
                },
                'border.subtle': {
                    value: {
                        base: '{colors.neutral.100}',
                        _dark: '{colors.neutral.800}',
                    },
                },
                'bg.subtle': {
                    value: {
                        base: '{colors.neutral.50}',
                        _dark: '{colors.neutral.800}',
                    },
                },
                'bg.panel': {
                    value: {
                        base: '{colors.neutral.50}',
                        _dark: '{colors.neutral.900}',
                    },
                },
                'bg.muted': {
                    value: { base: '{colors.white}', _dark: '{colors.neutral.800}' },
                },
                'bg.emphasized': {
                    value: {
                        base: '{colors.neutral.100}',
                        _dark: '{colors.neutral.700}',
                    },
                },
                'bg.inverted': {
                    value: { base: '{colors.neutral.900}', _dark: '{colors.neutral.50}' },
                },

                // Primary color variants
                primary: {
                    value: {
                        base: '{colors.primary.950}',
                        _dark: '{colors.primary.50}',
                    },
                },
                'primary.solid': {
                    value: {
                        base: '{colors.primary.950}',
                        _dark: '{colors.primary.50}',
                    },
                },
                'primary.contrast': {
                    value: { base: '{colors.white}', _dark: '{colors.neutral.900}' },
                },
                'primary.fg': {
                    value: {
                        base: '{colors.primary.600}',
                        _dark: '{colors.primary.400}',
                    },
                },
                'primary.muted': {
                    value: { base: '{colors.primary.50}', _dark: '{colors.primary.950}' },
                },
                'primary.subtle': {
                    value: {
                        base: '{colors.primary.100}',
                        _dark: '{colors.primary.900}',
                    },
                },
                'primary.emphasized': {
                    value: {
                        base: '{colors.primary.200}',
                        _dark: '{colors.primary.800}',
                    },
                },
                'primary.focusRing': {
                    value: {
                        base: '{colors.primary.600}',
                        _dark: '{colors.primary.500}',
                    },
                },
                'primary.bg': {
                    value: {
                        base: '{colors.primary.50}',
                        _dark: '{colors.primary.950}',
                    },
                },
                'primary.border': {
                    value: {
                        base: '{colors.primary.300}',
                        _dark: '{colors.primary.800}',
                    },
                },

                // Secondary color variants
                'secondary.solid': {
                    value: {
                        base: '{colors.secondary.700}',
                        _dark: '{colors.secondary.500}',
                    },
                },
                'secondary.contrast': {
                    value: { base: '{colors.white}', _dark: '{colors.neutral.900}' },
                },
                'secondary.fg': {
                    value: {
                        base: '{colors.secondary.600}',
                        _dark: '{colors.secondary.400}',
                    },
                },
                'secondary.muted': {
                    value: {
                        base: '{colors.secondary.50}',
                        _dark: '{colors.secondary.950}',
                    },
                },
                'secondary.subtle': {
                    value: {
                        base: '{colors.secondary.100}',
                        _dark: '{colors.secondary.900}',
                    },
                },
                'secondary.emphasized': {
                    value: {
                        base: '{colors.secondary.200}',
                        _dark: '{colors.secondary.800}',
                    },
                },
                'secondary.focusRing': {
                    value: {
                        base: '{colors.secondary.600}',
                        _dark: '{colors.secondary.500}',
                    },
                },
                'secondary.bg': {
                    value: {
                        base: '{colors.secondary.50}',
                        _dark: '{colors.secondary.950}',
                    },
                },
                'secondary.border': {
                    value: {
                        base: '{colors.secondary.300}',
                        _dark: '{colors.secondary.800}',
                    },
                },

                // Success color variants
                'success.solid': {
                    value: {
                        base: '{colors.success.600}',
                        _dark: '{colors.success.500}',
                    },
                },
                'success.contrast': {
                    value: { base: '{colors.white}', _dark: '{colors.neutral.900}' },
                },
                'success.fg': {
                    value: {
                        base: '{colors.success.600}',
                        _dark: '{colors.success.400}',
                    },
                },
                'success.muted': {
                    value: { base: '{colors.success.50}', _dark: '{colors.success.950}' },
                },
                'success.subtle': {
                    value: {
                        base: '{colors.success.100}',
                        _dark: '{colors.success.900}',
                    },
                },
                'success.emphasized': {
                    value: {
                        base: '{colors.success.200}',
                        _dark: '{colors.success.800}',
                    },
                },
                'success.focusRing': {
                    value: {
                        base: '{colors.success.600}',
                        _dark: '{colors.success.500}',
                    },
                },

                // Warning color variants
                'warning.solid': {
                    value: {
                        base: '{colors.warning.500}',
                        _dark: '{colors.warning.500}',
                    },
                },
                'warning.contrast': {
                    value: {
                        base: '{colors.neutral.900}',
                        _dark: '{colors.neutral.900}',
                    },
                },
                'warning.fg': {
                    value: {
                        base: '{colors.warning.600}',
                        _dark: '{colors.warning.400}',
                    },
                },
                'warning.muted': {
                    value: { base: '{colors.warning.50}', _dark: '{colors.warning.950}' },
                },
                'warning.subtle': {
                    value: {
                        base: '{colors.warning.100}',
                        _dark: '{colors.warning.900}',
                    },
                },
                'warning.emphasized': {
                    value: {
                        base: '{colors.warning.200}',
                        _dark: '{colors.warning.800}',
                    },
                },
                'warning.focusRing': {
                    value: {
                        base: '{colors.warning.500}',
                        _dark: '{colors.warning.500}',
                    },
                },

                // Error color variants
                'error.solid': {
                    value: { base: '{colors.error.600}', _dark: '{colors.error.500}' },
                },
                'error.contrast': {
                    value: { base: '{colors.white}', _dark: '{colors.neutral.900}' },
                },
                'error.fg': {
                    value: { base: '{colors.error.600}', _dark: '{colors.error.400}' },
                },
                'error.muted': {
                    value: { base: '{colors.error.50}', _dark: '{colors.error.950}' },
                },
                'error.subtle': {
                    value: { base: '{colors.error.100}', _dark: '{colors.error.900}' },
                },
                'error.emphasized': {
                    value: { base: '{colors.error.200}', _dark: '{colors.error.800}' },
                },
                'error.focusRing': {
                    value: { base: '{colors.error.600}', _dark: '{colors.error.500}' },
                },

                //heading
                heading: {
                    value: {
                        base: '{colors.neutral.600}',
                        _dark: '{colors.neutral.50}',
                    },
                },
            },
        },
    },
});
const systemTheme = mergeConfigs(defaultConfig, config);
export const system = createSystem(systemTheme);
