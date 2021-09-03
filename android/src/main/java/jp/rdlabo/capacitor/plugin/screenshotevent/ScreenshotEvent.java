package jp.rdlabo.capacitor.plugin.screenshotevent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.Nullable;

public class ScreenshotEvent {

    private Context context;
    private Activity activity;

    private static final String EXTERNAL_CONTENT_URI_MATCHER = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    private static final String[] PROJECTION = new String[] {
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_ADDED
    };
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;

    ContentResolver contentResolver;
    ContentObserver contentObserver;

    String lastPath;

    interface EventListener {
        void onEvent(String event, String path);
    }

    @Nullable
    private EventListener eventListener;

    public ScreenshotEvent(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        lastPath = new String("");
    }

    public String echo(String value) {
        System.out.println("ScreenshotEvent echo: " + value);
        return value;
    }

    public void startWatchEvent() {
        System.out.println("ScreenshotEvent startWatchEvent plugin Start");

        contentResolver = context.getContentResolver();

        contentObserver =
            new ContentObserver(null) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    System.out.println("ScreenshotEvent onChange: " + selfChange + ", " + uri.toString());
                    if (uri.toString().startsWith(EXTERNAL_CONTENT_URI_MATCHER)) {
                        Cursor cursor = null;
                        try {
                            cursor = contentResolver.query(uri, PROJECTION, null, null, SORT_ORDER);
                            if (cursor != null && cursor.moveToFirst()) {
                                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                                long currentTime = System.currentTimeMillis() / 1000;

                                System.out.println(
                                    "ScreenshotEvent path: " + path + ", dateAdded: " + dateAdded + ", currentTime: " + currentTime
                                );
                                if (matchPath(path) && matchTime(currentTime, dateAdded)) {
                                    if (!lastPath.equals(path)) {
                                        lastPath = path;
                                        System.out.println("ScreenshotEvent event: " + path);
                                        eventListener.onEvent("userDidTakeScreenshot", path);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("ScreenshotEvent open cursor fail");
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                    super.onChange(selfChange, uri);
                }
            };
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver);
    }

    public void removeWatchEvent() {
        contentResolver.unregisterContentObserver(contentObserver);
    }

    private static boolean matchPath(String path) {
        return path.toLowerCase().contains("screenshot") || path.contains("截屏") || path.contains("截图");
    }

    private static boolean matchTime(long currentTime, long dateAdded) {
        return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
    }

    public void setEventListener(@Nullable EventListener eventListener) {
        this.eventListener = eventListener;
    }
}
