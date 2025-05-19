package com.example.qlct.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qlct.Category_Add;
import com.example.qlct.Home.Home_My_wallets;
import com.example.qlct.Home.Home_New_wallet;
import com.example.qlct.Home.Home_TheGiaoDich;
import com.example.qlct.Home.Home_TheGiaoDich_Adapter;
import com.example.qlct.Home.Home_Wallet_Information;
import com.example.qlct.Home_TheTopSpent;
import com.example.qlct.Home_TheTopSpent_Adapter;
import com.example.qlct.Notification.Notification_activity;
import com.example.qlct.R;
import com.example.qlct.doitiente;
import com.example.qlct.Transaction.TransactionDetail;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

// Mock data classes to replace API entities
class Category {
    String picture;
    String name;

    Category(String picture, String name) {
        this.picture = picture;
        this.name = name;
    }
}

class Wallet {
    String name;
    String amount;
    String currency_unit;

    Wallet(String name, String amount, String currency_unit) {
        this.name = name;
        this.amount = amount;
        this.currency_unit = currency_unit;
    }
}

class GetAllTransactionsEntity_quyen {
    String transaction_date;
    String transaction_type;
    String amount;
    String currency_unit;
    String notes;
    Category category;
    Wallet wallet;

    GetAllTransactionsEntity_quyen(String transaction_date, String transaction_type, String amount,
                                   String currency_unit, String notes, Category category, Wallet wallet) {
        this.transaction_date = transaction_date;
        this.transaction_type = transaction_type;
        this.amount = amount;
        this.currency_unit = currency_unit;
        this.notes = notes;
        this.category = category;
        this.wallet = wallet;
    }
}

class GetAllWalletsEntity {
    String name;
    String amount;
    String currency_unit;

    GetAllWalletsEntity(String name, String amount, String currency_unit) {
        this.name = name;
        this.amount = amount;
        this.currency_unit = currency_unit;
    }
}

public class Home_fragment extends Fragment {

    ListView listView;
    String TenVi = "Total";
    ArrayList<Home_TheGiaoDich> theGiaoDichList;
    ArrayList<Home_TheGiaoDich> theGiaoDichList2;
    Home_TheGiaoDich_Adapter theGiaoDichAdap;
    ArrayList<Home_TheTopSpent> theTopSpentList;
    ArrayList<Home_TheTopSpent> theTopSpentList2;
    Home_TheTopSpent_Adapter theTopSpentAdap;

    // Mock data generation
    private ArrayList<GetAllTransactionsEntity_quyen> getMockTransactions() {
        ArrayList<GetAllTransactionsEntity_quyen> transactions = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        // Current date and previous month/week for realistic dates
        Calendar now = Calendar.getInstance();
        Calendar prevMonth = (Calendar) now.clone();
        prevMonth.add(Calendar.MONTH, -1);
        Calendar prevWeek = (Calendar) now.clone();
        prevWeek.add(Calendar.WEEK_OF_YEAR, -1);

        // Mock wallets
        Wallet wallet1 = new Wallet("Personal", "5000000", "VND");
        Wallet wallet2 = new Wallet("Savings", "200", "USD");
        Wallet wallet3 = new Wallet("Travel", "150", "EUR");

        // Mock categories
        Category food = new Category("food_icon", "Food");
        Category transport = new Category("transport_icon", "Transport");
        Category salary = new Category("salary_icon", "Salary");
        Category shopping = new Category("shopping_icon", "Shopping");

        // Transactions for current month
        transactions.add(new GetAllTransactionsEntity_quyen(
                sdf.format(now.getTime()), "EXPENSE", "100000", "VND", "Lunch", food, wallet1));
        transactions.add(new GetAllTransactionsEntity_quyen(
                sdf.format(now.getTime()), "INCOME", "5000000", "VND", "Salary", salary, wallet1));
        transactions.add(new GetAllTransactionsEntity_quyen(
                sdf.format(now.getTime()), "EXPENSE", "50", "USD", "Taxi", transport, wallet2));
        transactions.add(new GetAllTransactionsEntity_quyen(
                sdf.format(now.getTime()), "TRANSFER", "1000000", "VND", "Transfer to Savings", null, wallet1));

        // Transactions for previous month
        transactions.add(new GetAllTransactionsEntity_quyen(
                sdf.format(prevMonth.getTime()), "EXPENSE", "200000", "VND", "Dinner", food, wallet1));
        transactions.add(new GetAllTransactionsEntity_quyen(
                sdf.format(prevMonth.getTime()), "EXPENSE", "80", "EUR", "Clothes", shopping, wallet3));

        // Transactions for previous week
        transactions.add(new GetAllTransactionsEntity_quyen(
                sdf.format(prevWeek.getTime()), "EXPENSE", "150000", "VND", "Bus", transport, wallet1));
        transactions.add(new GetAllTransactionsEntity_quyen(
                sdf.format(prevWeek.getTime()), "INCOME", "100", "USD", "Freelance", salary, wallet2));

        return transactions;
    }

    private ArrayList<GetAllWalletsEntity> getMockWallets() {
        ArrayList<GetAllWalletsEntity> wallets = new ArrayList<>();
        wallets.add(new GetAllWalletsEntity("Personal", "5000000", "VND"));
        wallets.add(new GetAllWalletsEntity("Savings", "200", "USD"));
        wallets.add(new GetAllWalletsEntity("Travel", "150", "EUR"));
        return wallets;
    }

    private void Anhxa2() throws ParseException {
        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        theTopSpentList2 = new ArrayList<>();
        theTopSpentList = new ArrayList<>();
        double sumtien = 0;

        for (int i = 0; i < theGiaoDichList.size(); i++) {
            Date transactionDate = sdf.parse(theGiaoDichList.get(i).getNgayThang());
            Calendar transactionCalendar = Calendar.getInstance();
            transactionCalendar.setTime(transactionDate);

            int transactionMonth = transactionCalendar.get(Calendar.MONTH);
            int transactionYear = transactionCalendar.get(Calendar.YEAR);
            if (transactionYear != currentYear || transactionMonth != currentMonth) {
                continue;
            }
            int isadd = 0;
            if (theGiaoDichList.get(i).Typee.equals("TRANSFER") || theGiaoDichList.get(i).Typee.equals("INCOME")) {
                continue;
            }

            for (int j = 0; j < theTopSpentList.size(); j++) {
                doitiente doitien = new doitiente();
                if (theGiaoDichList.get(i).getTenGiaoDich().equals(theTopSpentList.get(j).getTenCategory())) {
                    String sotienmoi = theGiaoDichList.get(i).getSoTien();
                    double sotien = Double.parseDouble(sotienmoi);

                    if (theGiaoDichList.get(i).getCurrencyUnit().equals(" đ")) {
                        sotien = sotien;
                        sumtien += sotien;
                    } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" $")) {
                        sotien = sotien * doitien.getUSDtoVND();
                        sumtien += sotien;
                    } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" €")) {
                        sotien = sotien * doitien.getUERtoVND();
                        sumtien += sotien;
                    } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" ¥")) {
                        sotien = sotien * doitien.getCNYtoVND();
                        sumtien += sotien;
                    }

                    String SoTienCu = theTopSpentList.get(j).getSoTien();
                    double sotienCu = Double.parseDouble(SoTienCu);
                    theTopSpentList.get(j).setSoTien(String.valueOf(sotienCu + sotien));
                    isadd = 1;
                }
            }
            if (isadd == 0) {
                String sotienmoi = theGiaoDichList.get(i).getSoTien();
                double sotien = Double.parseDouble(sotienmoi);
                if (theGiaoDichList.get(i).getCurrencyUnit().equals(" đ")) {
                    sotien = sotien;
                    sumtien += sotien;
                } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" $")) {
                    sotien = sotien * doitien.getUSDtoVND();
                    sumtien += sotien;
                } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" €")) {
                    sotien = sotien * doitien.getUERtoVND();
                    sumtien += sotien;
                } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" ¥")) {
                    sotien = sotien * doitien.getCNYtoVND();
                    sumtien += sotien;
                }

                theTopSpentList.add(new Home_TheTopSpent(theGiaoDichList.get(i).getHinhAnh(),
                        theGiaoDichList.get(i).getTenGiaoDich(), String.valueOf(sotien)));
            }
        }

        for (int i2 = 0; i2 < theTopSpentList.size(); i2++) {
            double tienvi = Double.parseDouble(theTopSpentList.get(i2).getSoTien());
            theTopSpentList.get(i2).setSoTien(doitien.formatValue(tienvi) + " đ");
            double percent = (tienvi / sumtien) * 100;
            theTopSpentList.get(i2).setPercent(String.format("%.2f", percent) + "%");
        }

        Collections.sort(theTopSpentList, new Comparator<Home_TheTopSpent>() {
            @Override
            public int compare(Home_TheTopSpent o1, Home_TheTopSpent o2) {
                String s1 = o1.getPercent().replace(",", ".");
                String s2 = o2.getPercent().replace(",", ".");
                double percent1 = Double.parseDouble(s1.replace("%", ""));
                double percent2 = Double.parseDouble(s2.replace("%", ""));
                return Double.compare(percent2, percent1);
            }
        });

        theTopSpentList2 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (i < theTopSpentList.size()) {
                theTopSpentList2.add(theTopSpentList.get(i));
            }
        }
    }

    private void Anhxa3() throws ParseException {
        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);
        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        theTopSpentList2 = new ArrayList<>();
        theTopSpentList = new ArrayList<>();
        double sumtien = 0;

        for (int i = 0; i < theGiaoDichList.size(); i++) {
            Date transactionDate = sdf.parse(theGiaoDichList.get(i).getNgayThang());
            Calendar transactionCalendar = Calendar.getInstance();
            transactionCalendar.setTime(transactionDate);

            int transactionMonth = transactionCalendar.get(Calendar.MONTH);
            int transactionYear = transactionCalendar.get(Calendar.YEAR);
            int transactionWeek = transactionCalendar.get(Calendar.WEEK_OF_YEAR);
            if (transactionYear != currentYear || transactionMonth != currentMonth || transactionWeek != currentWeek) {
                continue;
            }
            int isadd = 0;
            if (theGiaoDichList.get(i).Typee.equals("TRANSFER") || theGiaoDichList.get(i).Typee.equals("INCOME")) {
                continue;
            }

            for (int j = 0; j < theTopSpentList.size(); j++) {
                doitiente doitien = new doitiente();
                if (theGiaoDichList.get(i).getTenGiaoDich().equals(theTopSpentList.get(j).getTenCategory())) {
                    String sotienmoi = theGiaoDichList.get(i).getSoTien();
                    double sotien = Double.parseDouble(sotienmoi);

                    if (theGiaoDichList.get(i).getCurrencyUnit().equals(" đ")) {
                        sotien = sotien;
                        sumtien += sotien;
                    } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" $")) {
                        sotien = sotien * doitien.getUSDtoVND();
                        sumtien += sotien;
                    } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" €")) {
                        sotien = sotien * doitien.getUERtoVND();
                        sumtien += sotien;
                    } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" ¥")) {
                        sotien = sotien * doitien.getCNYtoVND();
                        sumtien += sotien;
                    }

                    String SoTienCu = theTopSpentList.get(j).getSoTien();
                    double sotienCu = Double.parseDouble(SoTienCu);
                    theTopSpentList.get(j).setSoTien(String.valueOf(sotienCu + sotien));
                    isadd = 1;
                }
            }
            if (isadd == 0) {
                String sotienmoi = theGiaoDichList.get(i).getSoTien();
                double sotien = Double.parseDouble(sotienmoi);
                if (theGiaoDichList.get(i).getCurrencyUnit().equals(" đ")) {
                    sotien = sotien;
                    sumtien += sotien;
                } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" $")) {
                    sotien = sotien * doitien.getUSDtoVND();
                    sumtien += sotien;
                } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" €")) {
                    sotien = sotien * doitien.getUERtoVND();
                    sumtien += sotien;
                } else if (theGiaoDichList.get(i).getCurrencyUnit().equals(" ¥")) {
                    sotien = sotien * doitien.getCNYtoVND();
                    sumtien += sotien;
                }

                theTopSpentList.add(new Home_TheTopSpent(theGiaoDichList.get(i).getHinhAnh(),
                        theGiaoDichList.get(i).getTenGiaoDich(), String.valueOf(sotien)));
            }
        }

        for (int i2 = 0; i2 < theTopSpentList.size(); i2++) {
            double tienvi = Double.parseDouble(theTopSpentList.get(i2).getSoTien());
            theTopSpentList.get(i2).setSoTien(doitien.formatValue(tienvi) + " đ");
            double percent = (tienvi / sumtien) * 100;
            theTopSpentList.get(i2).setPercent(String.format("%.2f", percent) + "%");
        }

        Collections.sort(theTopSpentList, new Comparator<Home_TheTopSpent>() {
            @Override
            public int compare(Home_TheTopSpent o1, Home_TheTopSpent o2) {
                String s1 = o1.getPercent().replace(",", ".");
                String s2 = o2.getPercent().replace(",", ".");
                double percent1 = Double.parseDouble(s1.replace("%", ""));
                double percent2 = Double.parseDouble(s2.replace("%", ""));
                return Double.compare(percent2, percent1);
            }
        });

        theTopSpentList2 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (i < theTopSpentList.size()) {
                theTopSpentList2.add(theTopSpentList.get(i));
            }
        }
    }

    private void Anhxa() {
        try {
            theGiaoDichList = new ArrayList<>();
            ArrayList<GetAllTransactionsEntity_quyen> parseAPIList = getMockTransactions();
            ArrayList<GetAllWalletsEntity> walletsEntities = getMockWallets();

            for (GetAllTransactionsEntity_quyen item : parseAPIList) {
                String dv = " đ";
                if (item.currency_unit.equals("USD")) {
                    dv = " $";
                } else if (item.currency_unit.equals("EUR")) {
                    dv = " €";
                } else if (item.currency_unit.equals("CNY")) {
                    dv = " ¥";
                }
                if (item.transaction_type.equals("TRANSFER")) {
                    // Skip transfers
                } else {
                    if (TenVi.equals("Total") || TenVi.equals(item.wallet.name)) {
                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = originalFormat.parse(item.transaction_date);
                        String formattedDate = targetFormat.format(date);

                        theGiaoDichList.add(new Home_TheGiaoDich(item.category.picture, item.category.name,
                                item.amount, dv, formattedDate, item.notes, item.wallet.name, item.transaction_type));
                    }
                }
            }
            Log.d("Get_wallet_data_object", theGiaoDichList.toString());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Log.d("abcz", e.getMessage());
            e.printStackTrace();
        }
        theGiaoDichList2 = new ArrayList<>();
        if (theGiaoDichList.size() == 0) {
            // No transactions
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Collections.sort(theGiaoDichList, new Comparator<Home_TheGiaoDich>() {
                    public int compare(Home_TheGiaoDich o1, Home_TheGiaoDich o2) {
                        try {
                            return sdf.parse(o2.getNgayThang()).compareTo(sdf.parse(o1.getNgayThang()));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                Log.d("abcz", e.getMessage());
                e.printStackTrace();
            }

            for (int i = 0; i < theGiaoDichList.size(); i++) {
                if (i < 3) {
                    theGiaoDichList2.add(theGiaoDichList.get(i));
                }
            }
        }
    }

    double TongTien = 0;
    doitiente doitien = new doitiente();

    public void loadData() {
    }

    double ammount = 0;
    String currency_unit = "đ";
    double tongsovi = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            TenVi = arguments.getString("tenvi");
            ammount = arguments.getDouble("ammount");
            currency_unit = arguments.getString("currency_unit");
            tongsovi = arguments.getDouble("tongsovi");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflaterloading = getLayoutInflater();
        builder.setView(inflaterloading.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);
        AlertDialog load = builder.create();

        Anhxa();

        try {
            tongtranthangtruoc = 0;
            tongtranthangnay = 0;
            tongtrantuannay = 0;
            tongtrantuantruoc = 0;
            Anhxa2();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TongTien();

        View view = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        listView = view.findViewById(R.id.listView);
        ImageView imageView = view.findViewById(R.id.optionsVi);
        imageView.setVisibility(View.VISIBLE);
        ImageView unseen = view.findViewById(R.id.unseen);
        TextView text = view.findViewById(R.id.total_blance);
        ImageView noti = view.findViewById(R.id.noti);
        TextView textView = view.findViewById(R.id.tenVi);
        textView.setText(TenVi);
        if (TenVi.equals("Total")) {
            textView.setTextColor(Color.parseColor("#1EABED"));
        } else {
            textView.setTextColor(Color.GRAY);
        }

        TextView sotien = view.findViewById(R.id.sotiensodo);
        sotien.setText(doitien.formatValue(tongtranthangnay) + " đ");
        ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);

        BarChart barChart = view.findViewById(R.id.barChart);
        barChart.clear();
        barChart.setTouchEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);

        double maxValue = Math.max(tongtranthangtruoc, tongtranthangnay);
        double greatestCommonDivisor = Math.pow(10, Math.floor(Math.log10(maxValue) / 3) * 3);
        float scaledTongtranthangtruoc = (float) (tongtranthangtruoc / greatestCommonDivisor);
        float scaledTongtranthangnay = (float) (tongtranthangnay / greatestCommonDivisor);
        float chuan = scaledTongtranthangtruoc;
        while (scaledTongtranthangnay < chuan) {
            scaledTongtranthangnay = scaledTongtranthangnay * 10;
            scaledTongtranthangtruoc = scaledTongtranthangtruoc * 10;
        }

        ArrayList<BarEntry> dataValues1 = new ArrayList<>();
        dataValues1.add(new BarEntry(0, scaledTongtranthangtruoc));
        ArrayList<BarEntry> dataValues2 = new ArrayList<>();
        dataValues2.add(new BarEntry(1, scaledTongtranthangnay));

        BarDataSet barDataSet1 = new BarDataSet(dataValues1, "Last month");
        barDataSet1.setColor(Color.parseColor("#EE0E36"));
        barDataSet1.setValueTextSize(12f);
        BarDataSet barDataSet2 = new BarDataSet(dataValues2, "This month");
        barDataSet2.setColor(Color.parseColor("#5CC2F2"));
        barDataSet2.setValueTextSize(12f);

        BarData data = new BarData(barDataSet1, barDataSet2);
        float groupSpace = 0f;
        float barSpace = 0.3f;
        float barWidth = 0.7f;

        data.setBarWidth(barWidth);
        barChart.setData(data);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(2);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setAxisMinimum(0);

        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.invalidate();

        String unit;
        if (greatestCommonDivisor == Math.pow(10, 3)) {
            unit = "K";
        } else if (greatestCommonDivisor == Math.pow(10, 6)) {
            unit = "M";
        } else {
            unit = "";
        }

        TextView totalbalance = view.findViewById(R.id.total_blance);
        if (ammount < 0) {
            totalbalance.setTextColor(Color.RED);
        } else {
            totalbalance.setTextColor(Color.parseColor("#459244"));
        }
        totalbalance.setText(doitien.formatValue(ammount) + currency_unit);
        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load.show();
                Intent intent = new Intent(getActivity(), Notification_activity.class);
                startActivity(intent);
                load.dismiss();
            }
        });
        ImageView optionVi = view.findViewById(R.id.optionsVi);
        optionVi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load.show();
                Intent intent = new Intent(getActivity(), Home_Wallet_Information.class);
                intent.putExtra("name", TenVi);
                intent.putExtra("spec", "spec");
                startActivity(intent);
                load.dismiss();
            }
        });

        unseen.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (text.getText().toString().equals("**********")) {
                    unseen.setBackgroundResource(R.drawable.eye);
                    text.setText(doitien.formatValue(ammount) + " đ");
                } else {
                    text.setText("**********");
                    unseen.setBackgroundResource(R.drawable.eyedown);
                }
            }
        });
        TextView month = view.findViewById(R.id.month);
        TextView year = view.findViewById(R.id.year);

        ColorStateList colorStateList3 = ColorStateList.valueOf(Color.WHITE);
        month.setBackgroundTintList(colorStateList3);
        ColorStateList colorStateList2 = ColorStateList.valueOf(Color.parseColor("#ACACAC"));
        year.setBackgroundTintList(colorStateList2);

        sotien.setText(doitien.formatValue(tongtranthangnay) + " đ");
        month.setTextColor(Color.parseColor("#5CC2F2"));
        year.setTextColor(Color.BLACK);

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Anhxa2();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                month.setTextColor(Color.parseColor("#5CC2F2"));
                year.setTextColor(Color.BLACK);

                TextView tstm = view.findViewById(R.id.tstm);
                tstm.setText("Total spent this month");
                TextView sotien = view.findViewById(R.id.sotiensodo);
                sotien.setText(doitien.formatValue(tongtranthangnay) + " đ");
                ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
                month.setBackgroundTintList(colorStateList);
                ColorStateList colorStateList2 = ColorStateList.valueOf(Color.parseColor("#ACACAC"));
                year.setBackgroundTintList(colorStateList2);
                BarChart barChart = view.findViewById(R.id.barChart);
                barChart.clear();
                barChart.setTouchEnabled(false);
                barChart.setDragEnabled(false);
                barChart.setScaleEnabled(false);
                barChart.setPinchZoom(false);

                double maxValue = Math.max(tongtranthangtruoc, tongtranthangnay);
                double greatestCommonDivisor = Math.pow(10, Math.floor(Math.log10(maxValue) / 3) * 3);
                float scaledTongtranthangtruoc = (float) (tongtranthangtruoc / greatestCommonDivisor);
                float scaledTongtranthangnay = (float) (tongtranthangnay / greatestCommonDivisor);
                float chuan = scaledTongtranthangtruoc;
                while (scaledTongtranthangnay < chuan) {
                    scaledTongtranthangnay = scaledTongtranthangnay * 10;
                    scaledTongtranthangtruoc = scaledTongtranthangtruoc * 10;
                }

                ArrayList<BarEntry> dataValues1 = new ArrayList<>();
                dataValues1.add(new BarEntry(0, scaledTongtranthangtruoc));
                ArrayList<BarEntry> dataValues2 = new ArrayList<>();
                dataValues2.add(new BarEntry(1, scaledTongtranthangnay));

                BarDataSet barDataSet1 = new BarDataSet(dataValues1, "Last month");
                barDataSet1.setColor(Color.parseColor("#EE0E36"));
                barDataSet1.setValueTextSize(12f);
                BarDataSet barDataSet2 = new BarDataSet(dataValues2, "This month");
                barDataSet2.setColor(Color.parseColor("#5CC2F2"));
                barDataSet2.setValueTextSize(12f);

                BarData data = new BarData(barDataSet1, barDataSet2);
                float groupSpace = 0f;
                float barSpace = 0.3f;
                float barWidth = 0.7f;

                data.setBarWidth(barWidth);
                barChart.setData(data);
                barChart.groupBars(0, groupSpace, barSpace);
                barChart.getXAxis().setAxisMinimum(0);
                barChart.getXAxis().setAxisMaximum(2);
                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setAxisMinimum(0);
                Description description = new Description();
                description.setText("");
                barChart.setDescription(description);
                barChart.invalidate();

                String unit;
                if (greatestCommonDivisor == Math.pow(10, 3)) {
                    unit = "K";
                } else if (greatestCommonDivisor == Math.pow(10, 6)) {
                    unit = "M";
                } else {
                    unit = "";
                }
                ListView listView2 = view.findViewById(R.id.listView2);
                theTopSpentAdap = new Home_TheTopSpent_Adapter(getContext(), R.layout.home_dong_topspent, theTopSpentList2);
                listView2.setAdapter(theTopSpentAdap);
            }
        });
        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Anhxa3();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                TextView tstm = view.findViewById(R.id.tstm);
                tstm.setText("Total spent this week");
                year.setTextColor(Color.parseColor("#5CC2F2"));
                month.setTextColor(Color.BLACK);

                TextView sotien = view.findViewById(R.id.sotiensodo);
                sotien.setText(doitien.formatValue(tongtrantuannay) + " đ");
                ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
                year.setBackgroundTintList(colorStateList);
                ColorStateList colorStateList2 = ColorStateList.valueOf(Color.parseColor("#ACACAC"));
                month.setBackgroundTintList(colorStateList2);

                BarChart barChart = view.findViewById(R.id.barChart);
                barChart.setTouchEnabled(false);
                barChart.setDragEnabled(false);
                barChart.setScaleEnabled(false);
                barChart.setPinchZoom(false);

                double maxValue = Math.max(Math.abs(tongtrantuantruoc), Math.abs(tongtrantuannay));
                double greatestCommonDivisor = Math.pow(10, Math.floor(Math.log10(maxValue) / 3) * 3);
                float scaledTongtranthangtruoc = (float) (tongtrantuantruoc / greatestCommonDivisor);
                float scaledTongtranthangnay = (float) (tongtrantuannay / greatestCommonDivisor);
                float chuan = scaledTongtranthangtruoc;
                while (scaledTongtranthangnay < chuan) {
                    scaledTongtranthangnay = scaledTongtranthangnay * 10;
                    scaledTongtranthangtruoc = scaledTongtranthangtruoc * 10;
                }

                ArrayList<BarEntry> dataValues1 = new ArrayList<>();
                dataValues1.add(new BarEntry(0, scaledTongtranthangtruoc));
                ArrayList<BarEntry> dataValues2 = new ArrayList<>();
                dataValues2.add(new BarEntry(1, scaledTongtranthangnay));

                BarDataSet barDataSet1 = new BarDataSet(dataValues1, "Last week");
                barDataSet1.setColor(Color.parseColor("#EE0E36"));
                barDataSet1.setValueTextSize(12f);
                BarDataSet barDataSet2 = new BarDataSet(dataValues2, "This week");
                barDataSet2.setColor(Color.parseColor("#5CC2F2"));
                barDataSet2.setValueTextSize(12f);

                BarData data = new BarData(barDataSet1, barDataSet2);
                float groupSpace = 0f;
                float barSpace = 0.3f;
                float barWidth = 0.7f;

                data.setBarWidth(barWidth);
                barChart.setData(data);
                barChart.groupBars(0, groupSpace, barSpace);
                barChart.getXAxis().setAxisMinimum(0);
                barChart.getXAxis().setAxisMaximum(2);
                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setAxisMinimum(0);
                Description description = new Description();
                description.setText("");
                barChart.setDescription(description);
                barChart.invalidate();

                String unit;
                if (greatestCommonDivisor == Math.pow(10, 3)) {
                    unit = "K";
                } else if (greatestCommonDivisor == Math.pow(10, 6)) {
                    unit = "M";
                } else {
                    unit = "";
                }
                ListView listView2 = view.findViewById(R.id.listView2);
                theTopSpentAdap = new Home_TheTopSpent_Adapter(getContext(), R.layout.home_dong_topspent, theTopSpentList2);
                listView2.setAdapter(theTopSpentAdap);
            }
        });

        TextView seeAll = view.findViewById(R.id.Seeall);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seeAll.getText().toString().equals("See all")) {
                    load.show();
                    Intent intent = new Intent(getActivity(), Home_My_wallets.class);
                    TextView vi = view.findViewById(R.id.tenVi);
                    intent.putExtra("viduocchon", vi.getText());
                    startActivity(intent);
                    load.dismiss();
                } else {
                    Intent intent = new Intent(getActivity(), Home_New_wallet.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

        TextView showmore = view.findViewById(R.id.showmore);
        showmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (theGiaoDichList.size() == 0) {
                    return;
                }
                Intent ShowMoreIntent = new Intent(getActivity(), TransactionDetail.class);
                startActivity(ShowMoreIntent);
            }
        });
        if (theGiaoDichList.size() == 0) {
            showmore.setText("No transaction");
            showmore.setTextColor(Color.GRAY);
            listView.setVisibility(View.INVISIBLE);
        } else {
            theGiaoDichAdap = new Home_TheGiaoDich_Adapter(getContext(), R.layout.home_dong_giao_dich, theGiaoDichList2);
            listView.setAdapter(theGiaoDichAdap);
        }
        setListViewHeightBasedOnChildren(listView);
        ListView listView2 = view.findViewById(R.id.listView2);
        theTopSpentAdap = new Home_TheTopSpent_Adapter(getContext(), R.layout.home_dong_topspent, theTopSpentList2);
        listView2.setAdapter(theTopSpentAdap);
        setListViewHeightBasedOnChildren(listView2);
        if (tongsovi == 0) {
            TextView tenvi = view.findViewById(R.id.tenVi);
            tenvi.setText("No wallet");
            tenvi.setTextColor(Color.GRAY);
            TextView seeall = view.findViewById(R.id.Seeall);
            seeall.setText("ADD NEW");
            ImageView op = view.findViewById(R.id.optionsVi);
            op.setVisibility(View.INVISIBLE);
        } else {
            if (TenVi.equals("Total")) {
                ImageView optionvi = view.findViewById(R.id.optionsVi);
                optionvi.setVisibility(View.INVISIBLE);
            }
        }

        return view;
    }

    double tongtranthangnay = 0;
    double tongtranthangtruoc = 0;
    double tongtrantuannay = 0;
    double tongtrantuantruoc = 0;

    public void TongTien() {
        try {
            Calendar now = Calendar.getInstance();
            int currentMonth = now.get(Calendar.MONTH);
            int currentYear = now.get(Calendar.YEAR);
            int currentWeek = now.get(Calendar.WEEK_OF_YEAR);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (Home_TheGiaoDich item : theGiaoDichList) {
                Date transactionDate = sdf.parse(item.getNgayThang());
                Calendar transactionCalendar = Calendar.getInstance();
                transactionCalendar.setTime(transactionDate);

                int transactionMonth = transactionCalendar.get(Calendar.MONTH);
                int transactionYear = transactionCalendar.get(Calendar.YEAR);
                int transactionWeek = transactionCalendar.get(Calendar.WEEK_OF_YEAR);

                double soTien;
                String currencyUnit = item.getCurrencyUnit().trim();
                String amount = item.getSoTien().replace(",", "");

                switch (currencyUnit) {
                    case "đ":
                        soTien = Double.parseDouble(amount);
                        break;
                    case "$":
                        soTien = Double.parseDouble(amount) * doitien.getUSDtoVND();
                        break;
                    case "€":
                        soTien = Double.parseDouble(amount) * doitien.getUERtoVND();
                        break;
                    case "¥":
                        soTien = Double.parseDouble(amount) * doitien.getCNYtoVND();
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid currency unit: " + currencyUnit);
                }

                if (item.Typee.equals("EXPENSE")) {
                    if (transactionYear == currentYear) {
                        if (transactionMonth == currentMonth) {
                            tongtranthangnay += soTien;
                            if (transactionWeek == currentWeek) {
                                tongtrantuannay += soTien;
                            } else if (transactionWeek == currentWeek - 1) {
                                tongtrantuantruoc += soTien;
                            }
                        } else if (transactionMonth == currentMonth - 1) {
                            tongtranthangtruoc += soTien;
                        }
                    }
                }
            }

            Log.d("TongTien", "Tổng giao dịch tháng này: " + tongtranthangnay);
            Log.d("TongTien", "Tổng giao dịch tháng trước: " + tongtranthangtruoc);
            Log.d("TongTien", "Tổng giao dịch tuần này: " + tongtrantuannay);
            Log.d("TongTien", "Tổng giao dịch tuần trước: " + tongtrantuantruoc);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
        TextView tenvi = getView().findViewById(R.id.tenVi);
        TextView totalbalance = getView().findViewById(R.id.total_blance);

        if (requestCode == 1) {
            try {
                ArrayList<GetAllWalletsEntity> parseAPIList = getMockWallets();
                for (GetAllWalletsEntity item : parseAPIList) {
                    tenvi.setText(item.name);
                    if (item.currency_unit.equals("VND")) {
                        totalbalance.setText(doitien.formatValue(Double.parseDouble(item.amount)) + " đ");
                    } else if (item.currency_unit.equals("USD")) {
                        totalbalance.setText(doitien.formatValue(Double.parseDouble(item.amount)) + " $");
                    } else if (item.currency_unit.equals("EUR")) {
                        totalbalance.setText(doitien.formatValue(Double.parseDouble(item.amount)) + " €");
                    } else if (item.currency_unit.equals("CNY")) {
                        totalbalance.setText(doitien.formatValue(Double.parseDouble(item.amount)) + " ¥");
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tenvi.getText().toString().equals("No wallet")) {
            } else {
                TextView seeall = getView().findViewById(R.id.Seeall);
                seeall.setText("See all");
                ImageView op = getView().findViewById(R.id.optionsVi);
                op.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}