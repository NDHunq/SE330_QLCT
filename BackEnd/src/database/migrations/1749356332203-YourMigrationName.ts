import { MigrationInterface, QueryRunner } from "typeorm";

export class YourMigrationName1749356332203 implements MigrationInterface {
    name = 'YourMigrationName1749356332203'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`CREATE TABLE "user_wallet" ("user_id" uuid NOT NULL, "wallet_id" uuid NOT NULL, "join_date" TIMESTAMP NOT NULL DEFAULT now(), "isAdmin" boolean NOT NULL DEFAULT true, CONSTRAINT "PK_0278937b7c27070e5ce8c8e5412" PRIMARY KEY ("user_id", "wallet_id"))`);
        await queryRunner.query(`CREATE TABLE "user" ("id" uuid NOT NULL DEFAULT uuid_generate_v4(), "username" character varying(20) NOT NULL, "phone_number" character varying(15) NOT NULL, "password" character varying(100) NOT NULL, "deviceToken" text, "currency_unit" "public"."user_currency_unit_enum" NOT NULL DEFAULT 'VND', CONSTRAINT "UQ_78a916df40e02a9deb1c4b75edb" UNIQUE ("username"), CONSTRAINT "UQ_01eea41349b6c9275aec646eee0" UNIQUE ("phone_number"), CONSTRAINT "PK_cace4a159ff9f2512dd42373760" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "budget" ("id" uuid NOT NULL DEFAULT uuid_generate_v4(), "category_id" uuid NOT NULL, "limit_amount" numeric(30,0) NOT NULL, "expensed_amount" numeric(30,0) NOT NULL DEFAULT '0', "currency_unit" "public"."budget_currency_unit_enum" NOT NULL DEFAULT 'VND', "budget_type" "public"."budget_budget_type_enum" NOT NULL DEFAULT 'NO_RENEW', "no_renew_date_unit" "public"."budget_no_renew_date_unit_enum", "no_renew_date" character varying, "renew_date_unit" "public"."budget_renew_date_unit_enum", "custom_renew_date" TIMESTAMP, "is_active" boolean NOT NULL DEFAULT true, "enable_notification" boolean NOT NULL DEFAULT false, "create_at" TIMESTAMP NOT NULL DEFAULT now(), "user_id" uuid NOT NULL, CONSTRAINT "REL_af6f95ccfa1f460edca6b48880" UNIQUE ("category_id"), CONSTRAINT "PK_9af87bcfd2de21bd9630dddaa0e" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "category" ("id" uuid NOT NULL DEFAULT uuid_generate_v4(), "name" character varying(20) NOT NULL, "picture" text, "type" "public"."category_type_enum" NOT NULL DEFAULT 'EXPENSE', "user_id" uuid NOT NULL, CONSTRAINT "PK_9c4e4a89e3674fc9f382d733f03" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE UNIQUE INDEX "IDX_4ef92ef1299f3d2539a8eb58ff" ON "category" ("name", "user_id", "type") `);
        await queryRunner.query(`CREATE TABLE "transactions" ("id" uuid NOT NULL DEFAULT uuid_generate_v4(), "user_id" uuid NOT NULL, "amount" numeric(30,0) NOT NULL, "category_id" uuid, "wallet_id" uuid NOT NULL, "notes" text, "picture" text, "transaction_date" TIMESTAMP NOT NULL DEFAULT now(), "transaction_type" "public"."transactions_transaction_type_enum" NOT NULL DEFAULT 'EXPENSE', "currency_unit" "public"."transactions_currency_unit_enum" NOT NULL DEFAULT 'VND', "target_wallet_id" uuid, CONSTRAINT "PK_a219afd8dd77ed80f5a862f1db9" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "wallet" ("id" uuid NOT NULL DEFAULT uuid_generate_v4(), "name" character varying NOT NULL DEFAULT 'Default wallet', "amount" numeric(30,0) NOT NULL DEFAULT '0', "currency_unit" "public"."wallet_currency_unit_enum" NOT NULL DEFAULT 'VND', "create_at" TIMESTAMP NOT NULL DEFAULT now(), "update_at" TIMESTAMP NOT NULL DEFAULT now(), CONSTRAINT "PK_bec464dd8d54c39c54fd32e2334" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "otp_tokens" ("id" uuid NOT NULL DEFAULT uuid_generate_v4(), "phone_number" character varying NOT NULL, "otp_code" character varying NOT NULL, "type" character varying NOT NULL, "expires_at" TIMESTAMP NOT NULL, "created_at" TIMESTAMP NOT NULL DEFAULT now(), "updated_at" TIMESTAMP NOT NULL DEFAULT now(), CONSTRAINT "PK_424fa4c4152eafc0b2d929e138d" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "role" ("id" uuid NOT NULL DEFAULT uuid_generate_v4(), "name" character varying(30) NOT NULL, CONSTRAINT "PK_b36bcfe02fc8de3c57a8b2391c2" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE UNIQUE INDEX "IDX_ae4578dcaed5adff96595e6166" ON "role" ("name") `);
        await queryRunner.query(`CREATE TABLE "account" ("id" uuid NOT NULL DEFAULT uuid_generate_v4(), "roleId" uuid NOT NULL, "email" character varying(30) NOT NULL, "password" character varying(20) NOT NULL, "fullname" character varying(30) NOT NULL, "address" character varying(150) NOT NULL, "phone_number" character varying(15) NOT NULL, "birthday" date NOT NULL, "create_at" TIMESTAMP NOT NULL DEFAULT now(), "update_at" TIMESTAMP NOT NULL DEFAULT now(), CONSTRAINT "PK_54115ee388cdb6d86bb4bf5b2ea" PRIMARY KEY ("id"))`);
        await queryRunner.query(`ALTER TABLE "user_wallet" ADD CONSTRAINT "FK_7b752f8f6f9b2e1f85c120894dd" FOREIGN KEY ("user_id") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "user_wallet" ADD CONSTRAINT "FK_17ac73c7036e383bbf9d6ac3a3a" FOREIGN KEY ("wallet_id") REFERENCES "wallet"("id") ON DELETE CASCADE ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "budget" ADD CONSTRAINT "FK_af6f95ccfa1f460edca6b488803" FOREIGN KEY ("category_id") REFERENCES "category"("id") ON DELETE CASCADE ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "budget" ADD CONSTRAINT "FK_68df09bd8001a1fb0667a9b42f7" FOREIGN KEY ("user_id") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "category" ADD CONSTRAINT "FK_6562e564389d0600e6e243d9604" FOREIGN KEY ("user_id") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "transactions" ADD CONSTRAINT "FK_0b171330be0cb621f8d73b87a9e" FOREIGN KEY ("wallet_id") REFERENCES "wallet"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "transactions" ADD CONSTRAINT "FK_e9acc6efa76de013e8c1553ed2b" FOREIGN KEY ("user_id") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "transactions" ADD CONSTRAINT "FK_53e1a3d4483d64784ed2133747b" FOREIGN KEY ("target_wallet_id") REFERENCES "wallet"("id") ON DELETE SET NULL ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "transactions" ADD CONSTRAINT "FK_c9e41213ca42d50132ed7ab2b0f" FOREIGN KEY ("category_id") REFERENCES "category"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "account" ADD CONSTRAINT "FK_77bf26eef8865441fb9bd53a364" FOREIGN KEY ("roleId") REFERENCES "role"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "account" DROP CONSTRAINT "FK_77bf26eef8865441fb9bd53a364"`);
        await queryRunner.query(`ALTER TABLE "transactions" DROP CONSTRAINT "FK_c9e41213ca42d50132ed7ab2b0f"`);
        await queryRunner.query(`ALTER TABLE "transactions" DROP CONSTRAINT "FK_53e1a3d4483d64784ed2133747b"`);
        await queryRunner.query(`ALTER TABLE "transactions" DROP CONSTRAINT "FK_e9acc6efa76de013e8c1553ed2b"`);
        await queryRunner.query(`ALTER TABLE "transactions" DROP CONSTRAINT "FK_0b171330be0cb621f8d73b87a9e"`);
        await queryRunner.query(`ALTER TABLE "category" DROP CONSTRAINT "FK_6562e564389d0600e6e243d9604"`);
        await queryRunner.query(`ALTER TABLE "budget" DROP CONSTRAINT "FK_68df09bd8001a1fb0667a9b42f7"`);
        await queryRunner.query(`ALTER TABLE "budget" DROP CONSTRAINT "FK_af6f95ccfa1f460edca6b488803"`);
        await queryRunner.query(`ALTER TABLE "user_wallet" DROP CONSTRAINT "FK_17ac73c7036e383bbf9d6ac3a3a"`);
        await queryRunner.query(`ALTER TABLE "user_wallet" DROP CONSTRAINT "FK_7b752f8f6f9b2e1f85c120894dd"`);
        await queryRunner.query(`DROP TABLE "account"`);
        await queryRunner.query(`DROP INDEX "public"."IDX_ae4578dcaed5adff96595e6166"`);
        await queryRunner.query(`DROP TABLE "role"`);
        await queryRunner.query(`DROP TABLE "otp_tokens"`);
        await queryRunner.query(`DROP TABLE "wallet"`);
        await queryRunner.query(`DROP TABLE "transactions"`);
        await queryRunner.query(`DROP INDEX "public"."IDX_4ef92ef1299f3d2539a8eb58ff"`);
        await queryRunner.query(`DROP TABLE "category"`);
        await queryRunner.query(`DROP TABLE "budget"`);
        await queryRunner.query(`DROP TABLE "user"`);
        await queryRunner.query(`DROP TABLE "user_wallet"`);
    }

}
