import { inject, injectable } from "inversify";
import { DataSource, Repository } from "typeorm";
import { OtpToken } from "@/models/otp-tokenmodel";
import { ITYPES } from "@/types/interface.types";

@injectable()
export class OtpTokenRepository {
  private repository: Repository<OtpToken>;

  constructor(@inject(ITYPES.Datasource) dataSource: DataSource) {
    this.repository = dataSource.getRepository(OtpToken);
  }

  async findByPhoneAndType(
    phone_number: string,
    type: string
  ): Promise<OtpToken | null> {
    return this.repository.findOne({ where: { phone_number, type } });
  }

  async deleteByPhoneAndType(
    phone_number: string,
    type: string
  ): Promise<void> {
    await this.repository.delete({ phone_number, type });
  }

  async save(data: Partial<OtpToken>): Promise<OtpToken> {
    return this.repository.save(data);
  }
}
