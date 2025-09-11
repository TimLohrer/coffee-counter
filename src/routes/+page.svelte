<script lang="ts">
  import { onMount } from "svelte";
  import { APP_DATA, loadAppData } from "../utils/api";
  import { startup } from "../utils/global";
  import { getCurrent } from "@tauri-apps/plugin-deep-link";

  let x = "";

  onMount(async () => {
    await startup();
    // x = `Startup deep link URLs: ${startUrls}`;
    
    // await loadAppData();

    console.info(`App data on startup: ${JSON.stringify($APP_DATA)}`);

    // if ($APP_DATA?.user && $APP_DATA?.token) {
    //   window.location.href = "/home";
    // } else if (!$APP_DATA?.backendUrl) {
    //   window.location.href = "/setup";
    // } else {
    //   window.location.href = "/login";
    // }
  })
</script>

<main class="container">
  <p>{x}</p>
</main>
