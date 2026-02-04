import { Box, Button, Stack, Text } from '@chakra-ui/react';
import type React from 'react';
import { ApiError } from '../../api/errors';
import { SvgIllustration, type SvgIllustrationKey } from '../../ui/svg-illustration';

export interface ErrorScreenProps {
    error?: unknown;
    message?: string;
    onRetry?: () => void;
    title?: string;
    illustration?: SvgIllustrationKey;
}

export const ErrorScreen: React.FC<ErrorScreenProps> = ({
    error,
    message,
    title,
    onRetry,
    illustration,
}) => {
    const getErrorMessage = (): string => {
        if (message) {
            return message;
        }

        if (error instanceof ApiError) {
            return error.getErrorMessage();
        }

        if (error instanceof Error) {
            return error.message;
        }

        if (typeof error === 'string') {
            return error;
        }

        return 'Failed to initialize session. Please try again later.';
    };

    const getErrorTitle = (): string => {
        if (error instanceof ApiError) {
            if (error.isAuthError()) {
                return 'Authentication Failed';
            }
            if (error.isServerError()) {
                return 'Server Error';
            }
            if (error.isClientError()) {
                return 'Request Failed';
            }
        }
        return 'Initialization Error';
    };

    const errorMessage = getErrorMessage();
    const errorTitle = getErrorTitle();

    const inferredIllustration: SvgIllustrationKey = (() => {
        if (illustration) return illustration;
        if (title?.toLowerCase().includes('not found')) return 'notFound';
        if (title?.toLowerCase().includes('expired')) return 'cancel';
        if (title?.toLowerCase().includes('restricted')) return 'accessDenied';

        if (error instanceof ApiError) {
            if (error.isAuthError() || error.isForbiddenError()) return 'accessDenied';
            if (error.isNotFoundError()) return 'notFound';
            if (error.isNetworkError()) return 'serverDown';
            if (error.isServerError()) return 'serverError';
            if (error.isClientError()) return 'warnings';
        }

        return 'warnings';
    })();

    return (
        <Box
            _dark={{ bg: 'gray.900' }}
            alignItems='center'
            bg='white'
            className='page-transition'
            display='flex'
            height='100vh'
            justifyContent='center'
            left='0'
            position='fixed'
            top='0'
            width='100vw'
            zIndex='9999'
        >
            <Stack align='center' direction='column' gap={4} maxW='md' px={6} textAlign='center'>
                <SvgIllustration
                    illustration={inferredIllustration}
                    alt={title || errorTitle || 'Error'}
                />
                <Text
                    _dark={{ color: 'gray.100' }}
                    color='gray.800'
                    fontSize='xl'
                    fontWeight='semibold'
                >
                    {title || errorTitle}
                </Text>
                <Text _dark={{ color: 'gray.400' }} color='gray.600' fontSize='md'>
                    {errorMessage}
                </Text>
                {onRetry && (
                    <Button mt={4} onClick={onRetry} size='md' variant='solid'>
                        Retry
                    </Button>
                )}
            </Stack>
        </Box>
    );
};
