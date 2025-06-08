import { injectable } from "inversify";
import { INotificationService } from "@/service/interface/i.notification.service";

@injectable()
export class NotificationService implements INotificationService {
  // Tạm thời vô hiệu hóa tất cả chức năng notification
  async deleteNotification(notificationId: string): Promise<any> {
    return; // Không làm gì
  }

  async getNotificationList(userId: string): Promise<any> {
    return []; // Trả về mảng rỗng để tránh lỗi
  }

  async sendNotificationToDeviceToken(
    deviceToken: string,
    notification: { title: string; body: string; imageUrl?: string }
  ): Promise<any> {
    return; // Không làm gì
  }

  async sendNotificationToUser(userId: string, message: string): Promise<any> {
    console.log(`Notification to user ${userId} disabled temporarily`);
    return; // Không làm gì
  }
}
