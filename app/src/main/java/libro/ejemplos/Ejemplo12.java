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

public class Ejemplo12 extends AppCompatActivity {

    TextView mResponse = null;
    private String mIp = "192.168.1.155";
    private int mPuerto = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo12);

        mResponse = (TextView) findViewById(R.id.e12_response);
    }

    public void onConnect(View view) {
        InetSocketAddress direccion = new InetSocketAddress(mIp, mPuerto);
        new Thread(new HebraConectarSimple2(direccion)).start();
    }

    /**
     * HebraConectarSimple
     * Esta clase permite la conexión con un servidor TCP y la recepcion de la respuesta del mismo
     */
    public class HebraConectarSimple2 implements Runnable {

        String mRespuesta = "";
        private InetSocketAddress mIp = null;

        public HebraConectarSimple2(InetSocketAddress ip) {
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
                BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                OutputStream os = cliente.getOutputStream();
                mRespuesta = bis.readLine();
                mResponse.post(new Runnable() {
                    //mResponse es un atributo de la actividad
                    // vinculado a un campo de texto
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });

                os.write(new String("USER USER\r\n").getBytes());
                os.flush();
                mRespuesta = bis.readLine();
                mResponse.post(new Runnable() {
                    //mResponse es un atributo de la actividad
                    // vinculado a un campo de texto
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });

                os.write(new String("PASS 12345\r\n").getBytes());
                os.flush();
                mRespuesta = bis.readLine();
                if (mRespuesta != null) {
                    if (mRespuesta.startsWith("OK"))
                        mRespuesta = "Autenticado correctamente";
                    else {
                        mRespuesta = "Error de autenticación";
                    }
                }
                mResponse.post(new Runnable() {
                    //mResponse es un atributo de la actividad
                    // vinculado a un campo de texto
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });

                for (int n = 0; n < 10; n++) {
                    os.write(new String("ECHO " + n + "\r\n").getBytes());
                    os.flush();
                    mRespuesta = bis.readLine();
                    mResponse.post(new Runnable() {
                        //mResponse es un atributo de la actividad
                        // vinculado a un campo de texto
                        @Override
                        public void run() {
                            mResponse.setText(mRespuesta);
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
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
