package net.lzzy.practicesonline.activities.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/28
 * Description:
 */
public class DetectWebService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public class DeteWebBinder extends Binder{


        public void detect(){}
    }
}
