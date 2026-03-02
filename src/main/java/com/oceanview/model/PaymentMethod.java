package com.oceanview.model;

public class PaymentMethod {
    private int    methodId;
    private String methodName;
    private boolean isActive = true;

    public int     getMethodId()            { return methodId; }
    public void    setMethodId(int id)      { this.methodId = id; }
    public String  getMethodName()          { return methodName; }
    public void    setMethodName(String n)  { this.methodName = n; }
    public boolean isActive()               { return isActive; }
    public void    setActive(boolean a)     { this.isActive = a; }
}
