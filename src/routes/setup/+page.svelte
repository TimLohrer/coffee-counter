<script lang="ts">
    import { invoke } from "@tauri-apps/api/core";
    import Spinner from "../../components/Spinner.svelte";
    import { onMount } from "svelte";

    let backendUrl: string = "";
    let isConnected: boolean | null = null;

    async function setBackendUrl() {
        try {
            if (backendUrl.trim() === "") {
                isConnected = null;
                return;
            }
            if (backendUrl.endsWith("/")) {
                backendUrl = backendUrl.slice(0, -1);
            }
            isConnected = await invoke("set_backend_url", { url: backendUrl + "/api/v1" });
            if (isConnected) {
                setTimeout(() => {
                    window.location.href = "/login";
                }, 2 * 1000);
            }
        } catch (error) {
            console.error("Error setting backend URL:", error);
            isConnected = false;
        }
    }

    onMount(() => {
        document.getElementById("backendUrl")?.focus();
    });
</script>

<main class="container">
    <p class="title">Setup Backend URL</p>
    
    {#if isConnected !== true}
        <div class="input-wrapper">
            <input
                type="text"
                bind:value={backendUrl}
                on:change={setBackendUrl}
                placeholder="https://coffee-counter.example.com"
                class="input"
            />
            <button on:click={setBackendUrl} class="button">Connect</button>
        </div>
        {#if isConnected === false}
            <p class="status">❌ Connection failed!</p>
        {/if}
    {/if}
    {#if isConnected === true}
        <p class="status success">✅ Successfully connected!</p>
        <Spinner class="loading" size="25px" />
    {/if}
</main>

<style>
    .container {
        max-width: 100%;
        max-height: 100vh;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 0.5rem;
    }

    .title {
        font-size: 2.25rem;
        font-weight: bold;
        margin-top: 15rem;
    }

    .input-wrapper {
        display: flex;
        flex-direction: row;
        gap: 3.5px;
        margin-top: 3rem;
        width: 100%;
    }

    .input {
        width: 85vw;
        padding: 0.5rem;
        margin-top: 0.5rem;
        border: 2px solid #ccc;
        border-radius: 7.5px;
        font-size: 1rem;
    }

    .input:focus {
        outline: none;
        border-color: #007bff;
    }

    .button {
        padding: 0 0.75rem;
        font-size: 1rem;
        font-weight: bold;
        margin-top: 0.5rem;

        background-color: #007bff;
        color: white;
        border: none;
        border-radius: 7.5px;
        cursor: pointer;
    }

    .status {
        margin-top: 1rem;
        font-size: 1.25rem;
        font-weight: bold;
    }

    .success {
        margin-top: 5rem;
        margin-bottom: 2.5rem;
        font-size: 1.5rem;
    }
</style>