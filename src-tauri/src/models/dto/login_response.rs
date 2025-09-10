use serde::{Deserialize, Serialize};

use crate::models::user::User;

#[derive(Serialize, Deserialize, Debug, Clone)]
#[serde(rename_all = "camelCase")]
pub struct LoginResponse {
    pub user: User,
    pub token: String,
}
