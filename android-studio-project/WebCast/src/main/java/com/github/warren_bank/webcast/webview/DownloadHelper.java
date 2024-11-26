package com.github.warren_bank.webcast.webview;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadHelper {

  public static byte[] downloadBytes(String _url) {
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
      buffer = readInputStreamBytes(inputStream);
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

  public static byte[] readInputStreamBytes(InputStream inputStream) {
    byte[] buffer = null;

    try {
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

    return buffer;
  }

  public static String readInputStreamString(InputStream inputStream) {
    return readInputStreamString(inputStream, "UTF-8");
  }

  public static String readInputStreamString(InputStream inputStream, String charsetName) {
    byte[] buffer = readInputStreamBytes(inputStream);

    try {
      if (buffer == null) throw new Exception();

      return new String(buffer, charsetName);
    }
    catch(Exception e) {
      return null;
    }
  }

}
