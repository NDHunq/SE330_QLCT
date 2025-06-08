import { OtpTokenRepository } from "./../repository/otp-token.repository";
import { twoMinutesInSec } from "@/constants/time.constant";
import { ResetPasswordDto } from "@/dto/user/reset-password.dto";
import { UserRegisterDto } from "@/dto/user/user-register.dto";
import { IUserRepository } from "@/repository/interface/i.user.repository";
import { BaseService } from "@/service/base/base.service";
import { ICategoryService } from "@/service/interface/i.category.service";
import { IUserService } from "@/service/interface/i.user.service";
import { IWalletService } from "@/service/interface/i.wallet.service";
import { ITYPES } from "@/types/interface.types";
import { SERVICE_TYPES } from "@/types/service.types";
import BaseError from "@/utils/error/base.error";
import { generateRandomString } from "@/utils/random/ramdom.generate";
import { sendSms } from "@/utils/sms/sms.send";
import { plainToInstance } from "class-transformer";
import { validate } from "class-validator";
import { StatusCodes } from "http-status-codes";
import { inject } from "inversify";
const jwt = require("jsonwebtoken");

export class UserService extends BaseService implements IUserService<any> {
  private walletService: IWalletService<any>;
  private categoryService: ICategoryService<any>;
  private otpTokenRepository: OtpTokenRepository;

  constructor(
    @inject(ITYPES.Repository) repository: IUserRepository<any>,
    @inject(SERVICE_TYPES.Wallet) walletService: IWalletService<any>,
    @inject(SERVICE_TYPES.Category) categoryService: ICategoryService<any>,
    @inject(ITYPES.OtpTokenRepository) otpTokenRepository: OtpTokenRepository
  ) {
    super(repository);
    this.walletService = walletService;
    this.categoryService = categoryService;
    this.otpTokenRepository = otpTokenRepository;
  }

  async resetPasswordCallBack(data: ResetPasswordDto): Promise<any> {
    try {
      const otpRecord = await this.otpTokenRepository.findByPhoneAndType(
        data.phone_number,
        "forgetPassword"
      );
      if (!otpRecord) {
        throw new BaseError(
          StatusCodes.BAD_REQUEST,
          "fail",
          "OTP is expired or not found"
        );
      }
      if (otpRecord.otp_code !== data.otp_code) {
        throw new BaseError(StatusCodes.BAD_REQUEST, "fail", "Invalid OTP");
      }
      if (new Date() > otpRecord.expires_at) {
        await this.otpTokenRepository.deleteByPhoneAndType(
          data.phone_number,
          "forgetPassword"
        );
        throw new BaseError(StatusCodes.BAD_REQUEST, "fail", "OTP is expired");
      }

      await this.repository._update({
        where: { phone_number: data.phone_number },
        data: { password: data.new_password },
      });

      await this.otpTokenRepository.deleteByPhoneAndType(
        data.phone_number,
        "forgetPassword"
      );

      return {
        message: "Password has been reset successfully",
      };
    } catch (error) {
      throw error;
    }
  }

  async forgetPassword(phone_number: string): Promise<any> {
    try {
      if (
        !(await this.repository._exists({
          where: { phone_number: phone_number },
        }))
      ) {
        throw new BaseError(
          StatusCodes.BAD_REQUEST,
          "fail",
          "Phone number not found"
        );
      }
      const existingOtp = await this.otpTokenRepository.findByPhoneAndType(
        phone_number,
        "forgetPassword"
      );
      if (existingOtp) {
        throw new BaseError(
          StatusCodes.BAD_REQUEST,
          "fail",
          "Reset password code has been sent to your phone number. Please wait for 2 minutes before sending again"
        );
      }

      const randomToken = await generateRandomString();
      const expiresAt = new Date(Date.now() + twoMinutesInSec * 1000);

      await this.otpTokenRepository.save({
        phone_number,
        otp_code: randomToken,
        type: "forgetPassword",
        expires_at: expiresAt,
      });

      await sendSms(
        `Hello from expense management!\nYour reset password code is ${randomToken}`,
        [phone_number]
      );
      return {
        message: "OTP sent! Waiting for verify phone number",
      };
    } catch (error) {
      throw error;
    }
  }

  async updateDeviceToken(userId: string, deviceToken: string): Promise<any> {
    return this.repository.updateDeviceToken(userId, deviceToken);
  }

  async changePassword(
    userId: string,
    data: {
      old_password: string;
      new_password: string;
    }
  ): Promise<any> {
    const { old_password, new_password } = data;
    const user = await this.repository._findOne({ where: { id: userId } });
    if (!user) {
      throw new BaseError(StatusCodes.BAD_REQUEST, "fail", "User not found");
    }
    if (user.password !== old_password) {
      throw new BaseError(
        StatusCodes.BAD_REQUEST,
        "fail",
        "Old password is incorrect"
      );
    }
    return this.repository._update({
      where: { id: userId },
      data: { password: new_password },
    });
  }

  async login(phone_number: string, password: string): Promise<any> {
    try {
      const user = await this.repository._findOne({
        where: { phone_number: phone_number },
      });
      if (!user)
        throw new BaseError(StatusCodes.BAD_REQUEST, "fail", "User not found");
      if (user.password !== password) {
        throw new BaseError(
          StatusCodes.BAD_REQUEST,
          "fail",
          "Password is incorrect"
        );
      }
      const token = jwt.sign(
        {
          id: user.id,
          phone_number: user.phone_number,
          password: user.password,
        },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN }
      );
      delete user.password;
      return {
        status: "success",
        user: user,
        token: "Bearer " + token,
      } as any;
    } catch (error) {
      throw error;
    }
  }

  async verifyPhoneNumber(data: {
    phone_number: string;
    verify_token: string;
  }): Promise<any> {
    try {
      const { phone_number, verify_token } = data;

      const otpRecord = await this.otpTokenRepository.findByPhoneAndType(
        phone_number,
        "verifyPhone"
      );
      if (!otpRecord) {
        throw new BaseError(
          StatusCodes.BAD_REQUEST,
          "fail",
          "Cannot verify phone number. Please send OTP again!"
        );
      }
      if (otpRecord.otp_code !== verify_token) {
        throw new BaseError(
          StatusCodes.BAD_REQUEST,
          "fail",
          "Invalid verification code"
        );
      }
      if (new Date() > otpRecord.expires_at) {
        await this.otpTokenRepository.deleteByPhoneAndType(
          phone_number,
          "verifyPhone"
        );
        throw new BaseError(
          StatusCodes.BAD_REQUEST,
          "fail",
          "Verification code is expired"
        );
      }

      const nonActiveUser = JSON.parse(otpRecord.otp_code);
      const newUserInstance = plainToInstance(UserRegisterDto, nonActiveUser);
      const validateErrors = await validate(newUserInstance, {
        validationError: { target: false, value: false },
      });
      if (validateErrors.length > 0) {
        const formatError = validateErrors.map((error: any) =>
          Object.values(error.constraints).join(", ")
        );
        throw new BaseError(400, "fail", formatError);
      }

      const result = await this.repository._create({ data: newUserInstance });
      await this.otpTokenRepository.deleteByPhoneAndType(
        phone_number,
        "verifyPhone"
      );

      await this.walletService.createWalletForUser(result.id, { data: null });
      await this.categoryService.createDefaultCategories(result.id);

      return result;
    } catch (error) {
      throw error;
    }
  }

  async register(data: UserRegisterDto): Promise<any> {
    try {
      if (
        await this.repository._exists({
          where: { phone_number: data.phone_number },
        })
      ) {
        throw new BaseError(
          StatusCodes.BAD_REQUEST,
          "fail",
          "Phone number already exists"
        );
      }
      const existingOtp = await this.otpTokenRepository.findByPhoneAndType(
        data.phone_number,
        "verifyPhone"
      );
      if (existingOtp) {
        throw new BaseError(
          StatusCodes.BAD_REQUEST,
          "fail",
          "Verification code has been sent to your phone number. Please wait for 2 minutes before sending again"
        );
      }

      const randomToken = await generateRandomString();
      const expiresAt = new Date(Date.now() + twoMinutesInSec * 1000);

      await this.otpTokenRepository.save({
        phone_number: data.phone_number,
        otp_code: JSON.stringify(data),
        type: "verifyPhone",
        expires_at: expiresAt,
      });

      await sendSms(
        `Hello from expense management!\nYour verification code is ${randomToken}`,
        [data.phone_number]
      );
      return {
        message: "OTP sent! Waiting for verify phone number",
      };
    } catch (error: any) {
      throw error;
    }
  }
}
