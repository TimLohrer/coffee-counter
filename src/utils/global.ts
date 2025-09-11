import { getCurrent, onOpenUrl } from '@tauri-apps/plugin-deep-link';
import { writable } from 'svelte/store';

export const STARTUP_COMPLETED = writable(false);

export async function startup() {
    console.debug('App starting up...');

    // DEEP LINKS
    
    const startUrls = await getCurrent();
    console.debug(`Startup deep link URLs: ${startUrls}`);
    if (startUrls) {
        parseDeepLink(startUrls[0]);
    }
    
    await onOpenUrl((urls) => {
        console.debug(`Deep link URLs: ${urls}`);
        parseDeepLink(urls[0]);
    });

    STARTUP_COMPLETED.set(true);
}


function parseDeepLink(url: string) {
    console.debug(`Deep link URL: ${url}`);
    const pathElements = url.split('://')[1].split('?')[0].split('/');
    console.debug(`Path elements: ${pathElements}`);
    const machineId = pathElements[0];
    const isFree = pathElements[1] === 'free';

    window.location.href = `/home?machineId=${machineId}&isFree=${isFree}&at=${Date.now()}`;
}