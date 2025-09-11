use anyhow::Result;
use tauri::AppHandle;

use crate::{
    models::{app_data::AppData, dto::login_response::LoginResponse, user::User},
    utils::{api_client::ApiClient, store_manager::StoreManager},
};

pub mod models;
pub mod utils;

use std::sync::Mutex;

pub const STORE_NAME: &str = "appData";
pub const STORE_FILENAME: &str = "data.json";

lazy_static::lazy_static! {
    static ref BASE_URL: Mutex<String> = Mutex::new(String::new());
    static ref TOKEN: Mutex<Option<String>> = Mutex::new(None);
    static ref USER_ID: Mutex<Option<String>> = Mutex::new(None);
}

#[tauri::command]
async fn set_backend_url(app_handle: AppHandle, url: &str) -> Result<bool, ()> {
    match ApiClient::from_url(url).is_online().await {
        true => {
            let mut base_url = BASE_URL.lock().unwrap();
            *base_url = url.to_string();

            let mut user_data = StoreManager::get_app_data(&app_handle)
                .unwrap_or_else(|_| AppData::new(None, None, None));
            user_data.backend_url = Some(url.to_string());
            StoreManager::set_app_data(&app_handle, &user_data).unwrap();

            Ok(true)
        }
        false => Ok(false),
    }
}

#[tauri::command]
async fn create_account(email: &str, name: &str, password: &str) -> Result<User, String> {
    ApiClient::new()
        .create_account(email, name, password)
        .await
        .map_err(|e| format!("Failed to create account: {}", e))
}

#[tauri::command]
async fn login(
    app_handle: AppHandle,
    email: &str,
    password: &str,
) -> Result<LoginResponse, String> {
    let login_data = match ApiClient::new()
        .login(email, password)
        .await
        .map_err(|e| format!("Failed to login: {}", e))
    {
        Ok(data) => data,
        Err(err) => return Err(err),
    };

    let mut user_data = StoreManager::get_app_data(&app_handle)
        .map_err(|_| "Failed to get app data".to_string())?;

    user_data.token = Some(login_data.token.clone());
    user_data.user = Some(login_data.user.clone());

    StoreManager::set_app_data(&app_handle, &user_data)
        .map_err(|_| "Failed to set app data".to_string())?;

    Ok(login_data)
}

#[tauri::command]
async fn get_app_data(app_handle: AppHandle) -> Result<AppData, String> {
    StoreManager::get_app_data(&app_handle).map_err(|e| format!("Failed to get app data: {}", e))
}

#[tauri::command]
async fn logout(app_handle: AppHandle) -> Result<(), String> {
    let mut user_data = StoreManager::get_app_data(&app_handle)
        .map_err(|e| format!("Failed to get app data: {}", e))?;
    user_data.token = None;
    user_data.user = None;
    StoreManager::set_app_data(&app_handle, &user_data)
        .map_err(|e| format!("Failed to set app data: {}", e))?;

    let mut token = TOKEN.lock().unwrap();
    *token = None;
    let mut user_id = USER_ID.lock().unwrap();
    *user_id = None;

    Ok(())
}

#[tauri::command]
async fn reset_app_data(app_handle: AppHandle) -> Result<(), String> {
    let user_data = AppData::new(None, None, None);
    StoreManager::set_app_data(&app_handle, &user_data)
        .map_err(|e| format!("Failed to reset app data: {}", e))?;

    let mut base_url = BASE_URL.lock().unwrap();
    *base_url = String::new();
    let mut token = TOKEN.lock().unwrap();
    *token = None;
    let mut user_id = USER_ID.lock().unwrap();
    *user_id = None;

    Ok(())
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_deep_link::init())
        .plugin(tauri_plugin_store::Builder::new().build())
        .plugin(tauri_plugin_log::Builder::new().build())
        .plugin(tauri_plugin_opener::init())
        .plugin(tauri_plugin_log::Builder::new().build())
        .setup(|app| {
            StoreManager::initialize_store(app.handle())?;
            let app_data = StoreManager::get_app_data(&app.handle());
            match app_data {
                Ok(data) => {
                    if let Some(token) = &data.token {
                        let mut token_lock = TOKEN.lock().unwrap();
                        *token_lock = Some(token.to_string());
                    }
                    if let Some(backend_url) = &data.backend_url {
                        let mut base_url_lock = BASE_URL.lock().unwrap();
                        *base_url_lock = backend_url.to_string();
                    }
                    if let Some(user_id) = &data.user.as_ref().map(|u| u.id.clone()) {
                        let mut user_id_lock = USER_ID.lock().unwrap();
                        *user_id_lock = Some(user_id.to_string());
                    }

                    println!("\nInitialized with app data: {:?}\n", data);
                }
                Err(_) => (),
            }

            Ok(())
        })
        .invoke_handler(tauri::generate_handler![
            set_backend_url,
            create_account,
            login,
            get_app_data,
            logout,
            reset_app_data
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
