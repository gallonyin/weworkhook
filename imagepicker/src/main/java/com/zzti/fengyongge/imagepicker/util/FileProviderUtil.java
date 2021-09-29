package com.zzti.fengyongge.imagepicker.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Created by fengyongge on 2018/8/21
 */

public class FileProviderUtil {
    public static Uri getFileUri(Context context, File file, String authority) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, authority, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


}
