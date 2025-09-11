<script lang="ts">
  import { onMount } from "svelte";
  import { APP_DATA, loadAppData } from "../utils/api";
  import { info } from "@tauri-apps/plugin-log";
  import { get } from "svelte/store";

  onMount(async () => {
    await loadAppData();

    console.info(`App data on startup: ${JSON.stringify(get(APP_DATA))}`);

    const $APP_DATA = get(APP_DATA);

    if ($APP_DATA?.user && $APP_DATA?.token) {
      window.location.href = "/home";
    } else if (!$APP_DATA?.backendUrl) {
      window.location.href = "/setup";
    } else {
      window.location.href = "/login";
    }
  })
</script>

<main class="container">
</main>
