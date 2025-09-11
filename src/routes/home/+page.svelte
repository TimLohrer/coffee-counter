<script lang="ts">
    import { BottomSheet } from "svelte-bottom-sheet";
    import { APP_DATA } from "../../utils/api";
    import { onMount } from "svelte";

    let sheetOpen = $state(false);

    let sheetData: { machineId: string; isFree: string; at: string } | null = $state(null);

    onMount(async () => {
        // await loadAppData();

        const params = new URLSearchParams(window.location.search);
        const machineId = params.get("machineId");
        const isFree = params.get("isFree");
        const at = params.get("at");

        if (machineId && isFree && at) {
            sheetData = { machineId, isFree, at };
            sheetOpen = true;
        }
    });
</script>

<main class="container">
    <BottomSheet settings={{ maxHeight: 0.4, disableClosing: true }} bind:isSheetOpen={sheetOpen}>
        <BottomSheet.Overlay>
            <BottomSheet.Sheet>
                <BottomSheet.Handle />
                <BottomSheet.Content style="padding: 0;">
                    <div class="sheet-content">
                        <p>Machine ID: {sheetData?.machineId}</p>
                        <p>Is free: {sheetData?.isFree}</p>
                        <p>At: {sheetData?.at}</p>
                    </div>
                </BottomSheet.Content>
            </BottomSheet.Sheet>
        </BottomSheet.Overlay>
    </BottomSheet>
</main>

<style>
    .container {
        margin-top: env(safe-area-inset-top);
    }

    .sheet-content {
        display: flex;
        flex-direction: column;
        width: 100vw;
        height: 50%;
        align-items: center;
        justify-content: center;
        color: #000;
    }
</style>