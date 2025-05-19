package com.example.qlct.Transaction;

import android.app.DatePickerDialog;
import android.app.Dialog;
// import android.app.Instrumentation; // Không sử dụng
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
// import android.graphics.Bitmap; // Không sử dụng trực tiếp
import android.graphics.Rect;
// import android.graphics.drawable.BitmapDrawable; // Không sử dụng trực tiếp
import android.graphics.drawable.ColorDrawable;
// import android.graphics.drawable.Drawable; // Không sử dụng trực tiếp
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
// import android.util.Base64; // Không sử dụng
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
// import com.bumptech.glide.request.RequestOptions; // Đã có trong Category_adapter
import com.example.qlct.API_Config;
import com.example.qlct.API_Entity.CreateTransactionEntity;
import com.example.qlct.API_Entity.GetAllCategoryEntity;
import com.example.qlct.API_Entity.GetAllWalletsEntity;
import com.example.qlct.API_Entity.UpdateWalletEntity;
import com.example.qlct.API_Utils.CategoryAPIUtil;
import com.example.qlct.API_Utils.TransactionAPIUtil;
import com.example.qlct.API_Utils.WalletAPIUtil;
import com.example.qlct.Category.Category_adapter;
import com.example.qlct.Category.Category_hdp;
import com.example.qlct.Category_Add;
import com.example.qlct.Home.Home_New_wallet;
import com.example.qlct.R;
import com.example.qlct.SelectWallet_Adapter;
import com.example.qlct.Wallet_hdp;
import com.example.qlct.doitiente;
import com.google.android.material.button.MaterialButton;
// import com.google.android.material.datepicker.MaterialStyledDatePickerDialog; // Không sử dụng
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

// import java.io.ByteArrayOutputStream; // Không sử dụng
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddTransaction extends AppCompatActivity {

    private String url = null;
    // private Button uploadBtn; // Không thấy sử dụng
    private ImageView transactionImage;
    ActivityResultLauncher<Intent> resultLauncher;
    // private static final int PICK_IMAGE_REQUEST = 1; // Không thấy sử dụng
    private boolean isUserInteracting = true;

    // --- BIẾN CHO CATEGORY ---
    private ListView categoryListViewDialog; // ListView trong dialog chọn category
    private List<Category_hdp> categoryListGlobal = new ArrayList<>(); // Danh sách TẤT CẢ category
    private List<Category_hdp> renderCategoryListDialog; // Danh sách category ĐÃ LỌC để hiển thị trong dialog
    private String incomeCategoryStorageId = "";   // ID của category thu nhập đã chọn
    private String expenseCategoryStorageId = "";  // ID của category chi tiêu đã chọn
    private TextView incomeCategoryTextViewMain;
    private ImageView incomeCategoryIconMain;
    private TextView expenseCategoryTextViewMain;
    private ImageView expenseCategoryIconMain;
    // --- KẾT THÚC BIẾN CATEGORY ---

    private String currency;
    private ListView walletListView;
    private ArrayList<Wallet_hdp> walletList;

    private TextView select_category_txtview; // TextView tiêu đề "Select Category" / "From Wallet"
    private TextView select_wallet_txtview;   // TextView tiêu đề "Select Wallet" / "To Wallet"

    private String fromWalletIdStorage = "";
    private String targetWalletIdStorage = "";
    private String incomeWalletIdStorage = "";
    // private String incomeCategoryStorage = ""; // Đã thay bằng incomeCategoryStorageId
    private String expenseWalletIdStorage = "";
    // private String expenseCategoryStorage = ""; // Đã thay bằng expenseCategoryStorageId

    private String expenseWalletAmount = "";
    private String expenseWalletCurrency = "";
    private String fromWalletAmount = "";
    private String fromWalletCurrency = "";
    private ArrayList<Wallet_hdp> fromWalletList;
    private ArrayList<Wallet_hdp> targetWalletList;
    private boolean from_to_flag = true;

    private boolean income = true;
    private boolean expense = false;
    private boolean transfer = false;


    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Đổi tên hàm này để phân biệt với hàm nạp dữ liệu cho dialog
    private void AnhXaCategoryGlobal() {
        categoryListGlobal.clear();
        ArrayList<GetAllCategoryEntity> parseAPIList = new CategoryAPIUtil().getAllCategory();
        if (parseAPIList != null) {
            for (GetAllCategoryEntity category : parseAPIList) {
                categoryListGlobal.add(new Category_hdp(category.getId(), category.getName(), category.getPicture(), category.getType()));
            }
        } else {
            // Dữ liệu mẫu nếu API lỗi hoặc để demo
            Log.w("AddTransaction", "AnhXaCategoryGlobal: API returned null, using mock data.");
            categoryListGlobal.add(new Category_hdp("cat_id_1_mock", "Salary (Mock)", "https://cdn-icons-png.flaticon.com/128/2640/2640130.png", "INCOME"));
            categoryListGlobal.add(new Category_hdp("cat_id_2_mock", "Bonus (Mock)", "https://cdn-icons-png.flaticon.com/128/3135/3135706.png", "INCOME"));
            categoryListGlobal.add(new Category_hdp("cat_id_6_mock", "Gifts Received (Mock)", "https://cdn-icons-png.flaticon.com/128/1069/1069106.png", "INCOME"));

            // Thêm nhiều mục EXPENSE
            categoryListGlobal.add(new Category_hdp("cat_id_3_mock", "Food (Mock)", "https://cdn-icons-png.flaticon.com/128/857/857681.png", "EXPENSE"));
            categoryListGlobal.add(new Category_hdp("cat_id_4_mock", "Shopping (Mock)", "https://cdn-icons-png.flaticon.com/128/3081/3081751.png", "EXPENSE"));
            categoryListGlobal.add(new Category_hdp("cat_id_5_mock", "Transport (Mock)", "https://cdn-icons-png.flaticon.com/128/3063/3063831.png", "EXPENSE"));
        }

        Log.d("AddTransaction", "AnhXaCategoryGlobal: Loaded " + categoryListGlobal.size() + " categories.");
    }

    // Đổi tên hàm này để phân biệt
    private void RenderCategoryListForDialog() {
        renderCategoryListDialog = new ArrayList<>();
        if (!categoryListGlobal.isEmpty()) {
            String typeToFilter = "";
            if (income) {
                typeToFilter = "INCOME";
            } else if (expense) {
                typeToFilter = "EXPENSE";
            }

            if (!typeToFilter.isEmpty()) {
                for (Category_hdp category : categoryListGlobal) {
                    if (typeToFilter.equals(category.getCategory_type())) {
                        renderCategoryListDialog.add(category);
                    }
                }
            }
            Log.d("AddTransaction", "RenderCategoryListForDialog: Filtered " + renderCategoryListDialog.size() + " categories for type " + typeToFilter);
        } else {
            Log.w("AddTransaction", "RenderCategoryListForDialog: categoryListGlobal is empty.");
        }
    }


    private void AnhXaWallet() {
        try {
            walletList = new ArrayList<Wallet_hdp>();
            ArrayList<GetAllWalletsEntity> parseAPIList = new WalletAPIUtil().getAllWalletAPI();
            if (parseAPIList != null) {
                for (GetAllWalletsEntity item : parseAPIList) {
                    walletList.add(new Wallet_hdp(item.id, item.name, item.amount, R.drawable.wallet, item.currency_unit));
                }
            }
            else {
                // Dữ liệu mẫu nếu API lỗi hoặc để demo
                Log.w("AddTransaction", "AnhXaWallet: API returned null, using mock data.");
                walletList.add(new Wallet_hdp("wallet_id_1_mock", "Cash ", "1000000", R.drawable.wallet, "VND"));
                walletList.add(new Wallet_hdp("wallet_id_2_mock", "Bank Account", "5000000", R.drawable.wallet, "USD"));
                walletList.add(new Wallet_hdp("wallet_id_3_mock", "Credit Card ", "2000000", R.drawable.wallet, "EUR"));
            }
            Log.d("Get_wallet_data_object", walletList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Wallet_hdp> setupFromToWallet(String walletId) {
        ArrayList<Wallet_hdp> fromToWallet = new ArrayList<Wallet_hdp>();
        if (walletList != null) {
            for (Wallet_hdp wallet : walletList) {
                if (!wallet.getId().equals(walletId)) {
                    fromToWallet.add(wallet);
                }
            }
        }
        return fromToWallet;
    }

    private void showCategoryDialog() {
        RenderCategoryListForDialog();

        if (renderCategoryListDialog == null || renderCategoryListDialog.isEmpty()) {
            Toast.makeText(this, "No categories available for this type.", Toast.LENGTH_SHORT).show();
            Log.w("AddTransaction", "showCategoryDialog: No categories to display in dialog.");
            return;
        }

        final Dialog dialog = new Dialog(AddTransaction.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_select_category);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // Kiểm tra style tồn tại trước khi set
        if (getResources().getIdentifier("DialogAnimationn", "style", getPackageName()) != 0) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationn;
        }
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        // Kiểm tra drawable tồn tại trước khi set
        if (getResources().getIdentifier("dialog_bgh", "drawable", getPackageName()) != 0) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bgh);
        }


        dialog.show();

        categoryListViewDialog = dialog.findViewById(R.id.select_category_listview);
        TextView addnew = dialog.findViewById(R.id.select_category_addnew_btn);

        Category_adapter categoryAdapter = new Category_adapter(
                AddTransaction.this, // Sử dụng context của Activity
                R.layout.category_list_item, // THAY THẾ BẰNG TÊN FILE LAYOUT ITEM CATEGORY CỦA BẠN
                renderCategoryListDialog
        );
        categoryListViewDialog.setAdapter(categoryAdapter);

        categoryListViewDialog.setOnItemClickListener((parent, view, position, id) -> {
            if (renderCategoryListDialog != null && position < renderCategoryListDialog.size()) {
                Category_hdp selectedCategory = renderCategoryListDialog.get(position);
                if (income) {
                    incomeCategoryTextViewMain.setText(selectedCategory.getCategory_name());
                    Glide.with(AddTransaction.this)
                            .load(selectedCategory.getImageURL())
                            .placeholder(R.drawable.circle_icon)
                            .error(R.drawable.circle_icon)
                            .into(incomeCategoryIconMain);
                    incomeCategoryStorageId = selectedCategory.getCategory_id();
                    expenseCategoryStorageId = "";
                } else if (expense) {
                    expenseCategoryTextViewMain.setText(selectedCategory.getCategory_name());
                    Glide.with(AddTransaction.this)
                            .load(selectedCategory.getImageURL())
                            .placeholder(R.drawable.circle_icon)
                            .error(R.drawable.circle_icon)
                            .into(expenseCategoryIconMain);
                    expenseCategoryStorageId = selectedCategory.getCategory_id();
                    incomeCategoryStorageId = "";
                }
                Log.d("AddTransaction", "Category selected: " + selectedCategory.getCategory_name() + " (ID: " + selectedCategory.getCategory_id() + ")");
                dialog.dismiss();
            }
        });

        addnew.setOnClickListener(v -> {
            Intent AddCategoryIntent = new Intent(AddTransaction.this, Category_Add.class);
            startActivity(AddCategoryIntent);
            dialog.dismiss();
        });
    }


    private void showWalletDialog() {
        final Dialog dialog = new Dialog(AddTransaction.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_select_wallet);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (getResources().getIdentifier("DialogAnimationn", "style", getPackageName()) != 0) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationn;
        }
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        if (getResources().getIdentifier("dialog_bgh", "drawable", getPackageName()) != 0) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bgh);
        }
        dialog.show();

        walletListView = dialog.findViewById(R.id.select_wallet_listview);
        TextView fromWalletAmountTextView = findViewById(R.id.from_wallet_amount_txtview); // Tham chiếu đúng
        TextView targetWalletNameTextView = findViewById(R.id.target_wallet_name_txtview); // Tham chiếu đúng (nếu bạn đổi tên)

        if (transfer) {
            if (from_to_flag) { // Chọn "From Wallet"
                // Nếu "Target Wallet" chưa được chọn, hiển thị tất cả wallets
                // Ngược lại, hiển thị các wallets khác "Target Wallet"
                if (targetWalletIdStorage.isEmpty()) {
                    AnhXaWallet(); // Nạp lại nếu chưa có hoặc có thể đã thay đổi
                    SelectWallet_Adapter adapter = new SelectWallet_Adapter(AddTransaction.this, R.layout.select_wallet_item_list, walletList);
                    walletListView.setAdapter(adapter);
                } else {
                    fromWalletList = setupFromToWallet(targetWalletIdStorage);
                    SelectWallet_Adapter adapter = new SelectWallet_Adapter(AddTransaction.this, R.layout.select_wallet_item_list, fromWalletList);
                    walletListView.setAdapter(adapter);
                }
            } else { // Chọn "To Wallet"
                // "From Wallet" phải được chọn trước
                // Hiển thị các wallets khác "From Wallet"
                // AnhXaWallet(); // Nạp lại nếu chưa có hoặc có thể đã thay đổi (đảm bảo fromWalletIdStorage có giá trị)
                targetWalletList = setupFromToWallet(fromWalletIdStorage);
                SelectWallet_Adapter adapter = new SelectWallet_Adapter(AddTransaction.this, R.layout.select_wallet_item_list, targetWalletList);
                walletListView.setAdapter(adapter);
            }
        } else { // Income hoặc Expense
            AnhXaWallet();
            SelectWallet_Adapter adapter = new SelectWallet_Adapter(AddTransaction.this, R.layout.select_wallet_item_list, walletList);
            walletListView.setAdapter(adapter);
        }


        walletListView.setOnItemClickListener((parent, view, position, id) -> {
            Wallet_hdp selectedWallet = null;
            if (transfer) {
                if (from_to_flag) {
                    selectedWallet = (fromWalletList != null && !fromWalletList.isEmpty() && targetWalletIdStorage.isEmpty() == false) ? fromWalletList.get(position) : walletList.get(position);
                } else {
                    selectedWallet = targetWalletList.get(position);
                }
            } else {
                selectedWallet = walletList.get(position);
            }

            if (selectedWallet == null) {
                dialog.dismiss();
                return;
            }

            if (income) {
                ImageView walletIcon = findViewById(R.id.income_wallet_icon);
                walletIcon.setImageResource(selectedWallet.getImage());
                TextView walletTxtView = findViewById(R.id.income_wallet_name_txtview);
                walletTxtView.setText(selectedWallet.getWalletName());
                TextView walletMoney = findViewById(R.id.income_amount_txtview);
                String currencySymbol = getCurrencySymbol(selectedWallet.getCurrency());
                String amount = formatCurrency(Double.parseDouble(selectedWallet.getAmountMoney()), selectedWallet.getCurrency()) + " " + currencySymbol;
                walletMoney.setTextColor(getResources().getColor(R.color.xanhdam, null));
                walletMoney.setText(amount);
                incomeWalletIdStorage = selectedWallet.getId();
            } else if (expense) {
                ImageView walletIcon = findViewById(R.id.expense_wallet_icon);
                walletIcon.setImageResource(selectedWallet.getImage());
                TextView walletTxtView = findViewById(R.id.expense_wallet_name_txtview);
                walletTxtView.setText(selectedWallet.getWalletName());
                TextView walletMoney = findViewById(R.id.expense_amount_txtview);
                String currencySymbol = getCurrencySymbol(selectedWallet.getCurrency());
                String amount = formatCurrency(Double.parseDouble(selectedWallet.getAmountMoney()), selectedWallet.getCurrency()) + " " + currencySymbol;
                walletMoney.setTextColor(getResources().getColor(R.color.xanhdam, null));
                walletMoney.setText(amount);
                expenseWalletIdStorage = selectedWallet.getId();
                expenseWalletAmount = selectedWallet.getAmountMoney();
                expenseWalletCurrency = selectedWallet.getCurrency();
            } else if (transfer) {
                if (from_to_flag) {
                    ImageView walletIcon = findViewById(R.id.from_wallet_icon);
                    walletIcon.setImageResource(selectedWallet.getImage());
                    TextView walletTxtView = findViewById(R.id.from_wallet_txtview);
                    walletTxtView.setText(selectedWallet.getWalletName());
                    TextView walletMoney = findViewById(R.id.from_wallet_amount_txtview);
                    String currencySymbol = getCurrencySymbol(selectedWallet.getCurrency());
                    String amount = formatCurrency(Double.parseDouble(selectedWallet.getAmountMoney()), selectedWallet.getCurrency()) + " " + currencySymbol;
                    walletMoney.setText(amount);
                    walletMoney.setTextColor(getResources().getColor(R.color.xanhdam, null));
                    fromWalletIdStorage = selectedWallet.getId();
                    fromWalletAmount = selectedWallet.getAmountMoney();
                    fromWalletCurrency = selectedWallet.getCurrency();
                } else {
                    ImageView walletIcon = findViewById(R.id.target_wallet_icon);
                    walletIcon.setImageResource(selectedWallet.getImage());
                    TextView walletTxtView = findViewById(R.id.target_wallet_name_txtview);
                    walletTxtView.setText(selectedWallet.getWalletName());
                    TextView walletMoney = findViewById(R.id.target_wallet_amount_txtview);
                    String currencySymbol = getCurrencySymbol(selectedWallet.getCurrency());
                    String amount = formatCurrency(Double.parseDouble(selectedWallet.getAmountMoney()), selectedWallet.getCurrency()) + " " + currencySymbol;
                    walletMoney.setText(amount);
                    walletMoney.setTextColor(getResources().getColor(R.color.xanhdam, null));
                    targetWalletIdStorage = selectedWallet.getId();
                }
            }
            validateAmount(); // Sau khi chọn ví, validate lại amount
            dialog.dismiss();
        });

        TextView addNewWallet = dialog.findViewById(R.id.select_wallet_addnew_btn);
        addNewWallet.setOnClickListener(v -> {
            Intent AddWalletIntent = new Intent(AddTransaction.this, Home_New_wallet.class);
            startActivity(AddWalletIntent);
            dialog.dismiss();
        });
    }


    private void showCurrencyDialog() {
        final Dialog dialog = new Dialog(AddTransaction.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_currency);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (getResources().getIdentifier("DialogAnimationn", "style", getPackageName()) != 0) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationn;
        }
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        if (getResources().getIdentifier("dialog_bgh", "drawable", getPackageName()) != 0) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bgh);
        }
        dialog.show();

        LinearLayout bo1 = dialog.findViewById(R.id.bo1);
        LinearLayout bo2 = dialog.findViewById(R.id.bo2);
        LinearLayout bo3 = dialog.findViewById(R.id.bo3);
        LinearLayout bo4 = dialog.findViewById(R.id.bo4);
        MaterialButton txt1 = findViewById(R.id.add_trans_currency_btn); // Nên là MaterialButton

        // Đặt currency mặc định nếu chưa có
        if (currency == null || currency.isEmpty()) {
            currency = txt1.getText().toString();
        }


        // Reset background
        bo1.setBackgroundResource(0);
        bo2.setBackgroundResource(0);
        bo3.setBackgroundResource(0);
        bo4.setBackgroundResource(0);

        // Highlight lựa chọn hiện tại
        switch (currency) {
            case "USD":
                bo1.setBackgroundResource(R.drawable.nenluachon);
                break;
            case "VND":
                bo2.setBackgroundResource(R.drawable.nenluachon);
                break;
            case "EUR":
                bo3.setBackgroundResource(R.drawable.nenluachon);
                break;
            case "CNY":
                bo4.setBackgroundResource(R.drawable.nenluachon);
                break;
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
            bo1.setBackgroundResource(0);
            bo2.setBackgroundResource(0);
            bo4.setBackgroundResource(0);
        });
        bo4.setOnClickListener(v -> {
            currency = "CNY";
            bo4.setBackgroundResource(R.drawable.nenluachon);
            bo1.setBackgroundResource(0);
            bo2.setBackgroundResource(0);
            bo3.setBackgroundResource(0);
        });

        TextView ok = dialog.findViewById(R.id.apply);
        ok.setOnClickListener(v -> {
            MaterialButton btn = findViewById(R.id.add_trans_currency_btn);
            btn.setText(currency);
            TextInputLayout amountLayout = findViewById(R.id.Amount_txtbox_layout);
            amountLayout.setPrefixText(getCurrencySymbol(currency));
            validateAmount();
            dialog.dismiss();
        });
    }

    private String getCurrencySymbol(String currencyCode) {
        switch (currencyCode) {
            case "USD": return "$";
            case "VND": return "₫";
            case "EUR": return "€";
            case "CNY": return "¥";
            default: return "";
        }
    }

    private void setIncomeBackground(MaterialButton income_btn) {
        if (income) {
            income_btn.setBackgroundColor(getResources().getColor(R.color.xanhdam, null));
            income_btn.setTextColor(getResources().getColor(R.color.white, null));
            income_btn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
            income_btn.setRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.xanhnhat, null)));
        } else {
            income_btn.setBackgroundColor(getResources().getColor(R.color.white, null));
            income_btn.setTextColor(getResources().getColor(R.color.black, null));
            income_btn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.black, null)));
            income_btn.setRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.xanhdam, null)));
        }
    }

    private void setExpenseBackground(MaterialButton expense_btn) {
        if (expense) {
            expense_btn.setBackgroundColor(getResources().getColor(R.color.red, null));
            expense_btn.setTextColor(getResources().getColor(R.color.white, null));
            expense_btn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
            expense_btn.setRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.lightred, null)));
        } else {
            expense_btn.setBackgroundColor(getResources().getColor(R.color.white, null));
            expense_btn.setTextColor(getResources().getColor(R.color.black, null));
            expense_btn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.black, null)));
            expense_btn.setRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.red, null)));
        }
    }

    private void setTransferBackground(MaterialButton transfer_btn) {
        if (transfer) {
            transfer_btn.setBackgroundColor(getResources().getColor(R.color.xanhnen, null));
            transfer_btn.setTextColor(getResources().getColor(R.color.white, null));
            transfer_btn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
            transfer_btn.setRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.xanhnennhat, null)));
        } else {
            transfer_btn.setBackgroundColor(getResources().getColor(R.color.white, null));
            transfer_btn.setTextColor(getResources().getColor(R.color.black, null));
            transfer_btn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.black, null)));
            transfer_btn.setRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.xanhnen, null)));
        }
    }

    private void resetCategorySelectionUIIfNeeded() {
        Log.d("AddTransaction", "resetCategorySelectionUIIfNeeded called.");
        if (incomeCategoryTextViewMain != null) {
            incomeCategoryTextViewMain.setText("Choose your category");
        }
        if (incomeCategoryIconMain != null) {
            incomeCategoryIconMain.setImageResource(R.drawable.circle_icon);
        }
        incomeCategoryStorageId = "";

        if (expenseCategoryTextViewMain != null) {
            expenseCategoryTextViewMain.setText("Choose your category");
        }
        if (expenseCategoryIconMain != null) {
            expenseCategoryIconMain.setImageResource(R.drawable.circle_icon);
        }
        expenseCategoryStorageId = "";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_transaction), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ View cho Category trên UI chính
        incomeCategoryTextViewMain = findViewById(R.id.income_category_txtview);
        incomeCategoryIconMain = findViewById(R.id.income_category_icon);
        expenseCategoryTextViewMain = findViewById(R.id.expense_category_txtview);
        expenseCategoryIconMain = findViewById(R.id.expense_category_icon);

        // TextViews tiêu đề
        select_category_txtview = findViewById(R.id.select_category_txtview);
        select_wallet_txtview = findViewById(R.id.select_wallet_txtview);


        ImageButton close_btn = findViewById(R.id.close_button);
        close_btn.setOnClickListener(view -> finish());

        MaterialButton income_btn = findViewById(R.id.income_button);
        MaterialButton expense_btn = findViewById(R.id.expense_button);
        MaterialButton transfer_btn = findViewById(R.id.transfer_button);

        // Thiết lập trạng thái ban đầu
        setIncomeBackground(income_btn);
        setExpenseBackground(expense_btn);
        setTransferBackground(transfer_btn);
        resetCategorySelectionUIIfNeeded(); // Reset UI category ban đầu

        LinearLayout income_category_layout_main = findViewById(R.id.income_category_layout);
        LinearLayout expense_category_layout_main = findViewById(R.id.expense_category_layout);
        LinearLayout from_wallet_layout_main = findViewById(R.id.from_wallet_layout); // Đổi tên để rõ ràng
        LinearLayout income_wallet_layout_main = findViewById(R.id.income_wallet_layout);
        LinearLayout expense_wallet_layout_main = findViewById(R.id.expense_wallet_layout);
        LinearLayout target_wallet_layout_main = findViewById(R.id.target_wallet_layout);


        // Thiết lập hiển thị ban đầu (Income là default)
        income_category_layout_main.setVisibility(View.VISIBLE);
        expense_category_layout_main.setVisibility(View.GONE);
        from_wallet_layout_main.setVisibility(View.GONE);
        income_wallet_layout_main.setVisibility(View.VISIBLE);
        expense_wallet_layout_main.setVisibility(View.GONE);
        target_wallet_layout_main.setVisibility(View.GONE);
        if(select_category_txtview != null) select_category_txtview.setText("Select Category");
        if(select_wallet_txtview != null) select_wallet_txtview.setText("Select Wallet");

        // Nạp dữ liệu category toàn cục
        AnhXaCategoryGlobal();


        income_btn.setOnClickListener(view -> {
            income = true;
            expense = false;
            transfer = false;
            setIncomeBackground(income_btn);
            setExpenseBackground(expense_btn);
            setTransferBackground(transfer_btn);
            resetCategorySelectionUIIfNeeded();

            if(select_category_txtview != null) select_category_txtview.setText("Select Category");
            if(select_wallet_txtview != null) select_wallet_txtview.setText("Select Wallet");

            income_category_layout_main.setVisibility(View.VISIBLE);
            expense_category_layout_main.setVisibility(View.GONE);
            from_wallet_layout_main.setVisibility(View.GONE);
            income_wallet_layout_main.setVisibility(View.VISIBLE);
            expense_wallet_layout_main.setVisibility(View.GONE);
            target_wallet_layout_main.setVisibility(View.GONE);
        });

        expense_btn.setOnClickListener(view -> {
            income = false;
            expense = true;
            transfer = false;
            setIncomeBackground(income_btn);
            setExpenseBackground(expense_btn);
            setTransferBackground(transfer_btn);
            resetCategorySelectionUIIfNeeded();

            if(select_category_txtview != null) select_category_txtview.setText("Select Category");
            if(select_wallet_txtview != null) select_wallet_txtview.setText("Select Wallet");

            income_category_layout_main.setVisibility(View.GONE);
            expense_category_layout_main.setVisibility(View.VISIBLE);
            from_wallet_layout_main.setVisibility(View.GONE);
            income_wallet_layout_main.setVisibility(View.GONE);
            expense_wallet_layout_main.setVisibility(View.VISIBLE);
            target_wallet_layout_main.setVisibility(View.GONE);
        });

        transfer_btn.setOnClickListener(view -> {
            income = false;
            expense = false;
            transfer = true;
            setIncomeBackground(income_btn);
            setExpenseBackground(expense_btn);
            setTransferBackground(transfer_btn);
            resetCategorySelectionUIIfNeeded();

            if(select_category_txtview != null) select_category_txtview.setText("From Wallet");
            if(select_wallet_txtview != null) select_wallet_txtview.setText("To Wallet");

            income_category_layout_main.setVisibility(View.GONE);
            expense_category_layout_main.setVisibility(View.GONE);
            from_wallet_layout_main.setVisibility(View.VISIBLE);
            income_wallet_layout_main.setVisibility(View.GONE);
            expense_wallet_layout_main.setVisibility(View.GONE);
            target_wallet_layout_main.setVisibility(View.VISIBLE);
        });

        income_category_layout_main.setOnClickListener(view -> {
            if(income) showCategoryDialog();
        });

        expense_category_layout_main.setOnClickListener(view -> {
            if(expense) showCategoryDialog();
        });

        from_wallet_layout_main.setOnClickListener(view -> {
            from_to_flag = true;
            showWalletDialog();
        });

        income_wallet_layout_main.setOnClickListener(view -> {
            if(income) showWalletDialog(); // Chỉ show nếu là income
        });

        expense_wallet_layout_main.setOnClickListener(view -> {
            if(expense) showWalletDialog(); // Chỉ show nếu là expense
        });

        target_wallet_layout_main.setOnClickListener(view -> {
            from_to_flag = false;
            TextView currentFromWalletAmount = findViewById(R.id.from_wallet_amount_txtview);
            if (currentFromWalletAmount.getText().toString().isEmpty()) {
                Toast.makeText(AddTransaction.this, "Please select from wallet first", Toast.LENGTH_SHORT).show();
            } else {
                showWalletDialog();
            }
        });

        MaterialButton currencyBtn = findViewById(R.id.add_trans_currency_btn);
        currencyBtn.setOnClickListener(view -> showCurrencyDialog());

        TextInputEditText dateTextBox = findViewById(R.id.select_date_txtbox);
        dateTextBox.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddTransaction.this,
                    // Kiểm tra style tồn tại trước khi sử dụng
                    (getResources().getIdentifier("CustomDatePickerDialogTheme", "style", getPackageName()) != 0) ? R.style.CustomDatePickerDialogTheme : android.R.style.Theme_DeviceDefault_Dialog_Alert,
                    (datePickerView, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dateTextBox.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        try {
            dateTextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        validateDate();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> {
            TextInputEditText amountEditText = findViewById(R.id.Amount_txtbox);
            TextInputEditText dateEditText = findViewById(R.id.select_date_txtbox);
            TextInputEditText noteEditText = findViewById(R.id.note_txtbox);
            String amountStr = amountEditText.getText().toString().replaceAll("[.,]", "");
            String currentSelectedCurrency = ((MaterialButton) findViewById(R.id.add_trans_currency_btn)).getText().toString();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!validate()) {
                    Toast.makeText(AddTransaction.this, "An error(s) has occurred!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // Validation cơ bản cho API < O nếu cần
                if (amountStr.isEmpty() || dateEditText.getText().toString().isEmpty()) {
                    Toast.makeText(AddTransaction.this, "Please fill all required fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = originalFormat.parse(dateEditText.getText().toString());
            } catch (ParseException e) {
                Log.e("SaveButton", "Date parsing error", e);
                Toast.makeText(AddTransaction.this, "Invalid date format.", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = targetFormat.format(date);
            String note = noteEditText.getText().toString();
            TransactionAPIUtil transactionAPIUtil = new TransactionAPIUtil();

            String categoryIdToSend = null;
            String walletIdToSend = null;
            String transactionType = "";

            if (income) {
                if (incomeCategoryStorageId.isEmpty()) {
                    Toast.makeText(AddTransaction.this, "Please select an income category.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (incomeWalletIdStorage.isEmpty()) {
                    Toast.makeText(AddTransaction.this, "Please select an income wallet.", Toast.LENGTH_SHORT).show();
                    return;
                }
                categoryIdToSend = incomeCategoryStorageId;
                walletIdToSend = incomeWalletIdStorage;
                transactionType = "INCOME";
            } else if (expense) {
                if (expenseCategoryStorageId.isEmpty()) {
                    Toast.makeText(AddTransaction.this, "Please select an expense category.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (expenseWalletIdStorage.isEmpty()) {
                    Toast.makeText(AddTransaction.this, "Please select an expense wallet.", Toast.LENGTH_SHORT).show();
                    return;
                }
                categoryIdToSend = expenseCategoryStorageId;
                walletIdToSend = expenseWalletIdStorage;
                transactionType = "EXPENSE";
            } else if (transfer) {
                if (fromWalletIdStorage.isEmpty() || targetWalletIdStorage.isEmpty()) {
                    Toast.makeText(AddTransaction.this, "Please select both from and to wallets for transfer.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // categoryIdToSend là null cho transfer
                walletIdToSend = fromWalletIdStorage; // Wallet nguồn
                transactionType = "TRANSFER";
            }

            CreateTransactionEntity createTransactionEntity = new CreateTransactionEntity(
                    Double.parseDouble(amountStr),
                    formattedDate,
                    categoryIdToSend,
                    walletIdToSend,
                    note,
                    url, // Image URL
                    transactionType,
                    currentSelectedCurrency,
                    (transfer ? targetWalletIdStorage : null) // target_wallet_id chỉ cho transfer
            );

            try {
                boolean success = transactionAPIUtil.createTransactionAPI(createTransactionEntity);
                if(success) {
                    Toast.makeText(AddTransaction.this, "Add transaction successfully", Toast.LENGTH_SHORT).show();
                    if (income) excuteIncome(incomeWalletIdStorage);
                    else if (expense) excuteExpense(expenseWalletIdStorage);
                    else if (transfer) excuteTransfer(fromWalletIdStorage, targetWalletIdStorage);
                    setResult(RESULT_OK); // Gửi kết quả về Activity trước đó nếu cần
                    finish();
                } else {
                    Toast.makeText(AddTransaction.this, "Failed to add transaction. Please try again.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("SaveTransaction", "API call failed", e);
                Toast.makeText(AddTransaction.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        try {
            TextInputEditText amountEditText = findViewById(R.id.Amount_txtbox);
            amountEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isUserInteracting && !s.toString().isEmpty()) {
                        isUserInteracting = false;
                        try {
                            String cleanString = s.toString().replaceAll("[.,]", "");
                            if (!cleanString.isEmpty()) {
                                double amountValue = Double.parseDouble(cleanString);
                                String currentSelectedCurrency = ((MaterialButton) findViewById(R.id.add_trans_currency_btn)).getText().toString();
                                String formattedAmount = formatCurrency(amountValue, currentSelectedCurrency);
                                amountEditText.setText(formattedAmount);
                                amountEditText.setSelection(formattedAmount.length());
                            }
                        } catch (NumberFormatException e) {
                            Log.e("AmountFormat", "Error formatting amount", e);
                        } finally {
                            isUserInteracting = true; // Luôn đặt lại ở đây
                        }
                        validateAmount(); // Gọi validate sau khi setText
                    } else if (s.toString().isEmpty()){
                        isUserInteracting = false; // Tránh vòng lặp khi xóa hết
                        validateAmount(); // Validate khi trống
                        isUserInteracting = true;
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                    // isUserInteracting = true; // Đã chuyển vào onTextChanged
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        registerResult();
        transactionImage = findViewById(R.id.transaction_image);
        transactionImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            resultLauncher.launch(intent);
        });
    }

    private void excuteIncome(String incomeWalletID) { // Đổi tên tham số
        doitiente doi_tien_te = new doitiente();
        if (walletList == null) AnhXaWallet(); // Đảm bảo walletList có dữ liệu
        if (walletList == null) return; // Không thể thực thi nếu vẫn null

        for (Wallet_hdp wallet : walletList) {
            if (wallet.getId().equals(incomeWalletID)) {
                String currencies = ((MaterialButton) findViewById(R.id.add_trans_currency_btn)).getText().toString();
                TextInputEditText amountEditText = findViewById(R.id.Amount_txtbox);
                String amountStr = amountEditText.getText().toString().replaceAll("[.,]", "");
                if (amountStr.isEmpty()) return;

                double transactionAmountInVND = doi_tien_te.converttoVND(currencies, Double.parseDouble(amountStr));
                double currentWalletAmountInVND = doi_tien_te.converttoVND(wallet.getCurrency(), Double.parseDouble(wallet.getAmountMoney()));
                double newWalletAmountInVND = currentWalletAmountInVND + transactionAmountInVND;

                String newAmountInWalletCurrency = String.valueOf(doi_tien_te.convertFromVND(wallet.getCurrency(), newWalletAmountInVND));
                wallet.setAmountMoney(newAmountInWalletCurrency);

                WalletAPIUtil walletAPIUtil = new WalletAPIUtil();
                walletAPIUtil.updateWalletAPI(incomeWalletID, new UpdateWalletEntity(wallet.getWalletName(), Double.parseDouble(wallet.getAmountMoney()), wallet.getCurrency()));
                break;
            }
        }
    }

    private void excuteExpense(String expenseWalletID) { // Đổi tên tham số
        doitiente doi_tien_te = new doitiente();
        if (walletList == null) AnhXaWallet();
        if (walletList == null) return;

        for (Wallet_hdp wallet : walletList) {
            if (wallet.getId().equals(expenseWalletID)) {
                String currencies = ((MaterialButton) findViewById(R.id.add_trans_currency_btn)).getText().toString();
                TextInputEditText amountEditText = findViewById(R.id.Amount_txtbox);
                String amountStr = amountEditText.getText().toString().replaceAll("[.,]", "");
                if (amountStr.isEmpty()) return;

                double transactionAmountInVND = doi_tien_te.converttoVND(currencies, Double.parseDouble(amountStr));
                double currentWalletAmountInVND = doi_tien_te.converttoVND(wallet.getCurrency(), Double.parseDouble(wallet.getAmountMoney()));
                double newWalletAmountInVND = currentWalletAmountInVND - transactionAmountInVND;

                String newAmountInWalletCurrency = String.valueOf(doi_tien_te.convertFromVND(wallet.getCurrency(), newWalletAmountInVND));
                wallet.setAmountMoney(newAmountInWalletCurrency);

                WalletAPIUtil walletAPIUtil = new WalletAPIUtil();
                walletAPIUtil.updateWalletAPI(expenseWalletID, new UpdateWalletEntity(wallet.getWalletName(), Double.parseDouble(wallet.getAmountMoney()), wallet.getCurrency()));
                break;
            }
        }
    }

    private void excuteTransfer(String fromID, String targetID) {
        doitiente doi_tien_te = new doitiente();
        if (walletList == null) AnhXaWallet();
        if (walletList == null) return;

        Wallet_hdp fromWallet = null;
        Wallet_hdp targetWallet = null;

        for(Wallet_hdp w : walletList) {
            if(w.getId().equals(fromID)) fromWallet = w;
            if(w.getId().equals(targetID)) targetWallet = w;
        }

        if(fromWallet == null || targetWallet == null) {
            Log.e("ExecuteTransfer", "From or Target wallet not found in list.");
            return;
        }

        String currencies = ((MaterialButton) findViewById(R.id.add_trans_currency_btn)).getText().toString();
        TextInputEditText amountEditText = findViewById(R.id.Amount_txtbox);
        String amountStr = amountEditText.getText().toString().replaceAll("[.,]", "");
        if (amountStr.isEmpty()) return;
        double transactionAmount = Double.parseDouble(amountStr);

        // Xử lý From Wallet
        double fromWalletOriginalAmount = Double.parseDouble(fromWallet.getAmountMoney());
        double fromWalletAmountInVND = doi_tien_te.converttoVND(fromWallet.getCurrency(), fromWalletOriginalAmount);
        double transactionAmountInVND = doi_tien_te.converttoVND(currencies, transactionAmount);
        double newFromWalletAmountInVND = fromWalletAmountInVND - transactionAmountInVND;
        fromWallet.setAmountMoney(String.valueOf(doi_tien_te.convertFromVND(fromWallet.getCurrency(), newFromWalletAmountInVND)));
        new WalletAPIUtil().updateWalletAPI(fromID, new UpdateWalletEntity(fromWallet.getWalletName(), Double.parseDouble(fromWallet.getAmountMoney()), fromWallet.getCurrency()));

        // Xử lý Target Wallet
        double targetWalletOriginalAmount = Double.parseDouble(targetWallet.getAmountMoney());
        double targetWalletAmountInVND = doi_tien_te.converttoVND(targetWallet.getCurrency(), targetWalletOriginalAmount);
        // Số tiền chuyển có thể khác đơn vị tiền tệ với target wallet, nên cần quy đổi số tiền giao dịch sang VND trước, rồi cộng, rồi quy đổi lại
        // transactionAmountInVND đã tính ở trên
        double newTargetWalletAmountInVND = targetWalletAmountInVND + transactionAmountInVND;
        targetWallet.setAmountMoney(String.valueOf(doi_tien_te.convertFromVND(targetWallet.getCurrency(), newTargetWalletAmountInVND)));
        new WalletAPIUtil().updateWalletAPI(targetID, new UpdateWalletEntity(targetWallet.getWalletName(), Double.parseDouble(targetWallet.getAmountMoney()), targetWallet.getCurrency()));
    }


    public static String formatCurrency(double amount, String currency) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        if ("VND".equals(currency)) {
            symbols.setGroupingSeparator('.');
        } else {
            symbols.setGroupingSeparator(',');
        }
        symbols.setDecimalSeparator('.'); // Luôn dùng dấu chấm cho phần thập phân khi parse
        DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
        formatter.setMinimumFractionDigits(0); // Không hiển thị .00 nếu là số nguyên
        formatter.setMaximumFractionDigits(2); // Tối đa 2 chữ số thập phân
        return formatter.format(amount);
    }

    private boolean validateAmount() {
        String selectedCurrencyOnButton = ((MaterialButton) findViewById(R.id.add_trans_currency_btn)).getText().toString();
        TextInputLayout amountEditTextLayout = findViewById(R.id.Amount_txtbox_layout);
        TextInputEditText amountEditText = findViewById(R.id.Amount_txtbox);
        String amountInput = amountEditText.getText().toString().replaceAll("[.,]", "").trim();

        if (amountInput.isEmpty()) {
            amountEditTextLayout.setError("Amount can't be empty!");
            amountEditText.setTextColor(getResources().getColor(R.color.errorColor, null));
            amountEditTextLayout.setPrefixTextColor(ColorStateList.valueOf(getResources().getColor(R.color.errorColor, null)));
            return false;
        }

        try {
            double amountValue = Double.parseDouble(amountInput);
            if (amountValue <= 0) { // Amount phải lớn hơn 0
                amountEditTextLayout.setError("Please enter a positive amount!");
                amountEditText.setTextColor(getResources().getColor(R.color.errorColor, null));
                amountEditTextLayout.setPrefixTextColor(ColorStateList.valueOf(getResources().getColor(R.color.errorColor, null)));
                return false;
            }

            doitiente doi_tien_te = new doitiente();
            if (expense) {
                if (!expenseWalletAmount.isEmpty() && !expenseWalletCurrency.isEmpty()) {
                    double transactionAmountInVND = doi_tien_te.converttoVND(selectedCurrencyOnButton, amountValue);
                    double currentWalletBalanceInVND = doi_tien_te.converttoVND(expenseWalletCurrency, Double.parseDouble(expenseWalletAmount));
                    if (transactionAmountInVND > currentWalletBalanceInVND) {
                        amountEditTextLayout.setError("Expense amount can't be greater than wallet balance!");
                        amountEditText.setTextColor(getResources().getColor(R.color.errorColor, null));
                        amountEditTextLayout.setPrefixTextColor(ColorStateList.valueOf(getResources().getColor(R.color.errorColor, null)));
                        return false;
                    }
                }
            } else if (transfer) {
                if (!fromWalletAmount.isEmpty() && !fromWalletCurrency.isEmpty()) {
                    double transactionAmountInVND = doi_tien_te.converttoVND(selectedCurrencyOnButton, amountValue);
                    double currentWalletBalanceInVND = doi_tien_te.converttoVND(fromWalletCurrency, Double.parseDouble(fromWalletAmount));
                    if (transactionAmountInVND > currentWalletBalanceInVND) {
                        amountEditTextLayout.setError("Transfer amount can't be greater than wallet balance!");
                        amountEditText.setTextColor(getResources().getColor(R.color.errorColor, null));
                        amountEditTextLayout.setPrefixTextColor(ColorStateList.valueOf(getResources().getColor(R.color.errorColor, null)));
                        return false;
                    }
                }
            }
            amountEditTextLayout.setError(null);
            amountEditText.setTextColor(getResources().getColor(R.color.xanhnen, null));
            amountEditTextLayout.setPrefixTextColor(ColorStateList.valueOf(getResources().getColor(R.color.xanhnen, null)));
            return true;

        } catch (NumberFormatException e) {
            amountEditTextLayout.setError("Invalid amount format!");
            amountEditText.setTextColor(getResources().getColor(R.color.errorColor, null));
            amountEditTextLayout.setPrefixTextColor(ColorStateList.valueOf(getResources().getColor(R.color.errorColor, null)));
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validateDate() {
        TextInputLayout dateEditTextLayout = findViewById(R.id.select_date_txtbox_layout);
        TextInputEditText dateEditText = findViewById(R.id.select_date_txtbox);
        String dateInput = dateEditText.getText().toString().trim();

        if (dateInput.isEmpty()) {
            dateEditTextLayout.setError("Please press calendar icon to select a date!");
            dateEditTextLayout.setStartIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.errorColor, null)));
            return false;
        }
        try {
            // Thêm check cho định dạng ngày
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate selectedDate = LocalDate.parse(dateInput, formatter);
            if (selectedDate.isAfter(LocalDate.now())) {
                dateEditTextLayout.setError("Date can't be in the future!");
                dateEditText.setTextColor(getResources().getColor(R.color.errorColor, null)); // Sử dụng getResources().getColor
                dateEditTextLayout.setStartIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.errorColor, null)));
                return false;
            }
            dateEditTextLayout.setError(null);
            dateEditTextLayout.setStartIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.black, null)));
            dateEditText.setTextColor(getResources().getColor(R.color.black, null)); // Sử dụng getResources().getColor
            return true;
        } catch (Exception e) { // Bắt lỗi parse ngày
            dateEditTextLayout.setError("Invalid date format!");
            dateEditTextLayout.setStartIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.errorColor, null)));
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validate() {
        boolean isWalletValid = true;
        boolean isCategoryValid = true;

        if (income) {
            if (incomeWalletIdStorage.isEmpty()) isWalletValid = false;
            if (incomeCategoryStorageId.isEmpty()) isCategoryValid = false;
        } else if (expense) {
            if (expenseWalletIdStorage.isEmpty()) isWalletValid = false;
            if (expenseCategoryStorageId.isEmpty()) isCategoryValid = false;
        } else if (transfer) { // For transfer, category is not needed, but wallets are
            if (fromWalletIdStorage.isEmpty() || targetWalletIdStorage.isEmpty()) isWalletValid = false;
            isCategoryValid = true; // Category not applicable for transfer
        }

        boolean isAmountValid = validateAmount();
        boolean isDateValid = validateDate();

        if(!isWalletValid && income) Toast.makeText(this, "Please select an income wallet.", Toast.LENGTH_SHORT).show();
        else if(!isWalletValid && expense) Toast.makeText(this, "Please select an expense wallet.", Toast.LENGTH_SHORT).show();
        else if(!isWalletValid && transfer) Toast.makeText(this, "Please select both wallets for transfer.", Toast.LENGTH_SHORT).show();

        if(!isCategoryValid && income) Toast.makeText(this, "Please select an income category.", Toast.LENGTH_SHORT).show();
        else if(!isCategoryValid && expense) Toast.makeText(this, "Please select an expense category.", Toast.LENGTH_SHORT).show();


        return isAmountValid && isDateValid && isWalletValid && isCategoryValid;
    }


    private String uploadImageAPI(Uri imageUri) throws IOException, JSONException {
        String SERVER = API_Config.SERVER;
        String API_VERSION = API_Config.API_VERSION;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        File file = getFileFromUri(this, imageUri);
        if (file == null) {
            Log.e("UploadImage", "File from URI is null");
            throw new IOException("Could not get file from URI");
        }
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream"))) // Sửa lại RequestBody.create
                .build();
        Request request = new Request.Builder()
                .url(SERVER + "/" + API_VERSION + "/media/upload")
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            Log.e("UploadImage", "Upload failed: " + response.body() != null ? response.body().string() : "Unknown error");
            throw new IOException("Unexpected code " + response);
        }
        return response.body().string();
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        try {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                transactionImage.setImageURI(imageUri);
                                String responseStr = uploadImageAPI(imageUri);
                                Log.d("UploadResponse", responseStr);
                                JSONObject json = new JSONObject(responseStr);
                                if (json.has("data") && json.getJSONObject("data").has("picture_url")) {
                                    url = json.getJSONObject("data").getString("picture_url");
                                    Log.d("ImageData", "Image URL: " + url);
                                } else {
                                    Log.e("ImageData", "picture_url not found in response");
                                    Toast.makeText(this, "Failed to get image URL from server.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("ImageResult", "Error processing image result", e);
                            Toast.makeText(this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                Log.e("GetFileName", "Error getting file name from content URI", e);
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        // Thêm phần mở rộng nếu tên file không có
        if (result != null && !result.contains(".")) {
            String mimeType = context.getContentResolver().getType(uri);
            if (mimeType != null) {
                if (mimeType.equals("image/jpeg")) result += ".jpg";
                else if (mimeType.equals("image/png")) result += ".png";
                // Thêm các loại mime khác nếu cần
            } else {
                result += ".jpg"; // Mặc định nếu không lấy được mime type
            }
        }
        return result != null ? result : "temp_image.jpg"; // Trả về tên mặc định nếu null
    }

    private File getFileFromUri(Context context, Uri uri) {
        File file = null;
        String fileName = getFileName(context, uri); // Sử dụng tên file đã có phần mở rộng
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                file = new File(context.getCacheDir(), fileName);
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024 * 4]; // Tăng buffer size
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("GetFileFromUri", "Error creating file from URI", e);
            return null; // Trả về null nếu có lỗi
        }
        return file;
    }
}