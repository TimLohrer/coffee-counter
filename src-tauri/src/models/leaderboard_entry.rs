use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
#[serde(rename_all = "camelCase")]
pub struct LeaderboardEntry {
    #[serde(rename = "first")]
    pub user_id: String,
    #[serde(rename = "second")]
    pub coffee_count: u32,
}
