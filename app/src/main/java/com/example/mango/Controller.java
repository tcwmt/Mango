package com.example.mango;

import android.content.Context;

public class Controller {
    public Context context;
    public MainActivity mainActivity;
    public ScanActivity scanActivity;
    public ConnectActivity connectActivity;
    private static Controller controller;
    private Controller() {}

    public static Controller getInstance () {
        if(controller == null) {
            controller = new Controller();
        }

        return controller;
    }

    public void setMainActivity(MainActivity activity) {
        this.mainActivity = activity;
    }
    public void setScanActivity(ScanActivity activity) {
        this.scanActivity = activity;
    }
    public void setConnActivity(ConnectActivity activity) {
        this.connectActivity = activity;
    }

    public int devType = 1;
    public static final int methods_Init = 0;

    private Uimanagement uimanagement;
    public Uimanagement getUimanagement() {
        return uimanagement;
    }
    private void setUimanagement(Uimanagement uimanagement) {
        this.uimanagement = uimanagement;
    }

    public Object methods(int itype) {
        Object object = new Object();

        switch (itype) {
            case methods_Init:
                setUimanagement(new Uimanagement(mainActivity));
                break;
        }

        return object;
    }
}
