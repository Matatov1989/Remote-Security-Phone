package com.sergeant_matatov.remotesecurityphone;

import android.content.Context;

/**
 * Created by Yurka on 14.12.2016.
 */

public interface ISendSMS {
    void sendSMS(Context context, String phoneNum, String msg);
}
