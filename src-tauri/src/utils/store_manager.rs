use anyhow::Result;
use tauri::AppHandle;
use tauri_plugin_store::StoreExt;

use crate::{models::app_data::AppData, STORE_FILENAME, STORE_NAME};

pub struct StoreManager;

impl StoreManager {
    pub fn new() -> Self {
        StoreManager
    }

    pub fn initialize_store(app: &AppHandle) -> Result<()> {
        let store = app
            .store(STORE_FILENAME)
            .expect("Failed to create or load store!");
        let value = store.get(STORE_NAME);

        if value.is_none() {
            store.set(STORE_NAME, AppData::new(None, None, None).to_json());
        }

        Ok(())
    }

    pub fn get_app_data(app: &AppHandle) -> Result<AppData> {
        let store = app.store(STORE_FILENAME)?;
        match store.get(STORE_NAME) {
            Some(v) => Ok(AppData::from_json(&v)),
            None => return Err(anyhow::anyhow!("No app data found in store")),
        }
    }

    pub fn set_app_data(app: &AppHandle, data: &AppData) -> Result<()> {
        let store = app.store(STORE_FILENAME)?;
        store.set(STORE_NAME, data.to_json());
        println!("Set app data in store: {:?}", data);
        Ok(())
    }
}
