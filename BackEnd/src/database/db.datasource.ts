import "dotenv/config";
import { Account } from "../models/account.model";
import { Role } from "../models/role.model";
import "reflect-metadata";
import { DataSource } from "typeorm";
const path = require("path");
const entities_path = path.join(__dirname, "..", "models", "*");

export const AppDataSource = new DataSource({
  type: "postgres",
  url: "postgresql://postgres.bfjzdmtixcolcindaowa:redRUM@@@213@aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres",
  host: process.env.DB_HOST || "db.bfjzdmtixcolcindaowa.supabase.co",
  port: Number(process.env.DB_PORT) || 5432,
  username: process.env.DB_USERNAME || "postgres.bfjzdmtixcolcindaowa",
  password: process.env.DB_PASSWORD || "redRUM@@@213",
  database: process.env.DB_NAME || "se330",
  entities: [entities_path],
  synchronize: true,
  logging: false,
  migrations: [__dirname + "/migrations/*.js"],
});

export async function dbFirstStartQuery() {
  const manager = AppDataSource.manager;

  // await manager.query(`SET GLOBAL time_zone = '+07:00';`);
  // await manager.query(`SET time_zone = '+07:00';`);
}
