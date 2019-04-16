package net.lzzy.practicesonline.activities.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragments.SplashFragment;
import net.lzzy.practicesonline.activities.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.activities.utils.AppUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author lzzy_Colo
 * @date 2019/4/10
 * Description:
 */
public class SplashActivity extends BaseActivity implements SplashFragment.OnSplashFinishedListener {
    private SplashHandler handler = new SplashHandler(this);
    private int seconds = 10;
    private TextView tvDisplay;
    private TextView tvCount;
    public static final int WHAT_COUNTING = 0;
    public static final int WHAT_EXCEPTION = 1;
    public static final int WHAT_COUNT_DONT = 2;
    public static final int WHAT_SERVER_OFF = 3;
    private boolean isServerOn = true;

    private static class SplashHandler extends AbstractStaticHandler<SplashActivity> {


        protected SplashHandler(SplashActivity context) {
            super(context);
        }


        @Override
        public void handleMessage(Message msg, SplashActivity activity) {
            switch (msg.what) {
                case WHAT_COUNTING:
                    String text = msg.obj.toString() + "秒";
                    activity.tvDisplay.setText(text);
                    break;
                case WHAT_COUNT_DONT:
                    if (activity.isServerOn) {
                        activity.gotoMain();
                    }
                    break;
                case WHAT_EXCEPTION:
                    new AlertDialog.Builder(activity)
                            .setMessage(msg.obj.toString())
                            .setPositiveButton("继续", (dialog, which) -> activity.gotoMain())
                            .setNegativeButton("退出", (dialog, which) -> AppUtils.exit()).show();
                    break;
                case WHAT_SERVER_OFF:
                    Activity context = AppUtils.getRunningActivity();
                    new AlertDialog.Builder(Objects.requireNonNull(context))
                            .setMessage("服务器没有响应，是否继续？\n" + msg.obj)
                            .setPositiveButton("确定", (dialog, which) -> {
                                if (context instanceof SplashActivity) {
                                    ((SplashActivity) context).gotoMain();
                                }
                            })
                            .setNegativeButton("退出", (dialog, which) -> AppUtils.exit())
                            .setNeutralButton("设置", (dialog, which) -> {
                            }).show();
                    break;
                default:
                    break;

            }
        }

        @Override
        public void sendMessag(Object o) {

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvCount = findViewById(R.id.activity_splash_tv_count_down);
        if (!AppUtils.isNetworkAvailable()) {
            new AlertDialog.Builder(this)
                    .setMessage("网络不可用是否继续")
                    .setPositiveButton("退出", (dialog, which) -> AppUtils.exit())
                    .setPositiveButton("确定", (dialog, which) -> gotoMain())
                    .show();
        } else {
            ThreadPoolExecutor executor = AppUtils.getExectuor();
            executor.execute(this::countDown);
            executor.execute(this::detectServerStatus);

        }

    }


    private void countDown() {
        while (seconds >= 0) {
            handler.sendMessag(handler.obtainMessage(WHAT_COUNTING, seconds));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                handler.sendMessag(handler.obtainMessage(WHAT_EXCEPTION, e.getMessage()));

            }
            seconds--;
        }
        handler.sendEmptyMessage(WHAT_COUNT_DONT);
    }

    private void detectServerStatus() {
        try {
            AppUtils.tryConnectServer("http://10.88.91.102:8888");
        } catch (IOException e) {
            isServerOn = false;
            handler.sendMessag(handler.obtainMessage(WHAT_SERVER_OFF, e.getMessage()));
        }
    }

    public void gotoMain() {

    }

    @Override
    public void cancelCount() {
        seconds = 0;

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected int getContainerId() {
        return R.id.fragment_splash_container;
    }

    @Override
    protected Fragment createFragment() {
        return new SplashFragment();
    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setMessage("要退出吗")
                .setPositiveButton("确定", ((dialog, which) -> AppUtils.exit())).show();
    }*/


}
