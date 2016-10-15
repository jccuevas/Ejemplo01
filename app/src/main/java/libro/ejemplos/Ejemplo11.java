package libro.ejemplos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Ejemplo11 extends AppCompatActivity {
    TextView mResponse = null;
    private String mIp = "192.168.1.155";
    private int mPuerto = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo11);

        mResponse = (TextView) findViewById(R.id.e11_response);
    }

    public void onConnect(View view) {
        InetSocketAddress direccion = new InetSocketAddress(mIp, mPuerto);
        new Thread(new HebraConectarSimple(direccion)).start();
    }

    public class HebraConectarSimple implements Runnable {

        String mRespuesta = "";
        private InetSocketAddress mIp = null;

        public HebraConectarSimple(InetSocketAddress ip) {
            mIp = ip;
        }

        @Override
        public void run() {
            Socket cliente;
            try {
                //Se crea el socket TCP
                cliente = new Socket();
                //Se realiza la conexión al servidor
                cliente.connect(mIp);
                //Se leen los datos del buffer de entrada
                BufferedReader bis =
                        new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                OutputStream os = cliente.getOutputStream();
                mRespuesta = bis.readLine();
                mResponse.post(new Runnable() {
                    //mResponse es un atributo de la actividad
                    // vinculado a un campo de texto
                    // por lo que se debe modificar su contenido
                    // a través de un post()
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });

                os.write(new String("QUIT\r\n").getBytes());
                os.flush();
                mRespuesta = bis.readLine();
                mResponse.post(new Runnable() {
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });
                bis.close();
                os.close();
                cliente.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                mRespuesta = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                mRespuesta = "IOException: " + e.toString();
            }

            mResponse.post(new Runnable() {
                @Override
                public void run() {
                    mResponse.setText(mRespuesta);
                }
            });
        }
    }
}
