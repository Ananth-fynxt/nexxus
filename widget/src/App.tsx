import { TransactionSessionApp } from './app/features/transaction-session';
import { IllustrationGallery } from './app/dev/illustration-gallery';

function App() {
    if (import.meta.env.DEV) {
        const gallery = new URLSearchParams(window.location.search).get('gallery');
        if (gallery === '1') {
            return <IllustrationGallery />;
        }
    }
    return <TransactionSessionApp />;
}

export default App;
