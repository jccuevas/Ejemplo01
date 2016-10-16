package libro.ejemplos;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Ejemplo14 extends AppCompatActivity{

    private static Handler mHandler = null;
    TextView mResponse = null;
    private String mIp = NetworkInterface.mIp;
    private int mPuerto = NetworkInterface.mPuerto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo14);

        mResponse = (TextView) findViewById(R.id.e14_response);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message inputMessage) {
                String respuesta = "";
                // Obtiene el mensaje de la hebra de conexión.
                switch (inputMessage.what) {
                    case 1:
                        respuesta = inputMessage.getData().getString("response");
                        if (mResponse != null) {
                            mResponse.setText("Recibido: " + respuesta);
                            Log.d("Handler", "Recibido: " + respuesta);
                        }
                        break;
                }
            }

        };
    }

    public void onConnect(View view) {
        InetSocketAddress direccion = new InetSocketAddress(mIp, mPuerto);
        new Thread(new HebraConectar(direccion)).start();
    }

    /**
     * HebraConectar
     */
    public class HebraConectar implements Runnable {

        private InetSocketAddress mIp = null;

        public HebraConectar(InetSocketAddress ip) {
            mIp = ip;

        }

        protected void enviaRespuesta(String respuesta) {
            Message recibido = Message.obtain(mHandler, 1);
            Bundle datos = new Bundle();
            datos.putString("response", respuesta);
            recibido.setData(datos);
            recibido.sendToTarget();
        }

        @Override
        public void run() {
            String respuesta = "";
            Socket cliente;

            try {
                //Se crea el socket TCP
                cliente = new Socket();
                //Se realiza la conexión al servidor
                cliente.connect(mIp);
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                OutputStream os = cliente.getOutputStream();
                respuesta = bis.readLine();
                enviaRespuesta(respuesta);

                os.write(new String("USER USER\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();
                enviaRespuesta(respuesta);

                os.write(new String("PASS 12345\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();
                if (respuesta != null) {
                    if (respuesta.startsWith("OK"))
                        respuesta = "Autenticado correctamente";
                    else {
                        respuesta = "Error de autenticación";
                    }
                }
                enviaRespuesta(respuesta);

                for (int n = 0; n < 10; n++) {
                    os.write(new String("ECHO " + n + "\r\n").getBytes());
                    os.flush();
                    respuesta = bis.readLine();
                    enviaRespuesta(respuesta);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                os.write(new String("QUIT\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();
                enviaRespuesta(respuesta);
                bis.close();
                os.close();
                cliente.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                respuesta = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                respuesta = "IOException: " + e.toString();
            }
        }


    }
}
