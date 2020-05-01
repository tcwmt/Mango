package com.example.mango;

import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.view.View;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.mango.MainActivity.SIMPLEPROFILE_CHAR1_UUID;
import static com.example.mango.MainActivity.SIMPLEPROFILE_CHAR2_UUID;
import static com.example.mango.MainActivity.SIMPLEPROFILE_CHAR7_UUID;
import static com.example.mango.MainActivity.SIMPLEPROFILE_CHAR8_UUID;
import static com.example.mango.MainActivity.SIMPLEPROFILE_SERV_UUID;

public class ConnectActivity extends AppCompatActivity {
    private MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        Controller.getInstance().context = this;
        Controller.getInstance().setConnActivity(this);

        Controller.getInstance().getUimanagement().menuview = findViewById(R.id.menuview);
        Controller.getInstance().getUimanagement().countdownview = findViewById(R.id.countdownview);

        activity = Controller.getInstance().mainActivity;

        int id;
        switch (Controller.getInstance().devType) {
            case 1:
                id = R.drawable.m11;
                break;
            case 2:
                id = R.drawable.m22;
                break;
            case 3:
                id = R.drawable.m33;
                break;
            case 4:
                id = R.drawable.m44;
                break;
                default:
                    id = R.drawable.m11;
                    break;

        }
        findViewById(R.id.tev_logo).setBackgroundResource(id);

        findViewById(R.id.tev_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.tev_m1_power).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showToast("关机");
                BluetoothGattService service = activity.getGattService(SIMPLEPROFILE_SERV_UUID);
                if(service != null) {
                    write(service, SIMPLEPROFILE_CHAR7_UUID, "0");
                }
            }
        });

        findViewById(R.id.tev_m1_Qt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showToast( "开启Q弹模式");
                BluetoothGattService service = activity.getGattService(SIMPLEPROFILE_SERV_UUID);
                if(service != null) {
                    Controller.getInstance().getUimanagement().menuview.setType(3);
                    switch (Controller.getInstance().devType) {
                        case 1:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"210");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"700");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"300");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        case 2:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"200");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"2000");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"320");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        case 3:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"200");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"2000");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"320");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        case 4:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"200");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"2000");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"320");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                            default:
                                write(service, SIMPLEPROFILE_CHAR1_UUID,"210");
                                write(service, SIMPLEPROFILE_CHAR2_UUID,"700");
                                write(service, SIMPLEPROFILE_CHAR7_UUID,"300");
                                write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                                break;
                    }
                    activity.read(service, SIMPLEPROFILE_CHAR7_UUID);
                }
            }
        });

        findViewById(R.id.tev_m1_dc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showToast("开启导出模式");
                BluetoothGattService service = activity.getGattService(SIMPLEPROFILE_SERV_UUID);
                if(service != null) {
                    Controller.getInstance().getUimanagement().menuview.setType(1);
                    switch (Controller.getInstance().devType) {
                        case 1:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"168");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"700");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"360");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        case 2:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"160");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"2000");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"420");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        case 3:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"160");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"2000");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"420");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        case 4:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"160");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"2000");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"420");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        default:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"168");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"700");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"360");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                    }
                    activity.read(service, SIMPLEPROFILE_CHAR7_UUID);
                }
            }
        });

        findViewById(R.id.tev_m1_dr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showToast( "开启导入模式");
                BluetoothGattService service = activity.getGattService(SIMPLEPROFILE_SERV_UUID);
                if(service != null) {
                    Controller.getInstance().getUimanagement().menuview.setType(2);
                    switch (Controller.getInstance().devType) {
                        case 1:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"105");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"700");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"600");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        case 2:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"100");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"2000");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"600");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        case 3:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"100");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"2000");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"600");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                        case 4:
                            write(service, SIMPLEPROFILE_CHAR1_UUID,"100");
                            write(service, SIMPLEPROFILE_CHAR2_UUID,"2000");
                            write(service, SIMPLEPROFILE_CHAR7_UUID,"600");
                            write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                            break;
                            default:
                                write(service, SIMPLEPROFILE_CHAR1_UUID,"105");
                                write(service, SIMPLEPROFILE_CHAR2_UUID,"700");
                                write(service, SIMPLEPROFILE_CHAR7_UUID,"600");
                                write(service, SIMPLEPROFILE_CHAR8_UUID,"34");
                                break;
                    }
                    activity.read(service, SIMPLEPROFILE_CHAR7_UUID);
                }
            }
        });
    }

    private void write(BluetoothGattService service, UUID uuid, String str) {
        activity.write(service, uuid, str);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity.closeNotif();
        activity.closeConn();
    }
}
