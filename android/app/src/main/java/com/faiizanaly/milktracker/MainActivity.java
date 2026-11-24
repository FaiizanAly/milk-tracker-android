package com.faiizanaly.milktracker;

import android.content.ContentValues;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.getcapacitor.BridgeActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = getBridge().getWebView();
        if (webView != null) {
            WebSettings settings = webView.getSettings();
            settings.setDomStorageEnabled(true);
            settings.setAllowFileAccess(true);
            settings.setDatabaseEnabled(true);

            // Add JavaScript Interface for direct Blob/Base64 saving
            webView.addJavascriptInterface(new DownloadBridge(), "AndroidDownloader");

            // Attach DownloadListener for data: and blob: downloads
            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    if (url.startsWith("data:")) {
                        try {
                            int commaIndex = url.indexOf(",");
                            if (commaIndex != -1) {
                                String base64Data = url.substring(commaIndex + 1);
                                String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
                                saveBytesToDownloads(filename, mimetype, Base64.decode(base64Data, Base64.DEFAULT));
                            }
                        } catch (Exception e) {
                            showToast("Download failed: " + e.getMessage());
                        }
                    } else if (url.startsWith("blob:")) {
                        String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
                        String js = "javascript:(function() {" +
                                "  fetch('" + url + "').then(function(r){ return r.blob(); }).then(function(blob){" +
                                "    var reader = new FileReader();" +
                                "    reader.onload = function() {" +
                                "      var b64 = reader.result.split(',')[1];" +
                                "      if (window.AndroidDownloader) {" +
                                "        window.AndroidDownloader.saveBase64File('" + filename + "', blob.type || '" + mimetype + "', b64);" +
                                "      }" +
                                "    };" +
                                "    reader.readAsDataURL(blob);" +
                                "  }).catch(function(err){ console.error(err); });" +
                                "})()";
                        webView.post(() -> webView.evaluateJavascript(js, null));
                    }
                }
            });
        }
    }

    public class DownloadBridge {
        @JavascriptInterface
        public void saveBase64File(String filename, String mimeType, String base64Data) {
            try {
                byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
                saveBytesToDownloads(filename, mimeType, bytes);
            } catch (Exception e) {
                showToast("Failed to save " + filename + ": " + e.getMessage());
            }
        }
    }

    public boolean saveBytesToDownloads(String filename, String mimeType, byte[] bytes) {
        boolean success = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                values.put(MediaStore.MediaColumns.MIME_TYPE, (mimeType != null && !mimeType.isEmpty()) ? mimeType : "application/octet-stream");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                        if (os != null) {
                            os.write(bytes);
                            os.flush();
                            success = true;
                        }
                    }
                }
            } else {
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, filename);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(bytes);
                    fos.flush();
                    success = true;
                }
                MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, new String[]{mimeType}, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error saving file: " + e.getMessage());
            return false;
        }

        if (success) {
            showToast("Backup saved to Downloads: " + filename);
        }
        return success;
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
    }
}

