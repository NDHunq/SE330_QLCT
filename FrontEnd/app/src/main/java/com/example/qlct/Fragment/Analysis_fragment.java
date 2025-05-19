package com.example.qlct.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.qlct.Analysis.AnalysisExpenseFragment;
import com.example.qlct.Analysis.AnalysisIcomeFragment;
import com.example.qlct.Analysis.AnalysisNetIncomeFragment;
import com.example.qlct.API_Entity.GetAllCategoryEntity;
import com.example.qlct.API_Entity.GetAllTransactionsEntity_quyen;
import com.example.qlct.API_Entity.GetAllWalletsEntity;
import com.example.qlct.Notification.Notificaiton_activity;
import com.example.qlct.R;
import com.example.qlct.SelectWallet_Adapter;
import com.example.qlct.Wallet_hdp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Analysis_fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    RadioButton month, year;
    ImageView back, next;
    TextView date;
    ImageView bell;
    LinearLayout select_wallet;
    final Calendar calendar = Calendar.getInstance();
    final SimpleDateFormat monthFormat = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
    final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    private ListView walletListView;
    private ArrayList<Wallet_hdp> walletList;
    TextView wallet_name;
    ArrayList<GetAllTransactionsEntity_quyen> listTransactions;
    ArrayList<GetAllWalletsEntity> listwallet;
    ArrayList<GetAllCategoryEntity> listCategory;
    String id_wallet;

    public Analysis_fragment() {
    }

    public static Analysis_fragment newInstance(String param1, String param2) {
        Analysis_fragment fragment = new Analysis_fragment();
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
        View view = inflater.inflate(R.layout.fragment_analysis_fragment, container, false);
        initializeMockData();
        AnhXa(view);
        Load(listTransactions, "Total", listCategory, date.getText().toString());
        return view;
    }

    private void initializeMockData() {
        listTransactions = new ArrayList<>();
        listTransactions.add(new GetAllTransactionsEntity_quyen("1", "user1", "500000", "cat1", "wallet1", "Lunch", "android.resource://com.example.qlct/drawable/ic_food", "2025-05-01 10:00:00", "EXPENSE", "VND", null, null, null));
        listTransactions.add(new GetAllTransactionsEntity_quyen("2", "user1", "2000000", "cat5", "wallet2", "Salary", "android.resource://com.example.qlct/drawable/ic_salary", "2025-05-02 12:00:00", "INCOME", "VND", null, null, null));
        listTransactions.add(new GetAllTransactionsEntity_quyen("3", "user1", "300000", "cat2", "wallet1", "Bus fare", "android.resource://com.example.qlct/drawable/ic_transport", "2025-05-03 08:00:00", "EXPENSE", "VND", null, null, null));
        listTransactions.add(new GetAllTransactionsEntity_quyen("4", "user1", "1000000", null, "wallet1", "Transfer to Bank", "android.resource://com.example.qlct/drawable/ic_transfer", "2025-05-04 15:00:00", "TRANSFER", "VND", "wallet2", null, null));
        listTransactions.add(new GetAllTransactionsEntity_quyen("5", "user1", "150", "cat3", "wallet2", "Clothes", "android.resource://com.example.qlct/drawable/ic_shopping", "2025-05-05 14:00:00", "EXPENSE", "USD", null, null, null));
        listTransactions.add(new GetAllTransactionsEntity_quyen("6", "user1", "5000000", "cat6", "wallet2", "Bonus", "android.resource://com.example.qlct/drawable/ic_bonus", "2025-05-06 09:00:00", "INCOME", "VND", null, null, null));
        listTransactions.add(new GetAllTransactionsEntity_quyen("7", "user1", "400000", "cat4", "wallet1", "Movie", "android.resource://com.example.qlct/drawable/ic_entertainment", "2025-05-07 19:00:00", "EXPENSE", "VND", null, null, null));
        listTransactions.add(new GetAllTransactionsEntity_quyen("8", "user1", "200", "cat3", "wallet2", "Gift", "android.resource://com.example.qlct/drawable/ic_gift", "2025-05-08 11:00:00", "INCOME", "USD", null, null, null));
        listTransactions.add(new GetAllTransactionsEntity_quyen("9", "user1", "600000", "cat1", "wallet1", "Dinner", "android.resource://com.example.qlct/drawable/ic_food", "2025-05-09 18:00:00", "EXPENSE", "VND", null, null, null));
        listTransactions.add(new GetAllTransactionsEntity_quyen("10", "user1", "1000000", "cat6", "wallet2", "Freelance", "android.resource://com.example.qlct/drawable/ic_freelance", "2025-05-10 16:00:00", "INCOME", "VND", null, null, null));
        Log.d("AnalysisFragment", "Mock transactions initialized: " + listTransactions.size());

        listwallet = new ArrayList<>();
        listwallet.add(new GetAllWalletsEntity() {{ id = "wallet1"; name = "Cash"; amount = "1000000"; currency_unit = "VND"; create_at = "2025-01-01"; update_at = "2025-05-01"; }});
        listwallet.add(new GetAllWalletsEntity() {{ id = "wallet2"; name = "Bank"; amount = "5000000"; currency_unit = "USD"; create_at = "2025-01-01"; update_at = "2025-05-01"; }});
        listwallet.add(new GetAllWalletsEntity() {{ id = "Total"; name = "Total"; amount = "6000000"; currency_unit = "VND"; create_at = "2025-01-01"; update_at = "2025-05-01"; }});
        Log.d("AnalysisFragment", "Mock wallets initialized: " + listwallet.size());

        listCategory = new ArrayList<>();
        listCategory.add(new GetAllCategoryEntity("cat1", "Food", "https://cdn-icons-png.flaticon.com/512/9417/9417083.png", "EXPENSE", "user1"));
        listCategory.add(new GetAllCategoryEntity("cat2", "Transport", "https://cdn-icons-png.freepik.com/512/3158/3158155.png", "EXPENSE", "user1"));
        listCategory.add(new GetAllCategoryEntity("cat3", "Shopping", "https://cdn.iconscout.com/icon/free/png-256/free-shopping-icon-download-in-svg-png-gif-file-formats--mall-center-buying-clothes-grocery-purchasing-activities-flat-icons-pack-user-interface-1185355.png", "EXPENSE", "user1"));
        listCategory.add(new GetAllCategoryEntity("cat4", "Entertainment", "https://cdn-icons-png.flaticon.com/512/6008/6008427.png", "EXPENSE", "user1"));
        listCategory.add(new GetAllCategoryEntity("cat5", "Bills", "https://cdn-icons-png.flaticon.com/512/1649/1649577.png", "EXPENSE", "user1"));
        listCategory.add(new GetAllCategoryEntity("cat6", "Travel", "https://png.pngtree.com/png-clipart/20190628/original/pngtree-vacation-and-travel-icon-png-image_4032146.jpg", "EXPENSE", "user1"));

        Log.d("AnalysisFragment", "Mock categories initialized: " + listCategory.size());
    }

    public void Load(ArrayList<GetAllTransactionsEntity_quyen> listtrans, String id_wallet, ArrayList<GetAllCategoryEntity> listCategory, String Date) {
        ArrayList<GetAllTransactionsEntity_quyen> listTransCopy = new ArrayList<>();
        ArrayList<GetAllTransactionsEntity_quyen> listTransCopy1 = new ArrayList<>();
        ArrayList<GetAllTransactionsEntity_quyen> listTransCopy2 = new ArrayList<>();
        for (GetAllTransactionsEntity_quyen transaction : listtrans) {
            GetAllTransactionsEntity_quyen transactionCopy = new GetAllTransactionsEntity_quyen(
                    transaction.id, transaction.user_id, transaction.amount, transaction.category_id,
                    transaction.wallet_id, transaction.notes, transaction.picture, transaction.transaction_date,
                    transaction.transaction_type, transaction.currency_unit, transaction.target_wallet_id,
                    transaction.wallet, transaction.category);
            listTransCopy.add(transactionCopy);
            listTransCopy1.add(transactionCopy);
            listTransCopy2.add(transactionCopy);
        }
        Log.d("AnalysisFragment", "ListTransCopy size: " + listTransCopy.size());
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment child = new AnalysisNetIncomeFragment(listTransCopy, id_wallet, listCategory, getCurrencyUnitById(id_wallet), Date);
        transaction.replace(R.id.ChildFrag1, child);
        transaction.commit();

        FragmentManager fragmentManager1 = getChildFragmentManager();
        FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
        Fragment child1 = new AnalysisExpenseFragment(listTransCopy1, id_wallet, listCategory, getCurrencyUnitById(id_wallet), Date);
        transaction1.replace(R.id.ChildFrag2, child1);
        transaction1.commit();

        FragmentManager fragmentManager2 = getChildFragmentManager();
        FragmentTransaction transaction2 = fragmentManager2.beginTransaction();
        Fragment child2 = new AnalysisIcomeFragment(listTransCopy2, id_wallet, listCategory, getCurrencyUnitById(id_wallet), Date);
        transaction2.replace(R.id.ChildFrag3, child2);
        transaction2.commit();
    }

    public void AnhXa(View view) {
        month = view.findViewById(R.id.month);
        month.setChecked(true);
        year = view.findViewById(R.id.year);
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        next.setVisibility(View.INVISIBLE);
        date = view.findViewById(R.id.date);
        bell = view.findViewById(R.id.bell);
        select_wallet = view.findViewById(R.id.select_wallet);
        wallet_name = view.findViewById(R.id.wallet_name);

        // Đặt tháng hiện tại
        Calendar currentCalendar = Calendar.getInstance();
        date.setText(monthFormat.format(currentCalendar.getTime()));
        Log.d("AnalysisFragment", "Default date set to: " + date.getText().toString());

        bell.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Notificaiton_activity.class);
            startActivity(intent);
        });

        month.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                date.setText(monthFormat.format(currentCalendar.getTime()));
                next.setVisibility(View.INVISIBLE);
                calendar.setTime(currentCalendar.getTime());
                Log.d("AnalysisFragment", "Month selected, date: " + date.getText().toString());
            }
        });

        year.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                date.setText(yearFormat.format(currentCalendar.getTime()));
                next.setVisibility(View.INVISIBLE);
                calendar.setTime(currentCalendar.getTime());
                Log.d("AnalysisFragment", "Year selected, date: " + date.getText().toString());
            }
        });

        next.setOnClickListener(v -> {
            if (month.isChecked()) {
                calendar.add(Calendar.MONTH, 1);
                date.setText(monthFormat.format(calendar.getTime()));
                if ((calendar.getTime().getMonth() == currentCalendar.getTime().getMonth()) &&
                        (calendar.getTime().getYear() == currentCalendar.getTime().getYear())) {
                    next.setVisibility(View.INVISIBLE);
                }
            } else if (year.isChecked()) {
                calendar.add(Calendar.YEAR, 1);
                date.setText(yearFormat.format(calendar.getTime()));
                if (calendar.getTime().getYear() == currentCalendar.getTime().getYear()) {
                    next.setVisibility(View.INVISIBLE);
                }
            }
        });

        back.setOnClickListener(v -> {
            if (month.isChecked()) {
                calendar.add(Calendar.MONTH, -1);
                date.setText(monthFormat.format(calendar.getTime()));
            } else if (year.isChecked()) {
                calendar.add(Calendar.YEAR, -1);
                date.setText(yearFormat.format(calendar.getTime()));
            }
            next.setVisibility(View.VISIBLE);
        });

        select_wallet.setOnClickListener(v -> showWalletDialog());

        wallet_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<GetAllTransactionsEntity_quyen> lisrI = new ArrayList<>();
                for (GetAllTransactionsEntity_quyen transaction : listTransactions) {
                    GetAllTransactionsEntity_quyen transactionCopy = new GetAllTransactionsEntity_quyen(
                            transaction.id, transaction.user_id, transaction.amount, transaction.category_id,
                            transaction.wallet_id, transaction.notes, transaction.picture, transaction.transaction_date,
                            transaction.transaction_type, transaction.currency_unit, transaction.target_wallet_id,
                            transaction.wallet, transaction.category);
                    lisrI.add(transactionCopy);
                }
                if (wallet_name.getText().toString().equals("Total")) {
                    Load(lisrI, "Total", listCategory, date.getText().toString());
                } else {
                    id_wallet = getWalletIdByName(wallet_name.getText().toString());
                    Load(lisrI, id_wallet, listCategory, date.getText().toString());
                }
            }
        });

        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<GetAllTransactionsEntity_quyen> lisrI = new ArrayList<>();
                for (GetAllTransactionsEntity_quyen transaction : listTransactions) {
                    GetAllTransactionsEntity_quyen transactionCopy = new GetAllTransactionsEntity_quyen(
                            transaction.id, transaction.user_id, transaction.amount, transaction.category_id,
                            transaction.wallet_id, transaction.notes, transaction.picture, transaction.transaction_date,
                            transaction.transaction_type, transaction.currency_unit, transaction.target_wallet_id,
                            transaction.wallet, transaction.category);
                    lisrI.add(transactionCopy);
                }
                if (wallet_name.getText().toString().equals("Total")) {
                    Load(lisrI, "Total", listCategory, date.getText().toString());
                } else {
                    id_wallet = getWalletIdByName(wallet_name.getText().toString());
                    Load(lisrI, id_wallet, listCategory, date.getText().toString());
                }
            }
        });

        date.setOnClickListener(v -> ShowDiaLog());
    }

    void ShowDiaLog() {
        if (date.getText().length() == 4) {
            Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_year_picker);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            GridView gridView = dialog.findViewById(R.id.gridView);
            TextView year_lbl = dialog.findViewById(R.id.year);
            TextView cancel = dialog.findViewById(R.id.cancel);
            TextView ok = dialog.findViewById(R.id.ok);
            ImageView up = dialog.findViewById(R.id.up);
            ImageView down = dialog.findViewById(R.id.down);
            String[] years = {"2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025"};
            ArrayAdapter yearAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, years);
            gridView.setAdapter(yearAdapter);
            gridView.setOnItemClickListener((parent, view, position, id) -> year_lbl.setText(years[position]));
            up.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                if (Integer.parseInt(year_lbl.getText().toString()) < currentYear)
                    year_lbl.setText(Integer.parseInt(year_lbl.getText().toString()) + 1 + "");
                else
                    Toast.makeText(getContext(), "Năm không được lớn hơn " + currentYear, Toast.LENGTH_LONG).show();
            });
            down.setOnClickListener(v -> year_lbl.setText(Integer.parseInt(year_lbl.getText().toString()) - 1 + ""));
            cancel.setOnClickListener(v -> dialog.dismiss());
            ok.setOnClickListener(v -> {
                if (isYearLessThanCurrent(year_lbl.getText().toString())) {
                    Toast.makeText(getContext(), "Năm không được lớn hơn hiện tại", Toast.LENGTH_LONG).show();
                } else {
                    date.setText(year_lbl.getText().toString());
                }
                dialog.dismiss();
            });
        } else {
            Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_month_picker);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            GridView gridView = dialog.findViewById(R.id.gridView);
            TextView month_lbl = dialog.findViewById(R.id.month);
            TextView year_lbl = dialog.findViewById(R.id.year);
            TextView cancel = dialog.findViewById(R.id.cancel);
            TextView ok = dialog.findViewById(R.id.ok);
            ImageView up = dialog.findViewById(R.id.up);
            ImageView down = dialog.findViewById(R.id.down);
            String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            ArrayAdapter monthAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, month);
            gridView.setAdapter(monthAdapter);
            gridView.setOnItemClickListener((parent, view, position, id) -> month_lbl.setText(month[position]));
            up.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                if (Integer.parseInt(year_lbl.getText().toString()) < currentYear)
                    year_lbl.setText(Integer.parseInt(year_lbl.getText().toString()) + 1 + "");
                else
                    Toast.makeText(getContext(), "Năm không được lớn hơn " + currentYear, Toast.LENGTH_LONG).show();
            });
            down.setOnClickListener(v -> year_lbl.setText(Integer.parseInt(year_lbl.getText().toString()) - 1 + ""));
            cancel.setOnClickListener(v -> dialog.dismiss());
            ok.setOnClickListener(v -> {
                String inputDate = month_lbl.getText().toString() + " " + year_lbl.getText().toString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);
                try {
                    Date daTe = inputFormat.parse(inputDate);
                    String outputDate = outputFormat.format(daTe);
                    if (isDateLessThanCurrent(outputDate)) {
                        Toast.makeText(getContext(), "Tháng không được lớn hơn hiện tại", Toast.LENGTH_LONG).show();
                    } else {
                        date.setText(outputDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            });
        }
    }

    public boolean isDateLessThanCurrent(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
        try {
            Date inputDate = format.parse(dateString);
            Calendar currentCalendar = Calendar.getInstance();
            int currentYear = currentCalendar.get(Calendar.YEAR);
            int currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
            Calendar inputCalendar = Calendar.getInstance();
            inputCalendar.setTime(inputDate);
            int inputYear = inputCalendar.get(Calendar.YEAR);
            int inputMonth = inputCalendar.get(Calendar.MONTH) + 1;
            if (inputYear < currentYear || (inputYear == currentYear && inputMonth <= currentMonth)) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isYearLessThanCurrent(String yearString) {
        int inputYear = Integer.parseInt(yearString);
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        return inputYear > currentYear;
    }

    String getWalletIdByName(String walletName) {
        for (GetAllWalletsEntity wallet : listwallet) {
            if (wallet.name.equals(walletName)) {
                return wallet.id;
            }
        }
        Log.w("AnalysisFragment", "Wallet not found: " + walletName);
        return null;
    }

    private void showWalletDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_select_wallet);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationn;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bgh);
        dialog.show();

        walletListView = dialog.findViewById(R.id.select_wallet_listview);
        AnhXaWallet();
        Log.d("AnalysisFragment", "walletList size: " + (walletList != null ? walletList.size() : "null"));
        if (walletList == null || walletList.isEmpty()) {
            Toast.makeText(getContext(), "No wallets available", Toast.LENGTH_SHORT).show();
        }
        SelectWallet_Adapter adapter = new SelectWallet_Adapter(getActivity(), R.layout.select_wallet_item_list, walletList);
        walletListView.setAdapter(adapter);
        walletListView.setOnItemClickListener((parent, view, position, id) -> {
            wallet_name.setText(walletList.get(position).getWalletName());
            dialog.dismiss();
        });
    }

    private void AnhXaWallet() {
        try {
            walletList = new ArrayList<>();
            for (GetAllWalletsEntity item : listwallet) {
                walletList.add(new Wallet_hdp(item.id, item.name, item.amount, R.drawable.wallet, item.currency_unit));
            }
            walletList.add(new Wallet_hdp("total", "Total", "", R.drawable.wallet, ""));
            Log.d("AnalysisFragment", "Get_wallet_data_object: " + walletList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getCurrencyUnitById(String id_wallet) {
        if (id_wallet.equals("Total")) {
            return "đ";
        }
        for (GetAllWalletsEntity wallet : listwallet) {
            if (wallet.id.equals(id_wallet)) {
                String currency_unit = wallet.currency_unit;
                if (currency_unit.equals("VND")) {
                    return "đ";
                } else if (currency_unit.equals("USD")) {
                    return "$";
                } else if (currency_unit.equals("EUR")) {
                    return "€";
                } else if (currency_unit.equals("CNY")) {
                    return "¥";
                }
            }
        }
        return "đ";
    }
}