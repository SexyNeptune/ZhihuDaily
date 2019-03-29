package Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.zhihudailypurify.DownloadListener;
import com.example.zhihudailypurify.DownloadTask;

import java.util.ArrayList;
import java.util.List;

import db.Story;

public class DownloadService extends Service{

    private DownloadTask downloadTask;

    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onSuccess() {
            Toast.makeText(DownloadService.this, "下载成功", Toast.LENGTH_SHORT).show();
        }
    };

    public class DownloadBinder extends Binder{

        public void startDownload(List<Story> stories){
                downloadTask = new DownloadTask(listener,DownloadService.this);
                downloadTask.execute(stories);
        }
    }

    private DownloadBinder binder = new DownloadBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("DownloadService","启动=====================");
        return super.onStartCommand(intent, flags, startId);
    }
}
