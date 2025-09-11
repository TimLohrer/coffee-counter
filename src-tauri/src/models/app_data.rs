use serde::{Deserialize, Serialize};

use crate::models::user::User;

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct AppData {
    pub backend_url: Option<String>,
    pub token: Option<String>,
    pub user: Option<User>,
}

impl AppData {
    pub fn new(backend_url: Option<String>, token: Option<String>, user: Option<User>) -> Self {
        AppData {
            backend_url,
            token,
            user
        }
    }

    pub fn to_json(&self) -> serde_json::Value {
        serde_json::json!({
            "backendUrl": self.backend_url,
            "token": self.token,
            "user": self.user.as_ref().map(|u| u.to_json()),
        })
    }

    pub fn from_json(value: &serde_json::Value) -> Self {
        let backend_url = value.get("backendUrl").and_then(|v| v.as_str().map(String::from));
        let token = value.get("token").and_then(|v| v.as_str().map(String::from));
        let user = value.get("user").and_then(|v| User::from_json(v).into());

        AppData {
            backend_url,
            token,
            user
        }
    }
}