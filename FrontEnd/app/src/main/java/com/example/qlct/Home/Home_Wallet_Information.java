package com.example.qlct.Home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.qlct.R;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Home_Wallet_Information extends AppCompatActivity {

    int chon1 = 0;
    int chon2 = 0;
    int chon1in = 0;
    int chon2in = 0;
    String currency;
    String exname;
    String exid;
    String exammount;
    String excurrency;
    String create;
    String exupdate;
    String exduochon;
    String spec;

    // Mock data class to replace GetAllWalletsEntity
    private static class MockWallet {
        String id;
        String name;
        double amount;
        String currency_unit;
        String create_at;
        String update_at;

        MockWallet(String id, String name, double amount, String currency_unit, String create_at, String update_at) {
            this.id = id;
            this.name = name;
            this.amount = amount;
            this.currency_unit = currency_unit;
            this.create_at = create_at;
            this.update_at = update_at;
        }
    }

    // Mock data to replace API calls
    private List<MockWallet> getMockWallets() {
        return new ArrayList<>(Arrays.asList(
                new MockWallet("1", "Personal", 1000.0, "USD", "2025-01-01", "2025-05-01"),
                new MockWallet("2", "Savings", 5000.0, "VND", "2025-02-01", "2025-05-10"),
                new MockWallet("3", "Business", 2000.0, "EUR", "2025-03-01", "2025-05-15"),
                new MockWallet("4", "Travel", 1500.0, "CNY", "2025-04-01", "2025-05-18")
        ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        exid = intent.getStringExtra("id");
        exname = intent.getStringExtra("name");
        exammount = intent.getStringExtra("ammount");
        excurrency = intent.getStringExtra("currency");
        create = intent.getStringExtra("start");
        exupdate = intent.getStringExtra("update");
        exduochon = intent.getStringExtra("duocchon");
        spec = intent.getStringExtra("spec");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_wallet_information);

        // Handle spec case with mock data
        if (spec != null && spec.equals("spec")) {
            List<MockWallet> mockWallets = getMockWallets();
            for (MockWallet item : mockWallets) {
                if (item.name.equals(exname)) {
                    excurrency = item.currency_unit;
                    exammount = String.valueOf(item.amount);
                    create = item.create_at;
                    exupdate = item.update_at;
                    exduochon = item.name;
                    exid = item.id;
                    break;
                }
            }
        }

        // Save button
        TextView save = findViewById(R.id.save);
        save.setOnClickListener(v -> {
            TextInputEditText nameview = findViewById(R.id.namewallet);
            TextInputEditText ammountview = findViewById(R.id.ammountinf);
            try {
                // Simulate wallet update
                String newName = nameview.getText().toString();
                double newAmount = Double.parseDouble(ammountview.getText().toString());

                Intent intent1 = new Intent(Home_Wallet_Information.this, Home_My_wallets.class);
                intent1.putExtra("viduocchon", exduochon);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error updating wallet", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button
        ConstraintLayout delete = findViewById(R.id.delete);
        delete.setOnClickListener(v -> new AlertDialog.Builder(Home_Wallet_Information.this)
                .setTitle("Delete Wallet")
                .setMessage("Are you sure you want to delete this wallet?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Simulate wallet deletion
                    try {
                        // Mock deletion logic
                        Intent intent1 = new Intent(Home_Wallet_Information.this, Home_My_wallets.class);
                        TextInputEditText nameview = findViewById(R.id.namewallet);
                        if (exduochon.equals(nameview.getText().toString())) {
                            exduochon = "Total";
                        }
                        intent1.putExtra("viduocchon", exduochon);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        finish();
                    } catch (Exception e) {
                        new AlertDialog.Builder(Home_Wallet_Information.this)
                                .setTitle("Error")
                                .setMessage("Failed to delete wallet")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());

        // Set up views
        TextInputEditText nameview = findViewById(R.id.namewallet);
        nameview.setText(exname);
        TextInputEditText ammountview = findViewById(R.id.ammountinf);
        ammountview.setText(exammount);
        TextView txt1 = findViewById(R.id.txt);
        txt1.setText(excurrency);
        TextView startview = findViewById(R.id.start);
        startview.setText("Start day: " + create);
        TextView updateview = findViewById(R.id.update);
        updateview.setText("Last update: " + exupdate);

        // Cancel button
        TextView cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> finish());

        // Currency selection
        LinearLayout linearLayout = findViewById(R.id.currency);
        linearLayout.setOnClickListener(v -> showDialog());
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_currency);
        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationn;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        LinearLayout bo1 = dialog.findViewById(R.id.bo1);
        LinearLayout bo2 = dialog.findViewById(R.id.bo2);
        LinearLayout bo3 = dialog.findViewById(R.id.bo3);
        LinearLayout bo4 = dialog.findViewById(R.id.bo4);
        TextView txt1 = findViewById(R.id.txt);

        // Set initial selection
        if (txt1.getText().toString().equals("USD")) {
            bo1.setBackgroundResource(R.drawable.nenluachon);
        } else if (txt1.getText().toString().equals("VND")) {
            bo2.setBackgroundResource(R.drawable.nenluachon);
        } else if (txt1.getText().toString().equals("EUR")) {
            bo3.setBackgroundResource(R.drawable.nenluachon);
        } else if (txt1.getText().toString().equals("CNY")) {
            bo4.setBackgroundResource(R.drawable.nenluachon);
        }

        bo1.setOnClickListener(v -> {
            currency = "USD";
            bo1.setBackgroundResource(R.drawable.nenluachon);
            bo2.setBackgroundResource(0);
            bo3.setBackgroundResource(0);
            bo4.setBackgroundResource(0);
        });

        bo2.setOnClickListener(v -> {
            currency = "VND";
            bo2.setBackgroundResource(R.drawable.nenluachon);
            bo1.setBackgroundResource(0);
            bo3.setBackgroundResource(0);
            bo4.setBackgroundResource(0);
        });

        bo3.setOnClickListener(v -> {
            currency = "EUR";
            bo3.setBackgroundResource(R.drawable.nenluachon);
            bo2.setBackgroundResource(0);
            bo1.setBackgroundResource(0);
            bo4.setBackgroundResource(0);
        });

        bo4.setOnClickListener(v -> {
            currency = "CNY";
            bo4.setBackgroundResource(R.drawable.nenluachon);
            bo2.setBackgroundResource(0);
            bo3.setBackgroundResource(0);
            bo1.setBackgroundResource(0);
        });

        TextView ok = dialog.findViewById(R.id.apply);
        ok.setOnClickListener(v -> {
            TextView txt = findViewById(R.id.txt);
            txt.setText(currency);
            dialog.dismiss();
        });
    }

    private void showDialog2() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_member_can);
        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationn;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        LinearLayout bo1 = dialog.findViewById(R.id.bo1);
        LinearLayout bo2 = dialog.findViewById(R.id.bo2);
        TextView apply = dialog.findViewById(R.id.apply);
        ImageView check1 = dialog.findViewById(R.id.check1);
        ImageView check2 = dialog.findViewById(R.id.check2);
        check1.setVisibility(View.GONE);
        check2.setVisibility(View.GONE);

        bo1.setOnClickListener(v -> {
            if (chon1in == 0) {
                chon1in = 1;
                check1.setVisibility(View.VISIBLE);
                bo1.setBackgroundResource(R.drawable.nenluachon);
            } else {
                chon1in = 0;
                check1.setVisibility(View.GONE);
                bo1.setBackgroundResource(0);
            }
        });

        bo2.setOnClickListener(v -> {
            if (chon2in == 0) {
                check2.setVisibility(View.VISIBLE);
                chon2in = 1;
                bo2.setBackgroundResource(R.drawable.nenluachon);
            } else {
                chon2in = 0;
                check2.setVisibility(View.GONE);
                bo2.setBackgroundResource(0);
            }
        });

        apply.setOnClickListener(v -> dialog.dismiss());
    }
}