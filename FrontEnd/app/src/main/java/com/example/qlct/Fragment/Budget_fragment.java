package com.example.qlct.Fragment;

import static com.example.qlct.R.drawable.background_bo_phai;
import static com.example.qlct.R.drawable.background_bo_trai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.qlct.API_Entity.GetAllBudget;
import com.example.qlct.API_Entity.GetAllCategoryy;
import com.example.qlct.Budget.AddBudget;
import com.example.qlct.Budget.BudgetFinishFragment;
import com.example.qlct.Budget.BudgetRunningFragment;
import com.example.qlct.Notification.Notification_activity;
import com.example.qlct.R;
import com.example.qlct.doitiente;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Budget_fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView create_bud_butt, running, finish;
    ImageView bell;
    LinearLayout running_budget;
    ArrayList<GetAllBudget> allBudgets = new ArrayList<>();
    ArrayList<GetAllCategoryy> listCate = new ArrayList<>();
    TextView remaining;
    TextView total_spent;
    TextView total_budget;
    SeekBar seekBar;

    public Budget_fragment() {
        // Required empty public constructor
    }

    public static Budget_fragment newInstance(String param1, String param2) {
        Budget_fragment fragment = new Budget_fragment();
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
        // Khởi tạo dữ liệu mock
        initializeMockData();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget_fragment, container, false);
        create_bud_butt = view.findViewById(R.id.create_bud_butt);
        running = view.findViewById(R.id.running);
        finish = view.findViewById(R.id.finish);
        bell = view.findViewById(R.id.bell);
        remaining = view.findViewById(R.id.remaining);
        total_spent = view.findViewById(R.id.total_spent);
        total_budget = view.findViewById(R.id.total_budget);
        running_budget = view.findViewById(R.id.running_budget);
        seekBar = view.findViewById(R.id.seekBar);

        // Comment phần gọi API
        /*
        allBudgets = new BudgetAPIUtil().getAllBudgets();
        listCate = new CategoryAPIUntill().getAllCategoryys();
        */

        if (allBudgets == null) {
            Log.d("Budget", "null");
        } else {
            Log.d("Budget", allBudgets.size() + "");
        }

        double totalBudget = 0;
        double totalSpent = 0;
        try {
            DecimalFormat df = new DecimalFormat("#,###");
            doitiente doitiente = new doitiente();
            for (int i = 0; i < allBudgets.size(); i++) {
                if (allBudgets.get(i).getCurrency_unit().equals("VND")) {
                    totalBudget += Double.parseDouble(allBudgets.get(i).getLimit_amount());
                    totalSpent += Double.parseDouble(allBudgets.get(i).getExpensed_amount());
                } else {
                    totalBudget += doitiente.converttoVND(allBudgets.get(i).getCurrency_unit(),
                            Double.parseDouble(allBudgets.get(i).getLimit_amount()));
                    totalSpent += doitiente.converttoVND(allBudgets.get(i).getCurrency_unit(),
                            Double.parseDouble(allBudgets.get(i).getExpensed_amount()));
                }
            }
            total_budget.setText(df.format(totalBudget) + " đ");
            total_spent.setText(df.format(totalSpent) + " đ");
            remaining.setText(df.format(totalBudget - totalSpent) + " đ");
            seekBar.setMax((int) totalBudget);
            seekBar.setProgress((int) totalSpent);
            seekBar.setEnabled(false);
        } catch (Exception e) {
            Log.d("Budget", e.getMessage());
        }

        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Notification_activity.class);
                startActivity(intent);
            }
        });

        create_bud_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), AddBudget.class));
            }
        });

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment child = new BudgetRunningFragment(allBudgets, listCate);
        transaction.replace(R.id.budget_container, child);
        transaction.commit();

        running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running.setBackgroundTintList(null);
                running.setBackgroundResource(background_bo_trai);
                finish.setBackgroundResource(background_bo_phai);
                finish.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ACACAC")));
                running.setTextColor(Color.parseColor("#1EABED"));
                finish.setTextColor(Color.BLACK);
                running_budget.setVisibility(View.VISIBLE);

                FragmentManager fragmentManager2 = getChildFragmentManager();
                FragmentTransaction transaction2 = fragmentManager2.beginTransaction();
                Fragment child1 = new BudgetRunningFragment(allBudgets, listCate);
                transaction2.replace(R.id.budget_container, child1);
                transaction2.commit();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running.setBackgroundResource(background_bo_trai);
                finish.setBackgroundTintList(null);
                finish.setBackgroundResource(background_bo_phai);
                running.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ACACAC")));
                finish.setTextColor(Color.parseColor("#1EABED"));
                running.setTextColor(Color.BLACK);
                running_budget.setVisibility(View.GONE);

                FragmentManager fragmentManager3 = getChildFragmentManager();
                FragmentTransaction transaction3 = fragmentManager3.beginTransaction();
                Fragment child2 = new BudgetFinishFragment(allBudgets, listCate);
                transaction3.replace(R.id.budget_container, child2);
                transaction3.commit();
            }
        });

        return view;
    }
    // Hàm khởi tạo dữ liệu mock
    // Hàm khởi tạo dữ liệu mock
    private void initializeMockData() {
        // Mock danh sách category (thêm trường userId)
        listCate.add(new GetAllCategoryy("1", "Food",  "https://cdn-icons-png.flaticon.com/512/9417/9417083.png","EXPENSE", "user1"));
        listCate.add(new GetAllCategoryy("2", "Transport",  "https://cdn-icons-png.freepik.com/512/3158/3158155.png","EXPENSE", "user1"));
        listCate.add(new GetAllCategoryy("3", "Shopping",  "https://cdn.iconscout.com/icon/free/png-256/free-shopping-icon-download-in-svg-png-gif-file-formats--mall-center-buying-clothes-grocery-purchasing-activities-flat-icons-pack-user-interface-1185355.png", "EXPENSE","user1"));
        listCate.add(new GetAllCategoryy("4", "Entertainment",  "https://cdn-icons-png.flaticon.com/512/6008/6008427.png", "EXPENSE","user1"));
        listCate.add(new GetAllCategoryy("5", "Bills",  "https://cdn-icons-png.flaticon.com/512/1649/1649577.png","EXPENSE", "user1"));
        listCate.add(new GetAllCategoryy("6", "Travel",  "https://png.pngtree.com/png-clipart/20190628/original/pngtree-vacation-and-travel-icon-png-image_4032146.jpg", "EXPENSE","user1"));

        // Mock danh sách budget
        allBudgets.add(new GetAllBudget(
                "b1",
                "1",
                "1500000",
                "800000",
                "VND",
                "NO_RENEW",
                "DAY",
                "25-05-2025",
                null,
                null,
                true,
                true,
                "2025-05-01",
                "user1",
                listCate.get(0)
        ));

        allBudgets.add(new GetAllBudget(
                "b2",
                "2",
                "1000000",
                "400000",
                "VND",
                "RENEW",
                null,
                null,
                "Monthly",
                null,
                true,
                true,
                "2025-05-01",
                "user1",
                listCate.get(1)
        ));

        allBudgets.add(new GetAllBudget(
                "b3",
                "3",
                "2000000",
                "1200000",
                "VND",
                "NO_RENEW",
                "TIME_SPAN",
                "01-05-2025 31-05-2025",
                null,
                null,
                true,
                true,
                "2025-05-01",
                "user1",
                listCate.get(2)
        ));

        allBudgets.add(new GetAllBudget(
                "b4",
                "4",
                "500000",
                "200000",
                "VND",
                "RENEW",
                null,
                null,
                "Weekly",
                null,
                true,
                true,
                "2025-05-10",
                "user1",
                listCate.get(3)
        ));

        allBudgets.add(new GetAllBudget(
                "b5",
                "5",
                "1200000",
                "900000",
                "VND",
                "NO_RENEW",
                "DAY",
                "20-05-2025",
                null,
                null,
                true,
                true,
                "2025-05-01",
                "user1",
                listCate.get(4)
        ));

        allBudgets.add(new GetAllBudget(
                "b6",
                "6",
                "80",
                "50",
                "USD",
                "NO_RENEW",
                "TIME_SPAN",
                "15-05-2025 30-06-2025",
                null,
                null,
                true,
                true,
                "2025-05-01",
                "user1",
                listCate.get(5)
        ));

        allBudgets.add(new GetAllBudget(
                "b7",
                "1",
                "800000",
                "300000",
                "VND",
                "RENEW",
                null,
                null,
                "Monthly",
                null,
                true,
                true,
                "2025-04-01",
                "user1",
                listCate.get(0)
        ));

        allBudgets.add(new GetAllBudget(
                "b8",
                "2",
                "600000",
                "500000",
                "VND",
                "NO_RENEW",
                "DAY",
                "18-05-2025",
                null,
                null,
                true,
                true,
                "2025-05-01",
                "user1",
                listCate.get(1)
        ));

        allBudgets.add(new GetAllBudget(
                "b9",
                "3",
                "900000",
                "600000",
                "VND",
                "NO_RENEW",
                "TIME_SPAN",
                "01-04-2025 30-04-2025",
                null,
                null,
                true,
                true,
                "2025-04-01",
                "user1",
                listCate.get(2)
        ));

        allBudgets.add(new GetAllBudget(
                "b10",
                "4",
                "20",
                "10",
                "USD",
                "RENEW",
                null,
                null,
                "Weekly",
                null,
                true,
                true,
                "2025-05-15",
                "user1",
                listCate.get(3)
        ));
    }
}