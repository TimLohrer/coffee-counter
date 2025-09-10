<script lang="ts">
    import { invoke } from "@tauri-apps/api/core";
    import Spinner from "../../components/Spinner.svelte";

    let name = "";
    let email = "";
    let password = "";
    let confirmPassword = "";
    let processing = false;

    function signUp() {
        processing = true;
        setTimeout(async () => {
            try {
                const success: boolean = await invoke("create_account", { email, name, password });
                if (success) {
                    window.location.href = "/login";
                } else {
                    console.error("SignUp failed.");
                }
            } catch (error) {
                console.error("SignUp error:", error);
            } finally {
                processing = false;
            }
        }, 2 * 1000);
    }
</script>

<main class="container">
    <p class="title">Create Account</p>

    <input
        type="text"
        id="name"
        bind:value={name}
        on:change={() => document.getElementById('email')?.focus()}
        placeholder="Your Name"
        class="input"
    />
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
        on:change={() => document.getElementById('password-confirm')?.focus()}
        placeholder="Password"
        class="input"
    />
    <input
        type="password"
        id="password-confirm"
        bind:value={confirmPassword}
        on:change={signUp}
        placeholder="Confirm Password"
        class="input"
    />
    {#if password !== confirmPassword && confirmPassword.length > 0}
        <p class="error">Passwords must match!</p>
    {/if}
    <button on:click={signUp} class="button" class:disabled={processing} disabled={processing}>
        {#if processing}
            <Spinner size="20px" />
        {:else}
            Create Account
        {/if}
    </button>

    <div class="divider">
        <p>or</p>
        <hr>
    </div>

    <button on:click={() => window.location.href = "/login"} class="button" disabled={processing}>
        Login
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

    .error {
        height: 10px;
        font-size: 14px;
        align-self: flex-start;
        margin-left: 1rem;
        margin-top: 0;
        color: red;
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