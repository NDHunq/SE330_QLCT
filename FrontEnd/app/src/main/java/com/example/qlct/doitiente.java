package com.example.qlct;

import android.util.Log;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class doitiente {

    public double USDtoVND;
    public double UERtoVND; // Giữ nguyên UER nếu các phần khác đang dùng
    public double CNYtoVND;
    public double VNDtoUER; // Giữ nguyên UER
    public double VNDtoCNY;
    public double VNDtoUSD;

    // Constructor cũ của bạn
    public doitiente(double VNDtoUSD, double VNDtoUER, double VNDtoCNY) {
        this.VNDtoUSD = VNDtoUSD;
        this.VNDtoUER = VNDtoUER;
        this.VNDtoCNY = VNDtoCNY;
        // Cẩn thận: nếu VNDtoUSD, VNDtoUER, VNDtoCNY là 0, sẽ có lỗi chia cho 0 ở đây
        this.USDtoVND = (VNDtoUSD != 0) ? (1 / VNDtoUSD) : 0;
        this.UERtoVND = (VNDtoUER != 0) ? (1 / VNDtoUER) : 0;
        this.CNYtoVND = (VNDtoCNY != 0) ? (1 / VNDtoCNY) : 0;
    }

    // Constructor mặc định cũ của bạn
    public doitiente() {
        this.VNDtoUSD = 1.0/25455.0; // Đảm bảo là phép chia số thực
        this.VNDtoUER = 1.0/27462.13;
        this.VNDtoCNY = 1.0/3522.40;
        this.USDtoVND = 25455.0;
        this.UERtoVND = 27462.13;
        this.CNYtoVND = 3522.40;
    }

    // --- PHƯƠNG THỨC MỚI CẦN THÊM ---
    public double convertFromVND(String targetCurrency, double amountInVND) {
        if (amountInVND == 0) {
            return 0.0;
        }
        // Sử dụng các biến tỷ giá VNDto[NgoaiTe] mà bạn đã có
        switch (targetCurrency.toUpperCase()) {
            case "USD":
                // amountInVND * (1 USD / X VND) = amountInUSD
                // VNDtoUSD của bạn là (1 USD / X VND) nên phép tính là amountInVND * VNDtoUSD
                if (this.VNDtoUSD != 0) { // Kiểm tra để tránh nhân với 0 nếu tỷ giá chưa đúng
                    return amountInVND * this.VNDtoUSD;
                }
                break;
            case "UER": // Giữ UER theo code cũ của bạn
            case "EUR": // Thêm EUR để linh hoạt hơn nếu có chỗ gọi là EUR
                if (this.VNDtoUER != 0) {
                    return amountInVND * this.VNDtoUER;
                }
                break;
            case "CNY":
                if (this.VNDtoCNY != 0) {
                    return amountInVND * this.VNDtoCNY;
                }
                break;
            case "VND":
                return amountInVND;
            default:
                Log.e("doitiente", "convertFromVND: Cannot convert from VND to unknown/unsupported currency: " + targetCurrency + " or exchange rate is zero/not set.");
                return 0.0; // Hoặc ném Exception
        }
        Log.e("doitiente", "convertFromVND: Exchange rate for " + targetCurrency + " from VND (using VNDto[Currency] variable) is zero or not set.");
        return 0.0; // Trường hợp tỷ giá là 0
    }
    // --- KẾT THÚC PHƯƠNG THỨC MỚI ---


    // --- CÁC PHƯƠNG THỨC CŨ CỦA BẠN (GIỮ NGUYÊN) ---
    public double getUSDtoVND() {
        return USDtoVND;
    }

    public String formatValue(double value) {
        String sign = "";
        if (value < 0) {
            sign = "-";
            value = Math.abs(value); // Lấy giá trị tuyệt đối của số
        }

        String[] units = new String[] {"", "K", "M", "B", "T"};
        int unitIndex = 0;

        while (value >= 1000 && unitIndex < units.length - 1) {
            value /= 1000;
            unitIndex++;
        }

        DecimalFormat df = new DecimalFormat("0.###");
        return sign + df.format(value) + units[unitIndex]; // Thêm lại dấu vào kết quả
    }

    public Double converttoVND( String currency_unit,Double amount)
    {
        if (amount == null) return 0.0; // Thêm kiểm tra null cho amount
        if(currency_unit.equals("VND"))
        {
            return amount;
        }
        if(currency_unit.equals("USD"))
        {
            return amount*USDtoVND;
        }
        if(currency_unit.equals("UER")) // Giữ UER
        {
            return amount*UERtoVND;
        }
        if(currency_unit.equals("EUR")) // Thêm EUR nếu có thể được gọi
        {
            return amount*UERtoVND; // Giả sử UERtoVND là tỷ giá cho EUR
        }
        if(currency_unit.equals("CNY"))
        {
            return amount*CNYtoVND;
        }
        Log.e("doitiente", "converttoVND: Unknown currency_unit: " + currency_unit);
        return 0.0;
    }

    // Phương thức formatCurrency cũ của bạn (đã là static)
    public static String formatCurrency(double amount, String currency) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();

        if ("VND".equals(currency)) {
            symbols.setGroupingSeparator('.');
        } else {
            symbols.setGroupingSeparator(',');
        }
        symbols.setDecimalSeparator('.');
        DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
        // Thêm dòng này để format đẹp hơn cho số nguyên và số thập phân ngắn
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(amount);
    }

    public void setUSDtoVND(double USDtoVND) {
        this.USDtoVND = USDtoVND;
        this.VNDtoUSD = (USDtoVND != 0) ? (1/USDtoVND) : 0; // Cập nhật cả tỷ giá ngược lại
    }

    public double getUERtoVND() { // Giữ UER
        return UERtoVND;
    }

    public void setUERtoVND(double UERtoVND) { // Giữ UER
        this.UERtoVND = UERtoVND;
        this.VNDtoUER = (UERtoVND != 0) ? (1/UERtoVND) : 0;
    }

    public double getCNYtoVND() {
        return CNYtoVND;
    }

    public void setCNYtoVND(double CNYtoVND) {
        this.CNYtoVND = CNYtoVND;
        this.VNDtoCNY = (CNYtoVND != 0) ? (1/CNYtoVND) : 0;
    }

    public double getVNDtoUER() { // Giữ UER
        return VNDtoUER;
    }

    public void setVNDtoUER(double VNDtoUER) { // Giữ UER
        this.VNDtoUER = VNDtoUER;
        this.UERtoVND = (VNDtoUER != 0) ? (1/VNDtoUER) : 0;
    }

    public double getVNDtoCNY() {
        return VNDtoCNY;
    }

    public void setVNDtoCNY(double VNDtoCNY) {
        this.VNDtoCNY = VNDtoCNY;
        this.CNYtoVND = (VNDtoCNY != 0) ? (1/VNDtoCNY) : 0;
    }

    public double getVNDtoUSD() {
        return VNDtoUSD;
    }

    public void setVNDtoUSD(double VNDtoUSD) {
        this.VNDtoUSD = VNDtoUSD;
        this.USDtoVND = (VNDtoUSD != 0) ? (1/VNDtoUSD) : 0;
    }
}