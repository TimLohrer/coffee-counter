<script lang="ts">
    import { invoke } from "@tauri-apps/api/core";
    import Spinner from "../../components/Spinner.svelte";

    let email = "";
    let password = "";
    let processing = false;

    async function login() {
        processing = true;
        try {
            
            const user = await invoke("login", { email, password });
            if (user) {
                console.info("Login successful:", user.user.email);
                window.location.href = "/";
            } else {
                console.error("Login failed.");
            }
        } catch (error) {
            console.error("Login error:", error);
        } finally {
            setTimeout(() => {
                processing = false;
            }, 2 * 1000);
        }
    }
</script>

<main class="container">
    <p class="title">Login</p>

    <input
        type="email"
        id="email"
        bind:value={email}
        on:change={() => document.getElementById('password')?.focus()}
        placeholder="Email"
        class="input"
    />
    <input
        type="password"
        id="password"
        bind:value={password}
        on:change={login}
        placeholder="Password"
        class="input"
    />
    <button on:click={login} class="button" class:disabled={processing} disabled={processing}>
        {#if processing}
            <Spinner size="20px" />
        {:else}
            Login
        {/if}
    </button>

    <div class="divider">
        <p>or</p>
        <hr>
    </div>

    <button on:click={() => window.location.href = "/signup"} class="button" disabled={processing}>
        Create Account
    </button>
</main>

<style>
    .container {
        max-width: 100%;
        max-height: 100vh;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 1rem;
    }

    .title {
        font-size: 2.25rem;
        font-weight: bold;
        margin-top: 15rem;
        margin-bottom: 5rem;
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
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem;
        font-size: 1rem;
        font-weight: bold;
        background-color: #007bff;
        color: white;
        border: none;
        border-radius: 7.5px;
        margin-top: 1rem;
        width: 90vw;
        cursor: pointer;
    }

    .button.disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .divider {
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        margin-top: 1rem;
        width: 45vw;
    }

    hr {
        flex-grow: 1;
        border: none;
        border-top: 1px solid white;
        width: 100%;
    }

    .divider p {
        margin: 0;
        font-size: 1rem;
        position: absolute;
        text-align: center;
        padding: 0 0.75rem;
        background-color: var(--background-color);
    }
</style>