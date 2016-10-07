package libro.ejemplos;

import android.app.ProgressDialog;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Ejemplo21 extends AppCompatActivity {
    WebView mResultado = null;
    ProgressDialog mBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo21);

        mResultado = (WebView) findViewById(R.id.e21_webview);

        final EditText editurl = (EditText) findViewById(R.id.e21_edit_url);

        mBar = new ProgressDialog(this);
        mBar.setIndeterminate(true);
        mBar.setTitle(getString(R.string.e21_descargando));

        Button descargar = (Button) findViewById(R.id.e21_descargar);
        descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = editurl.getEditableText().toString();
                mBar.setMessage(url);
                mBar.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        descargaFTP(url, "UTF8");
                    }
                }).start();
            }
        });
    }

    public void descargaFTP(String destino, final String codificacion) {
        InputStream is;
        byte[] datos = null;
        try {
            URL url = new URL(destino);

            URLConnection urlConnection = url.openConnection();
            is = urlConnection.getInputStream();
            datos = readStream(is, codificacion);
            if (datos != null){
                final String contenido = new String(datos);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBar.dismiss();
                        mResultado.loadData(contenido,"text/html", codificacion);
                    }
                });
            }
            is.close();
        } catch (MalformedURLException exurl) {
            exurl.printStackTrace();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
        }
    }

    public byte[] readStream(InputStream is, final String codificacion) {
        byte[] datos = new byte[1024];

        BufferedReader in
                = null;
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

            while (is.available() > 0) {
                is.read(datos);
                System.out.println(new String(datos));
                baos.write(datos);
            }
            return baos.toByteArray();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
        return null;
    }
}
