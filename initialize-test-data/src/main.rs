extern crate postgres;

use std::env;
use std::fmt;
use std::fmt::Formatter;

use postgres::{Client, NoTls};
use uuid::Uuid;

struct User {
    id: Uuid,
    first_name: String,
    last_name: String,
    username: String,
}

impl fmt::Display for User {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {} {}", self.id, self.username, self.first_name, self.last_name)
    }
}

fn main() {
    let postgres_host: String = env::var("POSTGRES_HOST").unwrap();
    let mut client: Client = Client::connect(
        format!("host={} user=keycloak password=keycloak", postgres_host).as_str(),
        NoTls,
    ).unwrap();
    let mut users: Vec<User> = Vec::new();
    for row in client.query("SELECT id, first_name, last_name, username FROM user_entity", &[]).unwrap() {
        let id: &str = row.get(0);
        let first_name: &str;// = row.get(1);
        let last_name: &str;// = row.get(2);
        let username: &str = row.get(3);
        match username {
            "app_user" => {
                first_name = "Standard";
                last_name = "User"
            }
            "app_admin" => {
                first_name = "Admin";
                last_name = "User"
            }
            "app_super_user" => {
                first_name = "Super";
                last_name = "User"
            }
            &_ => {
                let col_1: Option<&str> = row.get(1);
                if col_1.is_none() {
                    first_name = "Lorem";
                } else {
                    first_name = col_1.unwrap();
                }
                let col_2: Option<&str> = row.get(2);
                if col_2.is_none() {
                    last_name = "Ipsum";
                } else {
                    last_name = col_2.unwrap();
                }
            }
        }
        println!("found: {} {} {} {}", id, username, first_name, last_name);
        let user: User = User {
            id: Uuid::parse_str(id).unwrap(),
            username: username.parse().unwrap(),
            first_name: first_name.parse().unwrap(),
            last_name: last_name.parse().unwrap(),
        };
        users.push(user);
    }
    client.close().unwrap();
    client = Client::connect(
        format!("host={} user=postgres password=postgres", postgres_host).as_str(),
        NoTls,
    ).unwrap();
    for user in &users {
        println!("{:}", user);
        client.execute(
            "INSERT INTO users (id, username, firstname, lastname) VALUES ($1, $2, $3, $4) ON CONFLICT DO NOTHING",
            &[&user.id, &user.username, &user.first_name, &user.last_name],
        ).unwrap();
    }

    let insert_query = "INSERT INTO documents(id, document_id) VALUES ($1, $2) ON CONFLICT DO NOTHING";
    let ids: [&str; 3] = ["c1df7d01-4bd7-40b6-86da-7e2ffabf37f7", "f2b2d644-3a08-4acb-ae07-20569f6f2a01", "90573d2b-9a5d-409e-bbb6-b94189709a19"];
    let document_ids: [&str; 3] = ["1", "2", "3"];
    let zip_iter = ids.iter().zip(document_ids.iter());
    for (id, document_id) in zip_iter {
        let id = Uuid::parse_str(&id).unwrap();
        client.execute(insert_query, &[&id, &document_id]).unwrap();
    }

    let user_permission_query = "INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type) VALUES ($1, $2, $3, $4) ON CONFLICT DO NOTHING";
    println!("\n\n");
    for user in &users {
        println!("____ {:}", user);
        for id in &ids {
            println!("--=-- {}", id);
            let user_permission_id: Uuid = Uuid::new_v4();
            let user_id: Uuid = user.id;
            let document_id: Uuid = Uuid::parse_str(id).unwrap();
            let permission_type: &str = "READ";
            client.execute(user_permission_query,
                &[&user_permission_id, &user_id, &document_id, &permission_type],
            ).unwrap();
        }
    }

    client.close().unwrap();
}