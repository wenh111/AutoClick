package com.wenh.autoclick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;


import com.wenh.autoclick.thread.Threads;

import java.io.DataOutputStream;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class ClickAccessibilityService extends AccessibilityService {
    private Timer timer;
    private int clickSpeed = 100;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        showFloatingView(); // 添加 UI
    }

    private WindowManager windowManager;
    private View floatingView;
    private int clickX = 0;
    private int clickY = 0;
    private int dayOfMothText = 1;
    private int hourText = 0;
    private int minText = 0;
    private boolean isStart;

    @SuppressLint("ClickableViewAccessibility")
    private void showFloatingView() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(this);
        floatingView = inflater.inflate(R.layout.floating_button_layout, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL  // 允许焦点
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;

        windowManager.addView(floatingView, params);
        ConstraintLayout constraintLayout = floatingView.findViewById(R.id.root_view);
        // 示例：添加按钮事件
        EditText speed = floatingView.findViewById(R.id.speed);
        EditText month = floatingView.findViewById(R.id.day_of_month);
        EditText hour = floatingView.findViewById(R.id.hour);
        EditText min = floatingView.findViewById(R.id.min);
        month.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    dayOfMothText = 0;
                } else {
                    try {
                        dayOfMothText = Integer.parseInt(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        hour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    hourText = 0;
                } else {
                    try {
                        hourText = Integer.parseInt(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        min.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    minText = 0;
                } else {
                    try {
                        minText = Integer.parseInt(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Button button = floatingView.findViewById(R.id.button);
        Button stopButton = floatingView.findViewById(R.id.button_stop);
        Button getLocalButton = floatingView.findViewById(R.id.get_local_button);
        getLocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                windowManager.updateViewLayout(floatingView, params);
            }
        });
        EditText eX = floatingView.findViewById(R.id.e_x);
        EditText eY = floatingView.findViewById(R.id.e_y);
        eX.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    clickX = 0;
                } else {
                    try {
                        clickX = Integer.parseInt(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        constraintLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                Log.d("Touch", "点击位置: (" + x + ", " + y + ")");
                eX.setText(String.valueOf(x));
                eY.setText(String.valueOf(y));
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                windowManager.updateViewLayout(floatingView, params);
            }
            return false;
        });
        eY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    clickY = 0;
                } else {
                    try {
                        clickY = Integer.parseInt(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                isStart = false;
            }
        });
        speed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    clickSpeed = 100;
                } else {
                    try {
                        clickSpeed = Integer.parseInt(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        button.setOnClickListener(v -> {
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManager.updateViewLayout(floatingView, params);
            isStart = true;
            Threads.back(new Runnable() {
                private boolean startClick;
                private long timeInMillis = System.currentTimeMillis();

                @Override
                public void run() {
                    while (isStart) {
                        if (clickSpeed < 50 && System.currentTimeMillis() - timeInMillis > 1000 * 60) {
                            isStart = false;
                        }
                        if (startClick) {
                            execTap(clickX, clickY); // 点击屏幕坐标
                            continue;
                        }
                        String nowFormat = TimeUtil.format(System.currentTimeMillis(), TimeUtil.YYYY_MM_DD_HH_MM_EN);
                        Log.d("ClickAccessibilityService", "现在的时间: " + nowFormat);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMothText);
                        calendar.set(Calendar.HOUR_OF_DAY, hourText);
                        calendar.set(Calendar.MINUTE, minText);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        timeInMillis = calendar.getTimeInMillis();
                        String format = TimeUtil.format(timeInMillis, TimeUtil.YYYY_MM_DD_HH_MM_EN);
                        Log.d("ClickAccessibilityService", "设定的时间: " + format);
                        long waitTime = timeInMillis - System.currentTimeMillis();
                        if (waitTime > 0) {
                            Threads.sleep(waitTime);
                        }

//                        while (timeInMillis > System.currentTimeMillis()) {}
//                        startClick = false;
                        clickAt(clickX, clickY); // 点击屏幕坐标 (800, 600)
                        System.out.println("开始时间:" + TimeUtil.format(System.currentTimeMillis(), "yyyy年MM月dd日 HH:mm:ss:SSS"));
                        startClick = true;
                    }
                }
            });
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                private boolean startClick;

                @Override
                public void run() {
                    if (startClick) {
                        clickAt(clickX, clickY); // 点击屏幕坐标 (800, 600)
                        return;
                    }
                    String nowFormat = TimeUtil.format(System.currentTimeMillis(), TimeUtil.YYYY_MM_DD_HH_MM_EN);
                    Log.d("ClickAccessibilityService", "现在的时间: " + nowFormat);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMothText);
                    calendar.set(Calendar.HOUR_OF_DAY, hourText);
                    calendar.set(Calendar.MINUTE, minText);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    long timeInMillis = calendar.getTimeInMillis();
                    String format = TimeUtil.format(timeInMillis, TimeUtil.YYYY_MM_DD_HH_MM_EN);
                    Log.d("ClickAccessibilityService", "设定的时间: " + format);
                    long waitTime = timeInMillis - System.currentTimeMillis();
                    if (waitTime > 0) {
                        Threads.sleep(timeInMillis - System.currentTimeMillis()); // 等待到设定时间
                    }
//                    while (timeInMillis > System.currentTimeMillis()) {}
//                        startClick = false;

                    clickAt(clickX, clickY); // 点击屏幕坐标 (800, 600)
                    System.out.println("开始时间:" + TimeUtil.format(System.currentTimeMillis(), "yyyy年MM月dd日 HH:mm:ss:SSS"));
                    startClick = true;
                }
            }, 0, clickSpeed);
        });
    }

    public void execTap(int x, int y) {
        try {
            String cmd = "input tap " + x + " " + y;
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes(cmd + "\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
            Threads.sleep(clickSpeed); // 确保点击间隔
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int chooseX, chooseY;
    private int chooseSpeed = 100;
    private GestureDescription gesture;

    @SuppressLint("NewApi")
    public void clickAt(final int x, final int y) {
        if (x != chooseX || y != chooseY || chooseSpeed != clickSpeed) {
            Path path = new Path();
            path.moveTo(x, y);
            GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, clickSpeed);
            gesture = new GestureDescription.Builder().addStroke(stroke).build();
            chooseX = x;
            chooseY = y;
            chooseSpeed = clickSpeed;
        }

        if (gesture != null) {
            dispatchGesture(gesture, null, null);
        }
//        Threads.sleep(clickSpeed); // 确保点击间隔

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) {
            windowManager.removeView(floatingView);
        }
    }

    @Override
    public void onInterrupt() {
    }
}