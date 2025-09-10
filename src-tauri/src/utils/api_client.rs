use reqwest::Client;
use serde::{de::DeserializeOwned, Serialize};

use crate::{models::{coffee::Coffee, coffee_machine::CoffeeMachine, dto::login_response::LoginResponse, leaderboard_entry::LeaderboardEntry, user::User}, BASE_URL, TOKEN};

pub struct ApiClient {
    base_url: String,
    client: Client,
    token: Option<String>,
}

impl ApiClient {
    pub fn new() -> Self {
        Self {
            base_url: BASE_URL.lock().unwrap().clone(),
            client: reqwest::Client::new(),
            token: TOKEN.lock().unwrap().clone(),
        }
    }

    pub fn from_url(url: &str) -> Self {
        Self {
            base_url: url.to_string(),
            client: reqwest::Client::new(),
            token: TOKEN.lock().unwrap().clone(),
        }
    }

    pub async fn is_online(&self) -> bool {
        println!("Checking if backend is online at {}", self.base_url);
        self.client.get(&self.base_url)
            .send()
            .await
            .map(|resp| resp.status().is_success())
            .unwrap_or(false)
    }

    pub async fn get<T : DeserializeOwned>(&self, endpoint: &str) -> Result<T, reqwest::Error> {
        let url = format!("{}/{}", self.base_url, endpoint);
        self.client.get(&url)
            .bearer_auth(self.token.clone().unwrap_or_default())
            .send()
            .await?
            .json::<T>()
            .await
    }

    pub async fn post<D : Serialize, T : DeserializeOwned>(&self, endpoint: &str, body: &D) -> Result<T, reqwest::Error> {
        let url = format!("{}/{}", self.base_url, endpoint);
        self.client.post(&url).body(serde_json::to_string(body).unwrap())
            .bearer_auth(self.token.clone().unwrap_or_default())
            .send()
            .await?
            .json::<T>()
            .await
    }


    pub async fn patch<D : Serialize, T : DeserializeOwned>(&self, endpoint: &str, body: &D) -> Result<T, reqwest::Error> {
        let url = format!("{}/{}", self.base_url, endpoint);
        self.client.patch(&url).body(serde_json::to_string(body).unwrap())
            .bearer_auth(self.token.clone().unwrap_or_default())
            .send()
            .await?
            .json::<T>()
            .await
    }

    pub async fn delete<T: DeserializeOwned>(&self, endpoint: &str) -> Result<T, reqwest::Error> {
        let url = format!("{}/{}", self.base_url, endpoint);
        self.client.delete(&url)
            .bearer_auth(self.token.clone().unwrap_or_default())
            .send()
            .await?
            .json::<T>()
            .await
    }

    // ============================================================================================

    pub async fn create_account(&self, email: &str, name: &str, password: &str) -> Result<User, reqwest::Error> {
        #[derive(Serialize)]
        struct CreateAccountRequest<'a> {
            email: &'a str,
            name: &'a str,
            password: &'a str,
        }

        let body = CreateAccountRequest { email, name, password };
        self.post("account", &body).await
    }

    pub async fn login(&self, email: &str, password: &str) -> Result<LoginResponse, reqwest::Error> {
        #[derive(Serialize)]
        struct LoginRequest<'a> {
            email: &'a str,
            password: &'a str,
        }

        let body = LoginRequest { email, password };
        let data: LoginResponse = self.post("login", &body).await?;
        
        // Update the global token
        let mut token_lock = TOKEN.lock().unwrap();
        *token_lock = Some(data.token.clone());

        Ok(data)
    }

    pub async fn get_user(&self, user_id: &str) -> Result<User, reqwest::Error> {
        let endpoint = format!("users/{}", user_id);
        self.get(&endpoint).await
    }

    pub async fn update_user(&self, user_id: &str, email: Option<&str>, name: Option<&str>) -> Result<User, reqwest::Error> {
        #[derive(Serialize)]
        struct UpdateUserRequest<'a> {
            email: Option<&'a str>,
            name: Option<&'a str>
        }

        let body = UpdateUserRequest { email, name };
        let endpoint = format!("users/{}", user_id);
        self.patch(&endpoint, &body).await
    }

    pub async fn delete_user(&self, user_id: &str) -> Result<User, reqwest::Error> {
        let endpoint = format!("users/{}", user_id);
        self.delete(&endpoint).await
    }

    pub async fn get_user_coffees(&self, user_id: &str) -> Result<Vec<Coffee>, reqwest::Error> {
        let endpoint = format!("users/{}/coffees", user_id);
        self.get(&endpoint).await
    }


    pub async fn create_coffee_machine(&self, name: &str, is_free: bool) -> Result<(), reqwest::Error> {
        #[derive(Serialize)]
        #[serde(rename_all = "camelCase")]
        struct CreateCoffeeMachineRequest<'a> {
            name: &'a str,
            is_free: bool,
        }

        let body = CreateCoffeeMachineRequest { name, is_free };
        self.post("machines", &body).await
    }

    pub async fn get_coffee_machines(&self) -> Result<Vec<CoffeeMachine>, reqwest::Error> {
        self.get("machines").await
    }

    pub async fn update_coffee_machine(&self, machine_id: &str, name: Option<&str>, is_free: Option<bool>) -> Result<CoffeeMachine, reqwest::Error> {
        #[derive(Serialize)]
        struct UpdateCoffeeMachineRequest<'a> {
            name: Option<&'a str>,
            is_free: Option<bool>,
        }

        let body = UpdateCoffeeMachineRequest { name, is_free };
        let endpoint = format!("machines/{}", machine_id);
        self.patch(&endpoint, &body).await
    }

    pub async fn delete_coffee_machine(&self, machine_id: &str) -> Result<CoffeeMachine, reqwest::Error> {
        let endpoint = format!("machines/{}", machine_id);
        self.delete(&endpoint).await
    }

    pub async fn create_coffee(&self, user_id: &str, machine_id: &str, price: f64) -> Result<Coffee, reqwest::Error> {
        #[derive(Serialize)]
        struct CreateCoffeeRequest<'a> {
            user_id: &'a str,
            machine_id: &'a str,
            price: f64,
        }

        let body = CreateCoffeeRequest { user_id, machine_id, price };
        self.post("coffees", &body).await
    }

    pub async fn delete_coffee(&self, coffee_id: &str) -> Result<Coffee, reqwest::Error> {
        let endpoint = format!("coffees/{}", coffee_id);
        self.delete(&endpoint).await
    }

    pub async fn get_leaderboard(&self, from: u64, to: u64) -> Result<Vec<LeaderboardEntry>, reqwest::Error> {
        let endpoint = format!("leaderboard?from={from}&to={to}");
        self.get(&endpoint).await
    }

    pub async fn get_leaderboard_position(&self, user_id: &str, from: u64, to: u64) -> Result<u32, reqwest::Error> {
        let endpoint = format!("leaderboard/{user_id}?from={from}&to={to}");
        self.get(&endpoint).await
    }
}