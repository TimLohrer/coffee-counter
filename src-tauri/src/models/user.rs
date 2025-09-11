use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
#[serde(rename_all = "camelCase")]
pub struct User {
    #[serde(rename = "_id")]
    pub id: String,
    pub name: String,
    pub is_admin: bool,
    pub created_at: u64,
}

impl User {
    pub fn new(id: String, name: String, is_admin: bool, created_at: u64) -> Self {
        User {
            id,
            name,
            is_admin,
            created_at,
        }
    }

    pub fn to_json(&self) -> serde_json::Value {
        serde_json::json!({
            "_id": self.id,
            "name": self.name,
            "isAdmin": self.is_admin,
            "createdAt": self.created_at,
        })
    }

    pub fn from_json(value: &serde_json::Value) -> Self {
        let id = value
            .get("_id")
            .and_then(|v| v.as_str().map(String::from))
            .unwrap_or_default();
        let name = value
            .get("name")
            .and_then(|v| v.as_str().map(String::from))
            .unwrap_or_default();
        let is_admin = value
            .get("isAdmin")
            .and_then(|v| v.as_bool())
            .unwrap_or(false);
        let created_at = value.get("createdAt").and_then(|v| v.as_u64()).unwrap_or(0);

        User {
            id,
            name,
            is_admin,
            created_at,
        }
    }
}
