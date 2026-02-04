import { Badge, Box, Heading, Text, VStack } from '@chakra-ui/react';
import type { Navigation } from '../domain/types';
import { IframePayment } from './components/iframe-payment';
import { NavigationCta } from './components/navigation-cta';
import { ProcessingMessage } from './components/processing-message';

type Props = {
    statusLabel: string;
    navigation: Navigation | undefined;
    isProcessing: boolean;
    onOpenPayment: () => void;
    iframe: { isOpen: boolean; url: string; isHtml: boolean };
    onCloseIframe: () => void;
};

export function TransactionSessionReady({
    statusLabel,
    navigation,
    isProcessing,
    onOpenPayment,
    iframe,
    onCloseIframe,
}: Props) {
    return (
        <>
            <Box bg='white' minH='100vh' p={6}>
                <VStack align='stretch' gap={6} maxW='2xl' mx='auto'>
                    <VStack align='stretch' gap={2}>
                        <Heading size='lg' color='gray.800'>
                            Nexxus Transaction
                        </Heading>
                        <Text color='gray.600' fontSize='sm'>
                            Secure transaction session
                        </Text>
                    </VStack>

                    <Box border='1px solid' borderColor='gray.200' borderRadius='lg' p={5}>
                        <VStack align='stretch' gap={4}>
                            <VStack align='stretch' gap={2}>
                                <Text color='gray.600' fontSize='sm'>
                                    Status
                                </Text>
                                <Badge
                                    w='fit-content'
                                    colorPalette={
                                        statusLabel === 'success'
                                            ? 'green'
                                            : statusLabel === 'failed'
                                              ? 'red'
                                              : 'blue'
                                    }
                                >
                                    {statusLabel}
                                </Badge>
                            </VStack>

                            {isProcessing ? (
                                <ProcessingMessage />
                            ) : (
                                <NavigationCta
                                    navigation={navigation}
                                    onOpenPayment={onOpenPayment}
                                />
                            )}
                        </VStack>
                    </Box>
                </VStack>
            </Box>

            <IframePayment
                isOpen={iframe.isOpen}
                url={iframe.url}
                isHtmlContent={iframe.isHtml}
                onClose={onCloseIframe}
            />
        </>
    );
}
