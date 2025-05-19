package com.example.qlct.API_Entity;

import java.io.Serializable;

public class GetAllTransactionsEntity implements Serializable {
    public String id;
    public String picture;
    public Category category;
    public String amount;
    public String transaction_date;
    public String transaction_type;
    public String notes;
    public String currency_unit;
    public String wallet_id;
    public String target_wallet_id;

    public GetAllTransactionsEntity(
            String id,
            String picture,
            Category category,
            String amount,
            String transaction_date,
            String transaction_type,
            String notes,
            String currency_unit,
            String wallet_id,
            String target_wallet_id
    ) {
        this.id = id;
        this.picture = picture;
        this.category = category;
        this.amount = amount;
        this.transaction_date = transaction_date;
        this.transaction_type = transaction_type;
        this.notes = notes;
        this.currency_unit = currency_unit;
        this.wallet_id = wallet_id;
        this.target_wallet_id = target_wallet_id;
    }
}