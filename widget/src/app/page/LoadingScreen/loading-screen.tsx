import { Box, Stack, Text } from '@chakra-ui/react';
import type React from 'react';
import { SvgIllustration } from '../../ui/svg-illustration';

export interface LoadingScreenProps {
    message?: string;
}

export const LoadingScreen: React.FC<LoadingScreenProps> = ({ message = 'Loading...' }) => {
    return (
        <Box
            _dark={{ bg: 'gray.900' }}
            alignItems='center'
            bg='white'
            display='flex'
            height='100vh'
            justifyContent='center'
            left='0'
            position='fixed'
            top='0'
            width='100vw'
            zIndex='9999'
        >
            <Stack align='center' direction='column' gap={4}>
                <SvgIllustration illustration='loading' alt='Loading' />
                <Text
                    _dark={{ color: 'gray.300' }}
                    color='gray.600'
                    fontSize='lg'
                    fontWeight='medium'
                >
                    {message}
                </Text>
            </Stack>
        </Box>
    );
};
