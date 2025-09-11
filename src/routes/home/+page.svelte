<script lang="ts">
    import { APP_DATA, loadAppData } from "../../utils/api";
    import { onMount } from "svelte";

    onMount(async () => {
        await loadAppData();
    });
</script>

<main class="container">
    <h1>Hallo {$APP_DATA?.user?.name}!</h1>
</main>