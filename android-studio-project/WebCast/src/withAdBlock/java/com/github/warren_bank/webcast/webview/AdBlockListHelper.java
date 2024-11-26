package com.github.warren_bank.webcast.webview;

import com.github.warren_bank.webcast.R;
import com.github.warren_bank.webcast.webview.AdBlockSettingsUtils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdBlockListHelper {

  private static final String FILENAME_PREFIX   = "adblock_";
  private static final String FILENAME_SUFFIX   = ".list";

  private static final long MILLIS_PER_DAY = 86400000L;

  private static String getFileName(Context context) {
    long now_millis    = System.currentTimeMillis();
    long now_days      = now_millis / MILLIS_PER_DAY;
    int  interval_days = AdBlockSettingsUtils.getCustomAdBlockListUpdateIntervalDays(context);
    long now_intervals = now_days / interval_days;

    return String.format("%s%d%s", FILENAME_PREFIX, now_intervals, FILENAME_SUFFIX);
  }

  private static File getFile(Context context) {
    String fileName = getFileName(context);
    File file = new File(context.getCacheDir(), fileName);
    return file;
  }

  private static void update(Context context) throws Exception {
    File file = getFile(context);
    if (file.exists()) return;

    deleteAll(context);

    String url = AdBlockSettingsUtils.getCustomAdBlockListUrl(context);
    if ((url == null) || url.isEmpty()) return;

    byte[] data = downloadBytes(url);
    if ((data == null) || (data.length == 0)) return;

    FileOutputStream fos = new FileOutputStream(file);
    fos.write(data);
    fos.flush();
    fos.close();
  }

  private static byte[] downloadBytes(String _url) {
    byte[] buffer = null;
    HttpURLConnection httpConn = null;
    try {
      URL url = new URL(_url);
      httpConn = (HttpURLConnection) url.openConnection();

      httpConn.setRequestMethod("GET");
      httpConn.setDoOutput(false);
      httpConn.setDoInput(true);
      httpConn.setUseCaches(false);
      httpConn.setReadTimeout(5000);

      httpConn.connect();
      if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new Exception();
      }

      InputStream inputStream = httpConn.getInputStream();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      int bytesRead;
      byte[] tempBuffer = new byte[4096];

      while (true) {
        bytesRead = inputStream.read(tempBuffer);
        if (bytesRead == -1) {
          break;
        }
        byteArrayOutputStream.write(tempBuffer, 0, bytesRead);
      }

      buffer = byteArrayOutputStream.toByteArray();
      inputStream.close();
    }
    catch (Exception ignored) {}
    finally {
      try {
        if (httpConn != null) {
          httpConn.disconnect();
        }
      }
      catch (Exception ignored) {}
    }

    return buffer;
  }

  private static void deleteAll(Context context) {
    File[] downloads = null;

    try {
      downloads = context.getCacheDir().listFiles(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return (name.startsWith(FILENAME_PREFIX) && name.endsWith(FILENAME_SUFFIX));
        }
      });
    }
    catch(Exception e) {
      downloads = null;
    }

    if (downloads != null) {
      for (File download : downloads) {
        try {
          download.delete();
        }
        catch(Exception e) {}
      }
    }
  }

  public static void delete(Context context) {
    try {
      File file = getFile(context);

      if (file.exists()) {
        file.delete();
      }
    }
    catch(Exception e) {}
  }

  public static InputStream open(Context context) {
    InputStream in = null;

    try {
      File file = getFile(context);

      if (!file.exists())
        update(context);

      if (!file.exists())
        throw new Exception();

      in = new FileInputStream(file);
    }
    catch(Exception e) {
      in = null;
    }

    if (in == null) {
      // fallback
      in = context.getResources().openRawResource(R.raw.adblock_serverlist);
    }

    return in;
  }

}
