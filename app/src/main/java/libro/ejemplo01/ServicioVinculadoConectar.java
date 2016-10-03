package libro.ejemplo01;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this,"Cliente vinculado",Toast.LENGTH_LONG).show();
        return mBinder;
    }

    public void conectar(InetSocketAddress direccion){
        String respuesta ="";
        Socket cliente=null;
        Toast.makeText(this,"Conectando con "+direccion.toString(),Toast.LENGTH_LONG).show();
//        try {
//            //Se crea el socket TCP
//            cliente = new Socket();
//
//            //Se realiza la conexión al servidor
//            cliente.connect(direccion);
//            BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
//            OutputStream os = cliente.getOutputStream();
//            //Se leen el mensaje del servidor
//            respuesta = bis.readLine();
//
//            bis.close();
//            os.close();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            respuesta = "UnknownHostException: " + e.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            respuesta = "IOException: " + e.toString();
//        }

       // return cliente;
    }

    public void enviar(Socket cliente){
        String respuesta ="";
        try {

            //Se leen los datos del buffer de entrada
            BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            OutputStream os = cliente.getOutputStream();


            os.write(new String("USER USER\r\n").getBytes());
            os.flush();
            respuesta = bis.readLine();

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
            System.out.println(respuesta);
            for (int n = 0; n < 10; n++) {
                os.write(new String("ECHO " + n + "\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();
                System.out.println(respuesta);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            os.write(new String("QUIT\r\n").getBytes());
            os.flush();
            respuesta = bis.readLine();
            System.out.println(respuesta);
            bis.close();
            os.close();
            cliente.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e("SVinculadoConectar","UnknownHostException: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SVinculadoConectar","IOException: " + e.toString());
        }

    }

    /**
     * Clase que usa el cliente que se vincula al servicio porque se sabe que siempre
     * se ejecutará en el mismo proceso que sus clientes, por lo que no necesita comnicaciones
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

        Socket mCliente;
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

                bis.close();
                os.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                mRespuesta = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                mRespuesta = "IOException: " + e.toString();
            } finally {
                Toast.makeText(getApplicationContext(),"Parando",Toast.LENGTH_LONG).show();
            }

        }
    }
}