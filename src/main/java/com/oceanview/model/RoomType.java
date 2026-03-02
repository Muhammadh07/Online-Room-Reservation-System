package com.oceanview.model;

public class RoomType {
    private int typeId;
    private String typeName;
    private String description;
    private double basePrice;

    public RoomType() {}
    public RoomType(int typeId, String typeName, double basePrice) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.basePrice = basePrice;
    }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
}