import { invoke } from "@tauri-apps/api/core";
import { writable } from "svelte/store";
import type AppData from "../models/AppData";

export const APP_DATA = writable(<AppData | null>(null));

export async function loadAppData() {
    try {
        const data: AppData = await invoke("get_app_data");
        APP_DATA.set(data);
        if (!data.user || !data.token) {
            window.location.href = "/";
        }
    } catch (error) {
        console.error("Failed to load user data:", error);
        APP_DATA.set(null);
        window.location.href = "/";
    }
}

export async function logout() {
    try {
        await invoke("logout");
        await loadAppData();
    } catch (error) {
        console.error("Failed to logout:", error);
    }
}

export async function resetApp() {
    try {
        await invoke("reset");
        await loadAppData();
    } catch (error) {
        console.error("Failed to reset app:", error);
    }
}