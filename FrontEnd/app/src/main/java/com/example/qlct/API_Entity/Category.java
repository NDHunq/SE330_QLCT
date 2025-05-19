// In file: app/src/main/java/com/example/qlct/API_Entity/Category.java
package com.example.qlct.API_Entity;

import java.io.Serializable;

public class Category implements Serializable {
    public String id;
    public String name;
    public String picture;
    public String type;

    public Category(String id, String name, String picture, String type) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.type = type;
    }
}