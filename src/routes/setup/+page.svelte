<script lang="ts">
    import { invoke } from "@tauri-apps/api/core";

    let backendUrl: string = "";
    let isConnected: boolean | null = null;

    async function setBackendUrl() {
        try {
            isConnected = await invoke("set_backend_url", { url: backendUrl });
        } catch (error) {
            console.error("Error setting backend URL:", error);
            isConnected = false;
        }
    }
</script>

<main class="container">
    <h1>Setup Backend URL</h1>
    <input
        type="text"
        bind:value={backendUrl}
        placeholder="Enter backend URLL"
        class="input"
    />
    <button on:click={setBackendUrl} class="button">Connect</button>
    <button on:click={() => window.location.href = "/setup"} class="button">reload</button>

    {#if isConnected === true}
        <p class="success">Successfully connected to the backend!</p>
    {:else if isConnected === false}
        <p class="error">Failed to connect to the backend. Please check the URL and try again.</p>
    {/if}
</main>

<style>
</style>