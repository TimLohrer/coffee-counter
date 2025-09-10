use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
#[serde(rename_all = "camelCase")]
pub struct CoffeeMachine {
    #[serde(rename = "_id")]
    pub id: String,
    pub name: String,
    pub is_free: bool,
    pub created_at: u64,
}
