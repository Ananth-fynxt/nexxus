import { useEffect, useMemo, useState } from 'react';
import { Box, HStack, Icon, IconButton, Text, VStack } from '@chakra-ui/react';
import { RefreshCw, X } from 'lucide-react';

type Props = {
    isOpen: boolean;
    url: string;
    isHtmlContent: boolean;
    onClose?: () => void;
};

export function IframePayment({ isOpen, url, isHtmlContent, onClose }: Props) {
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (!isOpen) return;
        setIsLoading(true);
    }, [isOpen, url, isHtmlContent]);

    const handleIframeLoad = () => {
        setIsLoading(false);
    };

    const handleClose = () => {
        setIsLoading(true);
        onClose?.();
    };

    const handleRefresh = () => {
        setIsLoading(true);
    };

    const iframeKey = useMemo(() => `${url}:${String(isHtmlContent)}`, [url, isHtmlContent]);

    if (!isOpen) return null;

    return (
        <Box
            position='fixed'
            top={0}
            left={0}
            right={0}
            bottom={0}
            bg='white'
            zIndex={9999}
            display='flex'
            flexDirection='column'
            overflow='hidden'
        >
            <HStack
                width='full'
                p={4}
                borderBottom='1px solid'
                borderColor='gray.200'
                justify='space-between'
                bg='white'
                zIndex={10}
            >
                <Text fontSize='lg' fontWeight='semibold' color='gray.800'>
                    Complete Payment
                </Text>
                <HStack gap={2}>
                    <IconButton
                        aria-label='Refresh'
                        size='sm'
                        variant='ghost'
                        onClick={handleRefresh}
                    >
                        <Icon as={RefreshCw} />
                    </IconButton>
                    <IconButton aria-label='Close' size='sm' variant='ghost' onClick={handleClose}>
                        <Icon as={X} />
                    </IconButton>
                </HStack>
            </HStack>

            {isLoading && (
                <Box
                    position='absolute'
                    top='50%'
                    left='50%'
                    transform='translate(-50%, -50%)'
                    zIndex={20}
                >
                    <VStack gap={3}>
                        <Box
                            width='40px'
                            height='40px'
                            border='4px solid'
                            borderColor='gray.200'
                            borderTopColor='blue.500'
                            borderRadius='50%'
                            animation='spin 1s linear infinite'
                        />
                        <Text fontSize='sm' color='gray.600'>
                            Loading payment page...
                        </Text>
                    </VStack>
                </Box>
            )}

            <Box width='full' flex={1} position='relative' overflow='hidden'>
                {isHtmlContent ? (
                    <iframe
                        key={`${iframeKey}:${isLoading ? 'l' : 'r'}`}
                        srcDoc={url}
                        style={{ width: '100%', height: '100%', border: 'none' }}
                        onLoad={handleIframeLoad}
                        sandbox='allow-forms allow-scripts allow-same-origin allow-popups allow-popups-to-escape-sandbox'
                        title='Payment Form'
                    />
                ) : (
                    <iframe
                        key={`${iframeKey}:${isLoading ? 'l' : 'r'}`}
                        src={url}
                        style={{ width: '100%', height: '100%', border: 'none' }}
                        onLoad={handleIframeLoad}
                        sandbox='allow-forms allow-scripts allow-same-origin allow-popups allow-popups-to-escape-sandbox allow-top-navigation'
                        title='Payment Page'
                    />
                )}
            </Box>

            <style>
                {`
                    @keyframes spin {
                        from { transform: rotate(0deg); }
                        to { transform: rotate(360deg); }
                    }
                `}
            </style>
        </Box>
    );
}
