use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
#[serde(rename_all = "camelCase")]
pub struct Coffee {
    #[serde(rename = "_id")]
    pub id: String,
    pub user_id: String,
    pub machine_id: String,
    pub price: f64,
    pub created_at: u64,
}
