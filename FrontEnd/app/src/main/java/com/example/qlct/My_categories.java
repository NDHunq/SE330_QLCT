package com.example.qlct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class My_categories extends AppCompatActivity {
    boolean isIncome = true;
    ImageButton backmycate;
    ListView lvcate;
    ArrayList<cate_item> arraycate;
    List<MockCategory> List;
    categories_Adapter adapter;
    TextView addnew;
    LinearLayout income;
    LinearLayout expense;
    TextView income1;
    TextView expense1;
    ImageButton incomearrow;
    ImageButton expensearrow;

    // Mock data class to replace GetAllCategoryEntity
    private static class MockCategory {
        String picture;
        String name;
        String type;

        MockCategory(String picture, String name, String type) {
            this.picture = picture;
            this.name = name;
            this.type = type;
        }
    }

    // Mock data to replace API calls
    private List<MockCategory> getMockCategories() {
        return new ArrayList<>(Arrays.asList(
                new MockCategory("ic_salary", "Salary", "INCOME"),
                new MockCategory("ic_bonus", "Bonus", "INCOME"),
                new MockCategory("ic_investment", "Investment", "INCOME"),
                new MockCategory("ic_food", "Food", "EXPENSE"),
                new MockCategory("ic_transport", "Transport", "EXPENSE"),
                new MockCategory("ic_shopping", "Shopping", "EXPENSE"),
                new MockCategory("ic_entertainment", "Entertainment", "EXPENSE")
        ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_categories);
        List = getMockCategories();

        backmycate = findViewById(R.id.backmycate);
        backmycate.setOnClickListener(v -> finish());

        income1 = findViewById(R.id.income1);
        expense1 = findViewById(R.id.expense1);
        incomearrow = findViewById(R.id.incomearrow);
        expensearrow = findViewById(R.id.expensearrow);
        income = findViewById(R.id.income);
        expense = findViewById(R.id.expense);
        addnew = findViewById(R.id.addnew);
        lvcate = findViewById(R.id.mylv);
        Anhxa();

        addnew.setOnClickListener(v -> {
            Intent myintent = new Intent(My_categories.this, Category_Add.class);
            startActivity(myintent);
        });

        income.setOnClickListener(v -> {
            isIncome = true;
            Anhxa();
            income.setBackgroundColor(getResources().getColor(R.color.xanhdam));
            income1.setTextColor(getResources().getColor(R.color.white));
            incomearrow.setBackground(getResources().getDrawable(R.drawable.downarrow_white));
            expense.setBackground(getResources().getDrawable(R.drawable.expense));
            expense1.setTextColor(getResources().getColor(R.color.black));
            expensearrow.setBackground(getResources().getDrawable(R.drawable.up_arrow_1));
        });

        expense.setOnClickListener(v -> {
            isIncome = false;
            Anhxa();
            income.setBackground(getResources().getDrawable(R.drawable.expense));
            income1.setTextColor(getResources().getColor(R.color.black));
            incomearrow.setBackground(getResources().getDrawable(R.drawable.arrow_down));
            expense.setBackgroundColor(getResources().getColor(R.color.red));
            expense1.setTextColor(getResources().getColor(R.color.white));
            expensearrow.setBackground(getResources().getDrawable(R.drawable.uparrow_white));
        });
    }

    private void Anhxa() {
        arraycate = new ArrayList<>();
        for (MockCategory category : List) {
            if (isIncome) {
                if (category.type.equals("INCOME")) {
                    arraycate.add(new cate_item(category.picture, category.name, R.drawable.delete));
                }
            } else {
                if (category.type.equals("EXPENSE")) {
                    arraycate.add(new cate_item(category.picture, category.name, R.drawable.delete));
                }
            }
        }
        adapter = new categories_Adapter(this, R.layout.categories_item, arraycate);
        lvcate.setAdapter(adapter);
    }
}