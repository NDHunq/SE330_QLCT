package com.example.qlct.Analysis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.qlct.API_Entity.GetAllCategoryEntity;
import com.example.qlct.API_Entity.GetAllTransactionsEntity_quyen;
import com.example.qlct.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AnalysisExpenseFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    PieChart pieChart;
    ArrayList<GetAllTransactionsEntity_quyen> listTransactions;
    ArrayList<GetAllCategoryEntity> listCategory;
    String id_wallet;
    String currency;
    String date;

    public AnalysisExpenseFragment() {
    }

    public AnalysisExpenseFragment(ArrayList<GetAllTransactionsEntity_quyen> listTransactions, String id_wallet, ArrayList<GetAllCategoryEntity> listCategory, String currency, String date) {
        this.listTransactions = listTransactions;
        this.id_wallet = id_wallet;
        this.listCategory = listCategory;
        this.currency = currency;
        this.date = date;
    }

    public static AnalysisExpenseFragment newInstance(String param1, String param2) {
        AnalysisExpenseFragment fragment = new AnalysisExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis_expense, container, false);
        pieChart = view.findViewById(R.id.piechart);
        Log.d("AnalysisExpense", "onCreateView - Transactions: " + (listTransactions != null ? listTransactions.size() : "null") +
                ", Wallet ID: " + id_wallet +
                ", Categories: " + (listCategory != null ? listCategory.size() : "null") +
                ", Date: " + date +
                ", Currency: " + currency);

        SetUpPieChart();

        View detailView = view.findViewById(R.id.detail_exp);
        if (detailView == null) {
            Log.e("AnalysisExpense", "expense_detail view not found in layout");
            Toast.makeText(getContext(), "Error: expense_detail not found", Toast.LENGTH_LONG).show();
            return view;
        }

        detailView.setOnClickListener(v -> {
            try {
                 Log.d("AnalysisExpense", "Clicked expense_detail - Starting AnalysisDetailExpense with " +
                        "Transactions: " + (listTransactions != null ? listTransactions.size() : "null") +
                        ", Wallet ID: " + id_wallet);

                Intent intent = new Intent(getActivity(), AnalysisDetailExpense.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("listTransactions", listTransactions);
                bundle.putString("id_wallet", id_wallet);
                bundle.putSerializable("listCategory", listCategory);
                bundle.putString("currency", currency);
                bundle.putString("date", date);
                intent.putExtras(bundle);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("AnalysisExpense", "Error starting AnalysisDetailExpense: " + e.getMessage());
                Toast.makeText(getContext(), "Error opening expense detail: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
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

            ArrayList<GetAllTransactionsEntity_quyen> listExpense = new ArrayList<>();
            if (id_wallet.equals("Total")) {
                for (GetAllTransactionsEntity_quyen transaction : listTransactions) {
                    Date transactionDate = format.parse(transaction.transaction_date);
                    calendar.setTime(transactionDate);
                    int transactionYear = calendar.get(Calendar.YEAR);
                    int transactionMonth = calendar.get(Calendar.MONTH) + 1;
                    if (transaction.transaction_type.equals("EXPENSE") && transactionYear == year && (month == -1 || transactionMonth == month)) {
                        listExpense.add(transaction);
                    }
                }
            } else {
                for (GetAllTransactionsEntity_quyen transaction : listTransactions) {
                    Date transactionDate = format.parse(transaction.transaction_date);
                    calendar.setTime(transactionDate);
                    int transactionYear = calendar.get(Calendar.YEAR);
                    int transactionMonth = calendar.get(Calendar.MONTH) + 1;
                    if ((transaction.transaction_type.equals("EXPENSE") && transaction.wallet_id.equals(id_wallet) && transactionYear == year && (month == -1 || transactionMonth == month)) ||
                            (transaction.transaction_type.equals("TRANSFER") && transaction.wallet_id.equals(id_wallet) && transactionYear == year && (month == -1 || transactionMonth == month))) {
                        listExpense.add(transaction);
                    }
                }
            }
            Log.d("AnalysisExpense", "Filtered expenses: " + listExpense.size());

            Collections.sort(listExpense, new Comparator<GetAllTransactionsEntity_quyen>() {
                @Override
                public int compare(GetAllTransactionsEntity_quyen t1, GetAllTransactionsEntity_quyen t2) {
                    return Float.compare(Float.parseFloat(t2.amount), Float.parseFloat(t1.amount));
                }
            });

            ArrayList<PieEntry> categories = new ArrayList<>();
            if (listExpense.size() == 0) {
                categories.add(new PieEntry(100, "No expense"));
            } else if (listExpense.size() <= 4) {
                for (GetAllTransactionsEntity_quyen transaction : listExpense) {
                    if (transaction.category_id != null) {
                        categories.add(new PieEntry(Float.parseFloat(transaction.amount), getCategoryNameById(transaction.category_id)));
                    } else {
                        categories.add(new PieEntry(Float.parseFloat(transaction.amount), "Transfer"));
                    }
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    if (listExpense.get(i).category_id != null) {
                        categories.add(new PieEntry(Float.parseFloat(listExpense.get(i).amount), getCategoryNameById(listExpense.get(i).category_id)));
                    } else {
                        categories.add(new PieEntry(Float.parseFloat(listExpense.get(i).amount), "Transfer"));
                    }
                }
                float sum = 0;
                for (int i = 4; i < listExpense.size(); i++) {
                    sum += Float.parseFloat(listExpense.get(i).amount);
                }
                categories.add(new PieEntry(sum, "Others"));
            }

            PieDataSet pieDataSet = new PieDataSet(categories, "");
            pieDataSet.setColors(new int[]{Color.parseColor("#FFFA8AA0"), Color.parseColor("#FF5DC5E3"), Color.parseColor("#FF65CBB6"), Color.parseColor("#FFF9DE74"), Color.parseColor("#FF89D889")});
            pieDataSet.setValueTextColor(R.color.black);
            pieDataSet.setValueTextSize(16f);
            pieDataSet.setLabel("");

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("Expense (%)");
            pieChart.animate();
            pieChart.setUsePercentValues(true);
            pieChart.setDrawEntryLabels(false);
        } catch (Exception e) {
            Log.d("AnalysisExpense", "Error in SetUpPieChart: " + e.getMessage());
        }
    }

    String getCategoryNameById(String id) {
        for (GetAllCategoryEntity category : listCategory) {
            if (category.id.equals(id)) {
                return category.name;
            }
        }
        return "Unknown";
    }
}