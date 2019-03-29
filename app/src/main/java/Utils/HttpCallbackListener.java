package Utils;

import android.graphics.Bitmap;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
