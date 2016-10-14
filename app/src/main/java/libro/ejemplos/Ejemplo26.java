package libro.ejemplos;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Ejemplo26 extends AppCompatActivity {
    private DownloadManager mDM;
    private BroadcastReceiver mReceiver = null;
    private long mIdDM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo26);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(mIdDM);
                    if (mDM != null) {
                        Cursor c = mDM.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c
                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c
                                    .getInt(columnIndex)) {
                                String uriString = c
                                        .getString(c
                                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                                startVideo("url al video");
                            }
                        }
                        unregisterReceiver(mReceiver);
                    }
                }
            }
        };

        registerReceiver(mReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));


    }

    protected void startVideo(String videoURI) {
        try {
            URL url = new URL(videoURI);
            String path = url.getFile();
            String fileName = Uri.decode(path);
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName);

            if (file.exists()) {//El fichero existe
                String videoUrl = "file://" + file.getPath(); // your URL here
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                intent.setDataAndType(Uri.parse(videoUrl), "video/mp4");
                startActivity(intent);
            } else {//El video no existe y se descargará
                mDM = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(videoURI));
                request.setTitle(getString(R.string.app_name) + " " + file.getPath());
                request.setDestinationUri(Uri.fromFile(file));
                mIdDM = mDM.enqueue(request);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}

