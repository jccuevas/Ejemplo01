package libro.ejemplos;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Ejemplo24 extends AppCompatActivity {

    public static final String URL_SCHEMA = "http";
    public static final String URL_CHARSET = "UTF-8";//Para las URL debe ser este juego de caracteres
    public static final String DEBUG_TAG = "Debug-Ejemplo24";
    public static final String SERVER = "192.168.1.162/wwwLibro/login.php";
    public static final String CRLF = "\r\n";
    public int mTotal = 0;

    WebView mResultado = null;
    ProgressDialog mBar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo24);

        mResultado = (WebView) findViewById(R.id.e24_webview);

        final EditText editUser = (EditText) findViewById(R.id.e24_edit_user);
        final EditText editPass = (EditText) findViewById(R.id.e24_edit_pass);


        mBar = new ProgressDialog(this);
        mBar.setIndeterminate(false);
        mBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        mBar.setTitle(getString(R.string.e24_descargando));

        Button login = (Button) findViewById(R.id.e24_descargar);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                final String user = URLEncoder.encode(editUser.getEditableText().toString(), URL_CHARSET);
                final String pass = URLEncoder.encode(editPass.getEditableText().toString(), URL_CHARSET);

                    final String urlFinal = URL_SCHEMA + "://" + SERVER;

                    mBar.setMessage(urlFinal);
                    mBar.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                autenticaPost(urlFinal, user, pass);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * @param direccion URL del recurso para el login
     * @param user      identificador de usuario
     * @param pass      clave del usuario
     * @throws IOException
     */
    private void autenticaPost(String direccion, String user, String pass) throws IOException {
        InputStream is = null;
        DataOutputStream dos = null;
        URL url = new URL(direccion);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setReadTimeout(10000 /* milisegundos */);
            conn.setConnectTimeout(15000 /* milisegundos */);
            //Se establece POST como método de envío
            conn.setRequestMethod("POST");
            //Se activa el envío de datos en el cuerpo de la petición
            conn.setDoOutput(true);
            //Se activa también la lectura del cuerpo de la respuesta
            conn.setDoInput(true);


            //Se escribe la petición, se ha puesto fraccionada para que se vea
            //el formato <parametro>=<valor> y & para añadir otro
            OutputStream os = conn.getOutputStream();
            BufferedWriter wr = new BufferedWriter(
                    new OutputStreamWriter(os, URL_CHARSET));

            wr.write("user=" + user);
            wr.write("&");
            wr.write("pass=" + pass);
            wr.flush();
            wr.close ();
            conn.connect();

            //Se lee la respuesta
            int response = conn.getResponseCode();
            final int len = conn.getHeaderFieldInt("CONTENT-LENGTH", 1024);
            final String contentType = conn.getHeaderField("CONTENT-TYPE");
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBar.setMax(len);
                }
            });


            final String datos = new String(readStream(is));
            if (datos != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBar.dismiss();
                        mResultado.loadData(datos, contentType, "iso_8859-15");
                    }
                });
            }
            is.close();
        } finally {
            conn.disconnect();
        }
    }


    public byte[] readStream(InputStream is) {
        byte[] datos = new byte[1024];
        int leidos = 0;

        mTotal = 0;
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            do {
                leidos = is.read(datos);

                if (leidos > 0) {
                    baos.write(datos);
                    mTotal = mTotal + leidos;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBar.setProgress(mTotal);
                        }
                    });
                }
            } while (leidos > 0);
            return baos.toByteArray();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
        return null;
    }
}
