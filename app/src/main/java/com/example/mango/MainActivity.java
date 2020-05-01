package com.example.mango;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    final static String s1 = "0000fff1-0000-1000-8000-00805f9b34fb";
    final static String s2 = "0000fff2-0000-1000-8000-00805f9b34fb";
    final static String s3 = "0000fff3-0000-1000-8000-00805f9b34fb";
    final static String s4 = "0000fff4-0000-1000-8000-00805f9b34fb";
    final static String s5 = "0000fff5-0000-1000-8000-00805f9b34fb";
    final static String s6 = "0000fff6-0000-1000-8000-00805f9b34fb";
    final static String s7 = "0000fff7-0000-1000-8000-00805f9b34fb";
    final static String s8 = "0000fff8-0000-1000-8000-00805f9b34fb";
    final static String s9 = "00002a25-0000-1000-8000-00805f9b34fb";
    public static final UUID SIMPLEPROFILE_SERV_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID SIMPLEPROFILE_DESC1_UUID= UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID SIMPLEPROFILE_CHAR9_UUID= UUID.fromString(s9);
    public static final UUID SIMPLEPROFILE_CHAR1_UUID= UUID.fromString(s1);
    public static final UUID SIMPLEPROFILE_CHAR2_UUID= UUID.fromString(s2);
    public static final UUID SIMPLEPROFILE_CHAR3_UUID= UUID.fromString(s3);
    public static final UUID SIMPLEPROFILE_CHAR4_UUID= UUID.fromString(s4);
    public static final UUID SIMPLEPROFILE_CHAR5_UUID= UUID.fromString(s5);
    public static final UUID SIMPLEPROFILE_CHAR6_UUID= UUID.fromString(s6);
    public static final UUID SIMPLEPROFILE_CHAR7_UUID= UUID.fromString(s7);
    public static final UUID SIMPLEPROFILE_CHAR8_UUID= UUID.fromString(s8);

    private UUID DEVNAME_SERV_UUID;

    private Uimanagement ui;
    private Controller controller;

    private TextView mTips;
    public String addr,name;
    public BleDevAdapter mBleDevAdapter;
    public BluetoothGatt mBluetoothGatt;
    private boolean isConnected = false;

    private boolean isSendToRead = true;
    private int timeout = 5;
    public String vername;
    private int dutyratio;
    private int cycle;
    private int temperature;
    private int electricity;
    private String devname;
    private int timing;
    private int maxtime;
    private int temperature_target;

    public void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Controller.getInstance().context, str, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // 扫描BLE
    public void reScan(Context context) {
        if (mBleDevAdapter.isScanning) {
            showToast( "正在扫描中。。。");
        } else
            mBleDevAdapter.reScan();
    }

    // BLE中心设备连接外围设备的数量有限(大概2~7个)，在建立新连接之前必须释放旧连接资源，否则容易出现连接错误133
    public void closeConn() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }
    // 与服务端连接的Callback
    public BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, final int status, int newState) {
            final BluetoothDevice dev = gatt.getDevice();
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                isConnected = true;
                gatt.discoverServices(); //启动服务发现
            } else {
                isConnected = false;
                closeConn();
            }

            showToast(String.format(status == 0 ? (newState == 2 ? "与[%s]连接成功，蓝牙验证中。。。" : "与[%s]连接断开") : ("与[%s]连接出错,错误码:" + status), dev));

            if(status != 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showmeeage("错误","连接出错,错误码:" + status);
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            boolean[] isR = new boolean[8];
            if (status == BluetoothGatt.GATT_SUCCESS) { //BLE服务发现成功
                // 遍历获取BLE服务Services/Characteristics/Descriptors的全部UUID
                for (BluetoothGattService service : gatt.getServices()) {
                    StringBuilder allUUIDs = new StringBuilder("UUIDs={\nS=" + service.getUuid().toString());
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        allUUIDs.append(",\nC=").append(characteristic.getUuid());
                        if(characteristic.getUuid().equals(SIMPLEPROFILE_CHAR9_UUID)) {
                            DEVNAME_SERV_UUID = service.getUuid();
                        }
                        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors())
                            allUUIDs.append(",\nD=").append(descriptor.getUuid());

                        try {
                            switch (characteristic.getUuid().toString()) {
                                case s1:
                                    isR[0] = true;
                                    break;
                                case s2:
                                    isR[1] = true;
                                    break;
                                case s3:
                                    isR[2] = true;
                                    break;
                                case s4:
                                    isR[3] = true;
                                    break;
                                case s5:
                                    isR[4] = true;
                                    break;
                                case s6:
                                    isR[5] = true;
                                    break;
                                case s7:
                                    isR[6] = true;
                                    break;
                                case s8:
                                    isR[7] = true;
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    allUUIDs.append("}");
                    //showToast("发现服务" + allUUIDs);
                }
            }

            boolean isT = true;
            for (int i = 0; i < 8; i++) {
                if(!isR[i]) {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showmeeage("错误", "没有发现服务 " + finalI);
                        }
                    });
                    isT = false;
                }
            }

            if(isT) {
                isEstart = true;
                try{
                    if(controller.scanActivity != null)
                        controller.scanActivity.finish();
                }catch (Exception e) {

                }
                readdata();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            UUID uuid = characteristic.getUuid();
            String str;
            if(uuid.equals(SIMPLEPROFILE_CHAR5_UUID)) {
                str = devname = new String(characteristic.getValue());
            } else if(uuid.equals(SIMPLEPROFILE_CHAR9_UUID)) {
                str = vername = new String(characteristic.getValue());
            } else {
                byte[] bd = characteristic.getValue();
                int iv;
                if(uuid.equals(SIMPLEPROFILE_CHAR8_UUID)) {
                    iv = bd[0] & 0xFF;
                    temperature_target = iv;
                }
                else
                    iv = (bd[0] & 0xFF) | (bd[1] << 8);

                str = iv + "";

                switch (uuid.toString()) {
                    case s1:
                        dutyratio = Integer.valueOf(iv);
                        break;
                    case s2:
                        cycle = Integer.valueOf(iv);
                        break;
                    case s3:
                        temperature = Integer.valueOf(iv);
                        temperature();
                        break;
                    case s4:
                        electricity = Integer.valueOf(iv);
                        electricity();
                        break;
                    case s6:
                        timing = Integer.valueOf(iv);
                        timing();
                        break;
                    case s7:
                        maxtime= Integer.valueOf(iv);
                        isTstart = true;
                        break;
                }
            }

            showToast("读取Characteristic[" + uuid + "]:\n" + str);
            isSendToRead = true;
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            UUID uuid = characteristic.getUuid();
            String valueStr = new String(characteristic.getValue());
            showToast("写入Characteristic[" + uuid + "]:\n" + valueStr);
            isSendToRead = true;
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();

            byte[] bd = characteristic.getValue();
            int iv = (bd[0] & 0xFF) | (bd[1] << 8);

            String valueStr = iv + "";
            showToast("通知Characteristic[" + uuid + "]:\n" + valueStr);

            switch (uuid.toString()) {
                case s3:
                    temperature = iv;
                    temperature();
                    break;
                case s4:
                    electricity = iv;
                    electricity();
                    break;
                case s6:
                    timing = iv;
                    timing();
                    break;
            }

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            UUID uuid = descriptor.getUuid();
            String valueStr = Arrays.toString(descriptor.getValue());
            showToast("读取Descriptor[" + uuid + "]:\n" + valueStr);
            isSendToRead = true;
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            UUID uuid = descriptor.getUuid();
            String valueStr = Arrays.toString(descriptor.getValue());
            showToast("写入Descriptor[" + uuid + "]:\n" + valueStr);
            isSendToRead = true;
        }
    };

    ArrayList<Icon> arrayList = new ArrayList();
    MyAdapter<Icon> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            String str = getFilesDir().getPath();
            File f = new File(str + "/data.ini");
            if(!f.exists()) {
                f.createNewFile();
            }
            str = readFile("data.ini");
            if(str != null && !str.isEmpty()) {
                String[] strings = str.split("\n");
                for (int i = 0; i < strings.length; i++) {
                    Icon icon = new Icon();
                    String[] strings1 = strings[i].split(",");
                    int itype = Integer.valueOf(strings1[0]);
                    switch (itype) {
                        case 1:
                            icon.setiId(R.drawable.m1);
                            icon.setiName("金·至尊");
                            break;
                        case 2:
                            icon.setiId(R.drawable.m2);
                            icon.setiName("紫·罗兰");
                            break;
                        case 3:
                            icon.setiId(R.drawable.m3);
                            icon.setiName("粉·の恋");
                            break;
                        case 4:
                            icon.setiId(R.drawable.m4);
                            icon.setiName("白·珍珠");
                            break;
                            default:
                                icon.setiId(R.drawable.m1);
                                icon.setiName("金·至尊");
                                break;
                    }
                    icon.setDevName(strings1[2]);
                    icon.setAddress(strings1[1]);
                    arrayList.add(icon);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        controller = Controller.getInstance();
        controller.setMainActivity(this);
        controller.methods(Controller.methods_Init);

        ui = controller.getUimanagement();

        findViewById(R.id.tev_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(50);
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    view.setBackgroundResource(R.drawable.ic_add_circle_black_24dp_t);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                isSendToRead = true;
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
            }
        });

        GridView gridView = findViewById(R.id.main_list);

        adapter = new MyAdapter<Icon>(arrayList, R.layout.item_grid_icon) {
            @Override
            public void bindView(ViewHolder holder, Icon obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
                String name = obj.getAddress() + "\n" + obj.getDevName();
                holder.setText(R.id.txt_name, name);
            }
        };
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSendToRead = true;
                closeConn();
                if (arrayList != null && arrayList.size() > 0) {
                    Icon icon = arrayList.get(position);
                    addr = icon.getAddress();
                    name = icon.getDevName();
                    BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addr);
                    mBluetoothGatt = device.connectGatt(MainActivity.this, false, mBluetoothGattCallback);
                    Toast.makeText(MainActivity.this, String.format("与[%s]开始连接............", icon.getAddress()), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "设备列表为空", Toast.LENGTH_SHORT).show();
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int cout = i;
                PopupMenu popup = new PopupMenu(Controller.getInstance().context, view);//第二个参数是绑定的那个view
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.mdelete, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                AlertDialog alertDialog = new AlertDialog.Builder(Controller.getInstance().context)
                                        .setTitle(Html.fromHtml("<font color='black'>"+ "提示" +"!</font>"))
                                        .setMessage(Html.fromHtml("<font color='black'>"+ "确定删除吗？" +"</font>"))
                                        .setIcon(R.drawable.ic_error_black_24dp)
                                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                arrayList.remove(cout);
                                                try {
                                                    witeFile(null);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                        })
                                        .setPositiveButton("取消", null)
                                        .create();
                                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.bgv);
                                alertDialog.show();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
                return true;
            }
        });

        setbule();
    }

    public void witeFile(String data) throws IOException {
        FileOutputStream fileOutputStream = openFileOutput("data.ini", MODE_PRIVATE);
        if(data != null) {
            fileOutputStream.write(data.getBytes());
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < arrayList.size(); i++) {
                Icon icon = arrayList.get(i);
                stringBuilder.append(icon.getItype() + "," + icon.getAddress() + "," + icon.getDevName() + "\n");
            }
            fileOutputStream.write(stringBuilder.toString().getBytes());
        }

        fileOutputStream.close();
    }
    public String readFile(String file) throws IOException {
        FileInputStream fileInputStream = openFileInput(file);
        int len = fileInputStream.available();
        byte[] bd = new byte[len];
        fileInputStream.read(bd);
        return new String(bd, "UTF-8");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Controller.getInstance().context = this;
    }

    boolean isTstart = false;
    private void timing() {
        final int time = maxtime - timing;
        if(time >= 0) {
            if(isTstart) {
                isTstart = false;
                ui.countdownview.setCout(maxtime);
            }

            ui.countdownview.setTime(timing);
            ui.tev_m1_time = controller.connectActivity.findViewById(R.id.tev_m1_time);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView)ui.tev_m1_time).setText(formatTimeS(time) + "");
                }
            });
        }
    }
    private String formatTimeS(long seconds) {
        int temp = 0;
        StringBuffer sb = new StringBuffer();
        if (seconds > 3600) {
            temp = (int) (seconds / 3600);
            sb.append((seconds / 3600) < 10 ? "0" + temp + ":" : temp + ":");
            temp = (int) (seconds % 3600 / 60);
            changeSeconds(seconds, temp, sb);
        } else {
            temp = (int) (seconds % 3600 / 60);
            changeSeconds(seconds, temp, sb);
        }
        return sb.toString();
    }
    private void changeSeconds(long seconds, int temp, StringBuffer sb) {
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");
        temp = (int) (seconds % 3600 % 60);
        sb.append((temp < 10) ? "0" + temp : "" + temp);
    }

    private void temperature() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int f = temperature / 10;
                ((TextView)controller.connectActivity.findViewById(R.id.tev_wd)).setText(f + "℃");
            }
        });
    }

    int ie = 0;
    int ies= 0;
    boolean isEstart = true;
    private void electricity() {
        if (ie == 3 || isEstart) {
            if(isEstart)
                isEstart = false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int f = ies / 4;
                    if(f > 100)
                        f = 100;
                    if(f < 0)
                        f = 0;
                    ((TextView)controller.connectActivity.findViewById(R.id.tev_dl)).setText(f + "%");
                    if(f > 20) {
                        if(f > 30) {
                            if(f > 50) {
                                if(f > 60) {
                                    if(f > 80) {
                                        if(f > 90) {
                                            (controller.connectActivity.findViewById(R.id.textView9)).setBackgroundResource(R.drawable.ic_battery_full_black_24dp);
                                        } else
                                            (controller.connectActivity.findViewById(R.id.textView9)).setBackgroundResource(R.drawable.ic_battery_90_black_24dp);
                                    } else
                                        (controller.connectActivity.findViewById(R.id.textView9)).setBackgroundResource(R.drawable.ic_battery_80_black_24dp);
                                } else
                                    (controller.connectActivity.findViewById(R.id.textView9)).setBackgroundResource(R.drawable.ic_battery_60_black_24dp);
                            } else
                                (controller.connectActivity.findViewById(R.id.textView9)).setBackgroundResource(R.drawable.ic_battery_50_black_24dp);
                        } else
                            (controller.connectActivity.findViewById(R.id.textView9)).setBackgroundResource(R.drawable.ic_battery_30_black_24dp);
                    } else
                        (controller.connectActivity.findViewById(R.id.textView9)).setBackgroundResource(R.drawable.ic_battery_20_black_24dp);

                    ie = 0;
                    ies = 0;
                }
            });
        } else {
            ies += (int) (1.67*(electricity) - 720);
            ie ++;
        }
    }

    public void readdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothGattService service = getGattService(DEVNAME_SERV_UUID);
                if(service != null) {
                    read(service, SIMPLEPROFILE_CHAR9_UUID);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showmeeage("错误","service DEVNAME_SERV_UUID = null");
                            } catch (Exception e) {

                            }
                        }
                    });
                }
                service = getGattService(SIMPLEPROFILE_SERV_UUID);
                if(service != null) {
                    boolean isR = read(service, SIMPLEPROFILE_CHAR7_UUID);
                    if(isR)
                        isR = notification(service, SIMPLEPROFILE_CHAR3_UUID, true);
                    else
                        return;
                    if(isR)
                        isR = notification(service, SIMPLEPROFILE_CHAR4_UUID, true);
                    else
                        return;
                    if(isR)
                        isR = notification(service, SIMPLEPROFILE_CHAR6_UUID, true);
                    else
                        return;
                    if(isR) {
                        int icout = 0;
                        boolean isadd = true;
                        for (int i = 0; i < arrayList.size(); i++) {
                            Icon icon = arrayList.get(i);
                            if(icon.getAddress().equals(addr)) {
                                icout = i;
                                isadd = false;
                                break;
                            }
                        }
                        if(isadd) {
                            if(devname == null) {
                                Controller.getInstance().devType = 1;
                                arrayList.add(new Icon(R.drawable.m1,"金·至尊", addr + "\n" + name));
                            } else if(devname.contains("Mango1.1")) {
                                Controller.getInstance().devType = 1;
                                arrayList.add(new Icon(R.drawable.m1,"金·至尊", addr + "\n" + name));
                            } else if(devname.contains("Mango1.2")) {
                                Controller.getInstance().devType = 2;
                                arrayList.add(new Icon(R.drawable.m2,"紫·罗兰", addr + "\n" + name));
                            } else if(devname.contains("Mango1.3")) {
                                Controller.getInstance().devType = 3;
                                arrayList.add(new Icon(R.drawable.m3,"粉·の恋", addr + "\n" + name));
                            } else if(devname.contains("Mango1.4")) {
                                Controller.getInstance().devType = 4;
                                arrayList.add(new Icon(R.drawable.m4,"白·珍珠", addr + "\n" + name));
                            }

                            try {
                                witeFile(null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } else
                            Controller.getInstance().devType = arrayList.get(icout).getItype();

                        startActivity(new Intent(MainActivity.this, ConnectActivity.class));
                    } else
                        return;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showmeeage("错误","service SIMPLEPROFILE_SERV_UUID = null");
                        }
                    });
                }
            }
        }).start();
    }
    public boolean write(BluetoothGattService service, UUID uuid, String str) {
        long start = System.currentTimeMillis();
        while (!isSendToRead) {
            if((System.currentTimeMillis() - start) / 1000 >= timeout) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showmeeage("警告", "写入超时");
                    }
                });
                return false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int id = Integer.valueOf(str);
        byte[] bd = new byte[2];
        bd[0] = (byte)id;
        bd[1] = (byte)(id >> 8);
        isSendToRead = false;
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);
        if(uuid.equals(SIMPLEPROFILE_CHAR8_UUID)) {
            characteristic.setValue(new byte[] { (byte)id });
        } else {
            characteristic.setValue(bd); //单次最多20个字节
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
        return true;
    }
    private boolean notification(BluetoothGattService service, UUID uuid, boolean open) {
        long start = System.currentTimeMillis();
        while (!isSendToRead) {
            if((System.currentTimeMillis() - start) / 1000 >= timeout) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showmeeage("警告", "写入描述超时");
                    }
                });
                return false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isSendToRead = false;
        // 设置Characteristic通知
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);//通过UUID获取可通知的Characteristic
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);

        // 向Characteristic的Descriptor属性写入通知开关，使蓝牙设备主动向手机发送数据
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(SIMPLEPROFILE_DESC1_UUID);
//             descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);//和通知类似,但服务端不主动发数据,只指示客户端读取数据
        descriptor.setValue(open ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
        return true;
    }
    public boolean read(BluetoothGattService service, UUID uuid) {
        long start = System.currentTimeMillis();
        while (!isSendToRead) {
            if((System.currentTimeMillis() - start) / 1000 >= timeout) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showmeeage("警告", "读取超时");
                    }
                });
                return false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isSendToRead = false;
        mBluetoothGatt.readCharacteristic(service.getCharacteristic(uuid));
        return true;
    }

    public void closeNotif() {
        BluetoothGattService service = getGattService(SIMPLEPROFILE_SERV_UUID);
        if(service != null) {
            notification(service, SIMPLEPROFILE_CHAR3_UUID, false);
            notification(service, SIMPLEPROFILE_CHAR4_UUID, false);
            notification(service, SIMPLEPROFILE_CHAR6_UUID, false);
        }
    }

    final int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if(grantResults.length > 0) {
                boolean isHavePermissions = true;
                for(int r : grantResults) {
                    if(r != PackageManager.PERMISSION_GRANTED) {
                        isHavePermissions = false;
                        break;
                    }
                }

                if(isHavePermissions) {
                    BluetoothAdapter ad = BluetoothAdapter.getDefaultAdapter();
                    if(!ad.isEnabled()) {
                        ad.enable();
                    }
                } else {
                    showmeeage("错误", "请允许所有权限！");
                }
            }
        }
    }

    private void setbule() {
        boolean isPermission = true;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            for(String str : PERMISSIONS_STORAGE) {
                if((ActivityCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED)) {
                    isPermission = false;
                    break;
                }
            }
        }

        if(isPermission) {
            BluetoothAdapter ad = BluetoothAdapter.getDefaultAdapter();
            if(!ad.isEnabled()) {
                ad.enable();
            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
        }

        // 检查是否支持BLE蓝牙
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showmeeage("警告", "本机不支持低功耗蓝牙！");
            finish();
            return;
        }
    }

    // 获取Gatt服务
    public BluetoothGattService getGattService(final UUID uuid) {
        if (!isConnected) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showmeeage("提示", "没有连接");
                }
            });

            return null;
        }
        BluetoothGattService service = mBluetoothGatt.getService(uuid);
        if (service == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        showmeeage("提示", "没有找到服务UUID=" + uuid);
                    } catch (Exception e) {

                    }
                }
            });
        }

        return service;
    }

    // 输出日志
    private void logTv(final String msg) {
        if (isDestroyed())
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mTips.getLineCount() > 3000)
                    mTips.setText("");
                mTips.append(msg + "\n\n");
            }
        });
    }

    private void showmeeage(String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(Controller.getInstance().context)
                .setTitle(Html.fromHtml("<font color='black'>"+ title +"!</font>"))
                .setMessage(Html.fromHtml("<font color='black'>"+ msg +"!</font>"))
                .setIcon(R.drawable.ic_error_black_24dp)
                .create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.bgv);
        alertDialog.show();
    }
}
