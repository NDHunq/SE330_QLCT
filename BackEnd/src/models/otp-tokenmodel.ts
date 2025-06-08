import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  UpdateDateColumn,
} from "typeorm";

@Entity("otp_tokens")
export class OtpToken {
  @PrimaryGeneratedColumn("uuid")
  id!: string;

  @Column()
  phone_number!: string;

  @Column()
  otp_code!: string;

  @Column()
  type!: string; // "forgetPassword" hoặc "verifyPhone"

  @Column({ type: "timestamp" })
  expires_at!: Date;

  @CreateDateColumn()
  created_at!: Date;

  @UpdateDateColumn()
  updated_at!: Date;
}
