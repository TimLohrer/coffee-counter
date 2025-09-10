use crate::{models::{dto::login_response::LoginResponse, user::User}, utils::api_client::ApiClient};

pub mod utils;
pub mod models;

use std::sync::Mutex;

lazy_static::lazy_static! {
    static ref BASE_URL: Mutex<String> = Mutex::new(String::new());
    static ref TOKEN: Mutex<Option<String>> = Mutex::new(None);
}

#[tauri::command]
async fn set_backend_url(url: &str) -> Result<bool, ()> {
    match ApiClient::from_url(&format!("{url}/api/v1")).is_online().await {
        true => {
            let mut base_url = BASE_URL.lock().unwrap();
            *base_url = url.to_string();
            Ok(true)
        },
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
async fn login(email: &str, password: &str) -> Result<LoginResponse, String> {
    ApiClient::new()
        .login(email, password)
        .await
        .map_err(|e| format!("Failed to login: {}", e))
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_opener::init())
        .invoke_handler(tauri::generate_handler![
            set_backend_url,
            create_account,
            login
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
