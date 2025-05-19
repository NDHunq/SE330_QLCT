package com.example.qlct.API_Entity;
import java.io.Serializable;

public class GetAllWalletsEntity implements Serializable {
    public String id;
    public String name;
    public String amount;
    public String currency_unit;
    public String create_at;
    public String update_at;

    public GetAllWalletsEntity(String id, String name, String amount, String currency_unit) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.currency_unit = currency_unit;
    }
}