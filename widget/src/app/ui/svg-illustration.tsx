import { Image } from '@chakra-ui/react';

export type SvgIllustrationKey =
    | 'accessDenied'
    | 'cancel'
    | 'connectionLost'
    | 'empty'
    | 'loading'
    | 'maintenance'
    | 'noData'
    | 'notFound'
    | 'processing'
    | 'security'
    | 'serverDown'
    | 'serverError'
    | 'success'
    | 'surveillance'
    | 'void'
    | 'warnings'
    | 'dataThief';

const SVG_PATHS: Record<SvgIllustrationKey, string> = {
    accessDenied: '/svg/undraw_access-denied_krem.svg',
    cancel: '/svg/undraw_cancel_7zdh.svg',
    connectionLost: '/svg/undraw_connection-lost_am29.svg',
    dataThief: '/svg/undraw_data-thief_d66l.svg',
    empty: '/svg/undraw_empty_4zx0.svg',
    loading: '/svg/undraw_loading_3kqt.svg',
    maintenance: '/svg/undraw_maintenance_4unj.svg',
    noData: '/svg/undraw_no-data_ig65.svg',
    notFound: '/svg/undraw_page-not-found_6wni.svg',
    processing: '/svg/undraw_processing_bto8.svg',
    security: '/svg/undraw_security_0ubl.svg',
    serverDown: '/svg/undraw_server-down_lxs9.svg',
    serverError: '/svg/undraw_server-error_syuz.svg',
    success: '/svg/undraw_success_288d.svg',
    surveillance: '/svg/undraw_surveillance_k6wl.svg',
    void: '/svg/undraw_void_wez2.svg',
    warnings: '/svg/undraw_warnings_agxg.svg',
};

type Props = {
    illustration: SvgIllustrationKey;
    alt: string;
    size?: number;
};

export function SvgIllustration({ illustration, alt, size = 260 }: Props) {
    return (
        <Image
            src={SVG_PATHS[illustration]}
            alt={alt}
            maxW={`${size}px`}
            w='full'
            h='auto'
            mx='auto'
        />
    );
}
