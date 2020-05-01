package com.example.mango;

public class Icon {
    private int itype;
    private int iId;
    private String iName;
    private String devName;
    private String address;

    public Icon() {
    }

    public Icon(int iId, String iName, String address) {
        this.iId = iId;
        this.iName = iName;

        if(iId == R.drawable.m1)
            itype = 1;
        else if(iId == R.drawable.m2)
            itype = 2;
        else if(iId == R.drawable.m3)
            itype = 3;
        else if(iId == R.drawable.m4)
            itype = 4;

        this.address = address.split("\n")[0];
        this.devName = address.split("\n")[1];
    }

    public int getItype() {
        return itype;
    }

    public int getiId() {
        return iId;
    }

    public String getiName() {
        return iName;
    }

    public String getAddress() {
        return address;
    }

    public String getDevName() {
        return devName;
    }

    public void setiId(int iId) {
        this.iId = iId;

        if(iId == R.drawable.m1)
            itype = 1;
        else if(iId == R.drawable.m2)
            itype = 2;
        else if(iId == R.drawable.m3)
            itype = 3;
        else if(iId == R.drawable.m4)
            itype = 4;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }

    public void setAddress(String iAddress) {
        this.address = iAddress;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }
}
