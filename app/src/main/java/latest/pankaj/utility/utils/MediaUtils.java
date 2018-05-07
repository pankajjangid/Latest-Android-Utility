package latest.pankaj.utility.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by Pankaj on 25/10/2017.
 */

public class MediaUtils {

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_VIDEO = "video";
    private static final String TAG = "MediaUtils";

    /**
     * *
     * Get runtime duration of media such as audio or video in milliseconds
     * **
     */
    public static long getDuration(Context ctx, Uri mediaUri) {
        Cursor cur = ctx.getContentResolver().query(mediaUri, new String[]{MediaStore.Video.Media.DURATION}, null, null, null);
        long duration = -1;

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    duration = cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DURATION));

                    if (duration == 0)
                        Log.d(TAG, " The image size was found to be 0. Reason: UNKNOWN");

                }    // end while
            } else if (cur.getCount() == 0) {
                Log.d(TAG, " cur size is 0. File may not exist");
            } else {
                Log.d(TAG, " cur is null");
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return duration;
    }


    /**
     * Checks if the parameter {@link Uri} is a Media content uri.
     * **
     */
    public static boolean isMediaContentUri(Uri uri) {
        if (!uri.toString().contains("content://media/")) {
            Log.w(TAG, "#isContentUri The uri is not a media content uri");
            return false;
        } else
            return true;
    }

    /**
     * Gets the size of the media resource pointed to by the paramter mediaUri.
     * <p/>
     * Known bug: for unknown reason, the image size for some images was found to be 0
     *
     * @param mediaUri uri to the media resource. For e.g. content://media/external/images/media/45490 or
     *                 content://media/external/video/media/45490
     * @return Size in bytes, -1 if error
     * **
     */
    public static long getMediaSize(Context ctx, Uri mediaUri) {

        long size = -1;
        if (!MediaUtils.isMediaContentUri(mediaUri)) {
            Log.d(TAG, " Not a valid content uri");
            return size;
        }

        String columnName = MediaStore.MediaColumns.DATA;
        Cursor cur = ctx.getContentResolver().query(mediaUri, new String[]{columnName}, null, null, null);

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String path = cur.getString(cur.getColumnIndex(columnName));
                    File f = new File(path);

                    size = f.length();

                    if (size == 0) {
                        Log.d(TAG, "  The media size was found to be 0. Reason: UNKNOWN");
                    }
                } // end while
            } else if (cur.getCount() == 0) {
                Log.d(TAG, " cur size is 0. File may not exist");
            } else {
                Log.d(TAG, "  cur is null");
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return size;
    }

    /**
     * Gets media file name.
     */
    public static String getFileName(Context ctx, Uri mediaUri) {
        // TODO: move to MediaUtils
        String colName = MediaStore.MediaColumns.DISPLAY_NAME;
        Cursor cur = ctx.getContentResolver().query(mediaUri, new String[]{colName}, null, null, null);
        String dispName = null;

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    dispName = cur.getString(cur.getColumnIndex(colName));

                    if (TextUtils.isEmpty(colName)) {
                        Log.d(TAG, "  The file name is blank or null. Reason: UNKNOWN");
                    }

                } // end while
            } else if (cur != null && cur.getCount() == 0) {
                Log.d(TAG, "  File may not exist");
            } else {
                Log.d(TAG, "  cur is null");
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return dispName;
    }

    /**
     * Gets media type from the Uri
     *
     * @return "video", "audio", "image" Returns null otherwise.
     * **
     */
    public static String getType(Uri uri) {
        if (uri == null) {
            throw new NullPointerException("Uri cannot be null");
        }

        String uriStr = uri.toString();

        if (uriStr.contains(TYPE_VIDEO)) {
            return TYPE_VIDEO;
        } else if (uriStr.contains(TYPE_AUDIO)) {
            return TYPE_AUDIO;
        } else if (uriStr.contains(TYPE_IMAGE)) {
            return TYPE_IMAGE;
        } else {
            return null;
        }
    }
}
