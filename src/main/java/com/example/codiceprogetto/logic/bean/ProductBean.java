package com.example.codiceprogetto.logic.bean;

public class ProductBean {
    private String name;
    private String id;
    private int unitsToOrder;
    private String size;
    public ProductBean(String name, String id, int unitsToOrder) {
        this.name = name;
        this.id = id;
        this.unitsToOrder = unitsToOrder;
    }

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public int getUnitsToOrder() {
        return unitsToOrder;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setUnitsToOrder(int unitsToOrder) {
        this.unitsToOrder = unitsToOrder;
    }
}
