package libro.ejemplos;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServicioVinculadoConectar extends Service {
    // Binder que se entraga a los clientes
    private final IBinder mBinder = new SocketBinder();
    Socket mCliente = null;
    private Handler mHandler = null;

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "Cliente vinculado", Toast.LENGTH_LONG).show();
        return mBinder;
    }

    public void conectar(Handler handler, InetSocketAddress direccion) {

        mHandler = handler;
        Toast.makeText(this, "Conectando con " + direccion.toString(), Toast.LENGTH_LONG).show();

        new Thread(new HebraConectarSimple(direccion)).start();
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public boolean autenticar(String user, String pass) {
        String respuesta = "";
        try {

            if (!mCliente.isClosed()) {
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(mCliente.getInputStream()));
                OutputStream os = mCliente.getOutputStream();

                os.write(new String("USER " + user + "\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();

                os.write(new String("PASS " + pass + "\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();
                if (respuesta != null) {
                    if (respuesta.startsWith("OK"))
                        respuesta = "Autenticado correctamente";
                    else {
                        respuesta = "Error de autenticación";
                    }
                }
                System.out.println(respuesta);

                bis.close();
                os.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SVinculadoConectar", "IOException: " + e.toString());
        }
        return false;
    }

    public void iniciarRecepcion() {
        new Thread(new HebraRecibir()).start();
    }

    public boolean enviar(String datos) {
        String respuesta = "";
        try {

            if (mCliente.isClosed()) {
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(mCliente.getInputStream()));
                OutputStream os = mCliente.getOutputStream();

                os.write(new String(datos + "\r\n").getBytes());
                os.flush();

                respuesta = bis.readLine();

                System.out.println(respuesta);
                bis.close();
                os.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SVinculadoConectar", "IOException: " + e.toString());
        }
        return false;
    }

    protected void enviaRespuesta(String respuesta) {
        Message recibido = Message.obtain(mHandler, 2);
        Bundle datos = new Bundle();
        datos.putString("response", respuesta);
        recibido.setData(datos);
        recibido.sendToTarget();
    }

    /**
     * Clase que usa el cliente que se vincula al servicio porque se sabe que siempre
     * se ejecutará en el mismo proceso que sus clientes, por lo que no necesita comunicaciones
     * inter-proceso (IPC).
     */
    public class SocketBinder extends Binder {
        ServicioVinculadoConectar getService() {
            // Devuelve una instancia de ServicioVinculadoConectar para que los clientes puedan llamar
            // a sus métodos públicos.
            return ServicioVinculadoConectar.this;
        }
    }

    /**
     * HebraConectarSimple
     * Esta clase permite la conexión con un servidor TCP y la recepcion de la respuesta del mismo
     */
    public class HebraConectarSimple implements Runnable {

        String mResponse = "";
        String mRespuesta = "";
        private InetSocketAddress mDestino = null;

        public HebraConectarSimple(InetSocketAddress ip) {
            mDestino = ip;
        }

        @Override
        public void run() {
            try {
                //Se crea el socket TCP
                mCliente = new Socket();
                //Se realiza la conexión al servidor
                mCliente.connect(mDestino);
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(mCliente.getInputStream()));
                OutputStream os = mCliente.getOutputStream();
                mRespuesta = bis.readLine();
                enviaRespuesta(mRespuesta);
                iniciarRecepcion();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e("SVinculadoConectar", "UnknownHostException: " + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("SVinculadoConectar", "IOException: " + e.toString());
            }

        }


    }

    /**
     * HebraConectarSimple
     * Esta clase permite la conexión con un servidor TCP y la recepcion de la respuesta del mismo
     */
    public class HebraAutenticar implements Runnable {

        String mUser = "";
        String mPass = "";
        String mResponse = "";
        String mRespuesta = "";
        private InetSocketAddress mDestino = null;

        public HebraAutenticar(String user, String pass) {
            mUser = user;
            mPass = pass;
        }

        @Override
        public void run() {
            try {
                //Se crea el socket TCP
                mCliente = new Socket();
                //Se realiza la conexión al servidor
                mCliente.connect(mDestino);
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(mCliente.getInputStream()));
                OutputStream os = mCliente.getOutputStream();
                mRespuesta = bis.readLine();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e("SVinculadoConectar", "UnknownHostException: " + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("SVinculadoConectar", "IOException: " + e.toString());
            }

        }
    }

    /**
     * HebraConectarSimple
     * Esta clase permite la conexión con un servidor TCP y la recepcion de la respuesta del mismo
     */
    public class HebraRecibir implements Runnable {

        String mRespuesta = "";
        private InetSocketAddress mDestino = null;

        public HebraRecibir() {

        }

        @Override
        public void run() {
            try {
                if (mCliente != null)
                    while (!mCliente.isClosed()) {

                        //Se leen los datos del buffer de entrada en cuanto se reciban
                        BufferedReader bis = new BufferedReader(new InputStreamReader(mCliente.getInputStream()));
                        OutputStream os = mCliente.getOutputStream();
                        mRespuesta = bis.readLine();

                        enviaRespuesta(mRespuesta);
                    }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e("SVinculadoConectar", "UnknownHostException: " + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("SVinculadoConectar", "IOException: " + e.toString());
            }

        }
    }

}