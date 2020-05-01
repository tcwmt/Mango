package com.example.mango;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScanActivity extends AppCompatActivity {

    private boolean isScan = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Controller.getInstance().context = this;
        Controller.getInstance().setScanActivity(this);

        findViewById(R.id.tev_scan_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanActivity.this.finish();
            }
        });

        findViewById(R.id.tev_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ((TextView)view).setTextColor(Color.BLACK);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(50);
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView)view).setTextColor(Color.WHITE);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                scan();
            }
        });

        isScan = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scan();
                    }
                });
            }
        }).start();
    }

    private void scan() {
        if(Controller.getInstance().mainActivity.mBleDevAdapter == null || isScan) {
            RecyclerView rv = findViewById(R.id.rv_list);
            rv.setLayoutManager(new LinearLayoutManager(ScanActivity.this));
            Controller.getInstance().mainActivity.mBleDevAdapter = new BleDevAdapter(ScanActivity.this, new BleDevAdapter.Listener() {
                @Override
                public void onItemClick(final BluetoothDevice dev) {
                    ((TextView)findViewById(R.id.tev_connect)).setTextColor(Color.WHITE);
                    findViewById(R.id.connect).setBackground(getDrawable(R.drawable.ic_bluetooth_connected_black_24dp));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(50);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((TextView)findViewById(R.id.tev_connect)).setTextColor(Color.BLACK);
                                        findViewById(R.id.connect).setBackground(getDrawable(R.drawable.ic_bluetooth_connected_black_24dp_t));
                                    }
                                });

                                MainActivity activity = Controller.getInstance().mainActivity;
                                activity.addr = dev.getAddress();
                                activity.name = dev.getName();
                                activity.closeConn();

                                Controller.getInstance().mainActivity.mBluetoothGatt = dev.connectGatt(ScanActivity.this, false, activity.mBluetoothGattCallback); // 连接蓝牙设备
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    Toast.makeText(ScanActivity.this, String.format("与[%s]开始连接............", dev), Toast.LENGTH_SHORT).show();
                }
            });
            rv.setAdapter(Controller.getInstance().mainActivity.mBleDevAdapter);
            isScan = false;
        } else
            Controller.getInstance().mainActivity.reScan(this);
    }
}
