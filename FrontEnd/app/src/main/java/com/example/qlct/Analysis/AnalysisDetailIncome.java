package com.example.qlct.Analysis;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qlct.API_Entity.GetAllCategoryEntity;
import com.example.qlct.API_Entity.GetAllTransactionsEntity_quyen;
import com.example.qlct.R;
import com.example.qlct.doitiente;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AnalysisDetailIncome extends AppCompatActivity {
    ImageView exit;
    List<AnalysisExpense> list;
    ListView listView;
    PieChart pieChart;
    ArrayList<GetAllTransactionsEntity_quyen> listTransactions;
    ArrayList<GetAllTransactionsEntity_quyen> listIncome;
    ArrayList<GetAllCategoryEntity> listCategory;
    String id_wallet;
    TextView total_icome;
    String currency;
    ArrayList<Integer> colors;
    String date;
    TextView date_lbl;
    doitiente doitiente = new doitiente();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analysis_detail_income);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            listTransactions = (ArrayList<GetAllTransactionsEntity_quyen>) bundle.getSerializable("listTransactions");
            id_wallet = bundle.getString("id_wallet");
            listCategory = (ArrayList<GetAllCategoryEntity>) bundle.getSerializable("listCategory");
            currency = bundle.getString("currency");
            date = bundle.getString("date");
            Log.d("AnalysisDetailIncome", "onCreate - Transactions: " + (listTransactions != null ? listTransactions.size() : "null") +
                    ", Wallet ID: " + id_wallet +
                    ", Categories: " + (listCategory != null ? listCategory.size() : "null") +
                    ", Date: " + date +
                    ", Currency: " + currency);
        } else {
            Log.e("AnalysisDetailIncome", "Bundle is null");
            Toast.makeText(this, "Error: No data received", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        total_icome = findViewById(R.id.total_income);
        exit = findViewById(R.id.exit_Income);
        listView = findViewById(R.id.listvieww);
        pieChart = findViewById(R.id.piechart);
        date_lbl = findViewById(R.id.date_lbl);
        date_lbl.setText(date);

        exit.setOnClickListener(v -> finish());

        list = new ArrayList<>();
        SetUpPieChart();
        if (listIncome == null || listIncome.isEmpty()) {
            Log.w("AnalysisDetailIncome", "No incomes to display");
            Toast.makeText(this, "No income data available", Toast.LENGTH_SHORT).show();
            total_icome.setText("0 " + currency);
        } else {
            DoiDonVi();
            AnhXa();
        }

        Analysis_Expense_Adapter adapter = new Analysis_Expense_Adapter(list, this, R.layout.analysis_expense_list_item);
        listView.setAdapter(adapter);
    }

    void DoiDonVi() {
        for (GetAllTransactionsEntity_quyen transaction : listIncome) {
            if (id_wallet.equals("Total")) {
                try {
                    if (transaction.currency_unit.equals("VND"))
                        transaction.amount = String.valueOf(Float.parseFloat(transaction.amount));
                    else if (transaction.currency_unit.equals("USD"))
                        transaction.amount = String.valueOf(Float.parseFloat(transaction.amount) * doitiente.getUSDtoVND());
                    else if (transaction.currency_unit.equals("CNY"))
                        transaction.amount = String.valueOf(Float.parseFloat(transaction.amount) * doitiente.getCNYtoVND());
                    else if (transaction.currency_unit.equals("EUR"))
                        transaction.amount = String.valueOf(Float.parseFloat(transaction.amount) * doitiente.getUERtoVND());
                } catch (NumberFormatException e) {
                    Log.e("AnalysisDetailIncome", "Error parsing amount: " + transaction.amount + ", Error: " + e.getMessage());
                }
            }
        }
    }

    Double TinhPhanTram(String a, String b) {
        try {
            a = a.replace(",", ".");
            b = b.replace(",", ".");
            double valueA = convertStringToNumber(a);
            double valueB = convertStringToNumber(b);
            if (valueB == 0) return 0.0;
            return (valueA * 100) / valueB;
        } catch (Exception e) {
            Log.e("AnalysisDetailIncome", "Error in TinhPhanTram: " + e.getMessage());
            return 0.0;
        }
    }

    double convertStringToNumber(String str) {
        try {
            double value;
            char lastChar = str.charAt(str.length() - 1);
            switch (lastChar) {
                case 'K':
                    value = Double.parseDouble(str.substring(0, str.length() - 1)) * 1000;
                    break;
                case 'B':
                    value = Double.parseDouble(str.substring(0, str.length() - 1)) * 1000000000;
                    break;
                case 'M':
                    value = Double.parseDouble(str.substring(0, str.length() - 1)) * 1000000;
                    break;
                default:
                    value = Double.parseDouble(str);
                    break;
            }
            return value;
        } catch (Exception e) {
            Log.e("AnalysisDetailIncome", "Error in convertStringToNumber: " + str + ", Error: " + e.getMessage());
            return 0.0;
        }
    }

    void AnhXa() {
        for (int i = 0; i < listIncome.size(); i++) {
            int color = i < colors.size() ? colors.get(i) : colors.get(colors.size() - 1);
            double kq;
            try {
                kq = TinhPhanTram(doitiente.formatValue(Double.parseDouble(listIncome.get(i).amount)),
                        total_icome.getText().toString().substring(0, total_icome.getText().toString().length() - 2));
            } catch (Exception e) {
                Log.e("AnalysisDetailIncome", "Error calculating percentage: " + e.getMessage());
                kq = 0.0;
            }
            String picture = listIncome.get(i).category_id != null
                    ? getCategoryPictureById(listIncome.get(i).category_id)
                    : "android.resource://com.example.qlct/drawable/ic_transfer";
            String categoryName = listIncome.get(i).category_id != null
                    ? getCategoryNameById(listIncome.get(i).category_id)
                    : "Transfer";
            list.add(new AnalysisExpense(color, picture, categoryName, roundTwoDecimals(kq),
                    doitiente.formatValue(Double.parseDouble(listIncome.get(i).amount)), currency));
            Log.d("AnalysisDetailIncome", "Added income: " + categoryName + ", Amount: " + listIncome.get(i).amount + ", Picture: " + picture);
        }
    }

    void SetUpPieChart() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            String[] dateParts = date.split("-");
            int month = -1, year;
            if (date.length() == 4) {
                year = Integer.parseInt(date);
            } else {
                month = Integer.parseInt(dateParts[0]);
                year = Integer.parseInt(dateParts[1]);
            }

            listIncome = new ArrayList<>();
            if (id_wallet.equals("Total")) {
                for (GetAllTransactionsEntity_quyen transaction : listTransactions) {
                    Date transactionDate = format.parse(transaction.transaction_date);
                    calendar.setTime(transactionDate);
                    int transactionYear = calendar.get(Calendar.YEAR);
                    int transactionMonth = calendar.get(Calendar.MONTH) + 1;
                    if (transaction.transaction_type.equals("INCOME") && transactionYear == year && (month == -1 || transactionMonth == month)) {
                        listIncome.add(transaction);
                    }
                }
            } else {
                for (GetAllTransactionsEntity_quyen transaction : listTransactions) {
                    Date transactionDate = format.parse(transaction.transaction_date);
                    calendar.setTime(transactionDate);
                    int transactionYear = calendar.get(Calendar.YEAR);
                    int transactionMonth = calendar.get(Calendar.MONTH) + 1;
                    if ((transaction.transaction_type.equals("INCOME") && transaction.wallet_id.equals(id_wallet) && transactionYear == year && (month == -1 || transactionMonth == month)) ||
                            (transaction.transaction_type.equals("TRANSFER") && transaction.target_wallet_id.equals(id_wallet) && transactionYear == year && (month == -1 || transactionMonth == month))) {
                        listIncome.add(transaction);
                    }
                }
            }
            Log.d("AnalysisDetailIncome", "Filtered incomes: " + listIncome.size());

            Collections.sort(listIncome, new Comparator<GetAllTransactionsEntity_quyen>() {
                @Override
                public int compare(GetAllTransactionsEntity_quyen t1, GetAllTransactionsEntity_quyen t2) {
                    return Float.compare(Float.parseFloat(t2.amount), Float.parseFloat(t1.amount));
                }
            });
            AdjustList(listIncome);
            total_icome.setText(doitiente.formatValue(TotalIncome(listIncome)) + " " + currency);
            ArrayList<PieEntry> categories = new ArrayList<>();
            if (listIncome.size() == 0) {
                categories.add(new PieEntry(100, "No income"));
            } else if (listIncome.size() <= 4) {
                for (int i = 0; i < listIncome.size(); i++) {
                    if (listIncome.get(i).category_id != null)
                        categories.add(new PieEntry(Float.parseFloat(listIncome.get(i).amount), getCategoryNameById(listIncome.get(i).category_id)));
                    else
                        categories.add(new PieEntry(Float.parseFloat(listIncome.get(i).amount), "Transfer"));
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    if (listIncome.get(i).category_id != null)
                        categories.add(new PieEntry(Float.parseFloat(listIncome.get(i).amount), getCategoryNameById(listIncome.get(i).category_id)));
                    else
                        categories.add(new PieEntry(Float.parseFloat(listIncome.get(i).amount), "Transfer"));
                }
                float sum = 0;
                for (int i = 4; i < listIncome.size(); i++) {
                    sum += Float.parseFloat(listIncome.get(i).amount);
                }
                categories.add(new PieEntry(sum, "Others"));
            }

            colors = new ArrayList<>();
            colors.add(Color.parseColor("#FFFA8AA0"));
            colors.add(Color.parseColor("#FF5DC5E3"));
            colors.add(Color.parseColor("#FF65CBB6"));
            colors.add(Color.parseColor("#FFF9DE74"));
            colors.add(Color.parseColor("#FF89D889"));

            PieDataSet pieDataSet = new PieDataSet(categories, "");
            pieDataSet.setColors(colors);
            pieDataSet.setValueTextColor(R.color.black);
            pieDataSet.setValueTextSize(16f);
            pieDataSet.setLabel("");

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("Income (%)");
            pieChart.animate();
            pieChart.setUsePercentValues(true);
            pieChart.setDrawEntryLabels(false);

            Legend legend = pieChart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setFormSize(14f);
            legend.setTextSize(14f);
            legend.setDrawInside(false);
        } catch (Exception e) {
            Log.d("AnalysisDetailIncome", "Error in SetUpPieChart: " + e.getMessage());
        }
    }

    void AdjustList(ArrayList<GetAllTransactionsEntity_quyen> listIncome) {
        for (int i = 0; i < listIncome.size(); i++) {
            if (listIncome.get(i).category_id != null) {
                for (int j = i + 1; j < listIncome.size(); j++) {
                    if (listIncome.get(i).category_id.equals(listIncome.get(j).category_id)) {
                        listIncome.get(i).amount = String.valueOf(Float.parseFloat(listIncome.get(i).amount) + Float.parseFloat(listIncome.get(j).amount));
                        listIncome.remove(j);
                        j--;
                    }
                }
            } else {
                for (int j = i + 1; j < listIncome.size(); j++) {
                    if (listIncome.get(j).transaction_type.equals("TRANSFER")) {
                        listIncome.get(i).amount = String.valueOf(Float.parseFloat(listIncome.get(i).amount) + Float.parseFloat(listIncome.get(j).amount));
                        listIncome.remove(j);
                        j--;
                    }
                }
            }
        }
    }

    Double TotalIncome(ArrayList<GetAllTransactionsEntity_quyen> listIncome) {
        Double sum = 0.0;
        for (GetAllTransactionsEntity_quyen transaction : listIncome) {
            try {
                if (id_wallet.equals("Total")) {
                    if (transaction.currency_unit.equals("VND"))
                        sum += Float.parseFloat(transaction.amount);
                    else if (transaction.currency_unit.equals("USD"))
                        sum += Float.parseFloat(transaction.amount) * doitiente.getUSDtoVND();
                    else if (transaction.currency_unit.equals("CNY"))
                        sum += Float.parseFloat(transaction.amount) * doitiente.getCNYtoVND();
                    else if (transaction.currency_unit.equals("EUR"))
                        sum += Float.parseFloat(transaction.amount) * doitiente.getUERtoVND();
                } else {
                    sum += Float.parseFloat(transaction.amount);
                }
            } catch (NumberFormatException e) {
                Log.e("AnalysisDetailIncome", "Error parsing amount in TotalIncome: " + transaction.amount + ", Error: " + e.getMessage());
            }
        }
        return sum;
    }

    String getCategoryNameById(String id) {
        if (id == null) return "Unknown";
        for (GetAllCategoryEntity category : listCategory) {
            if (category.id.equals(id)) {
                return category.name;
            }
        }
        return "Unknown";
    }

    String getCategoryPictureById(String id) {
        if (id == null) return "android.resource://com.example.qlct/drawable/ic_placeholder";
        for (GetAllCategoryEntity category : listCategory) {
            if (category.id.equals(id)) {
                return category.picture != null ? category.picture : "android.resource://com.example.qlct/drawable/ic_placeholder";
            }
        }
        return "android.resource://com.example.qlct/drawable/ic_placeholder";
    }

    public String formatString(String input) {
        String[] parts = input.split("\\.");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = sb.length() - 3; i > 0; i -= 3) {
            sb.insert(i, '.');
        }
        return sb.toString();
    }

    public double roundTwoDecimals(double d) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double convertStringToDouble(String input) {
        String processedInput = input.replace(".", "");
        double value = Double.parseDouble(processedInput);
        return value / 1000;
    }
}