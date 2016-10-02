package libro.ejemplo01;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class ServicioVinculadoConectar extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public Socket conectar(InetSocketAddress direccion){
        String respuesta ="";
        Socket cliente=null;
        try {
            //Se crea el socket TCP
            cliente = new Socket();

            //Se realiza la conexión al servidor
            cliente.connect(direccion);


            BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            OutputStream os = cliente.getOutputStream();
            //Se leen el mensaje del servidor
            respuesta = bis.readLine();

            bis.close();
            os.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            respuesta = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            respuesta = "IOException: " + e.toString();
        }

        return cliente;
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
            respuesta = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            respuesta = "IOException: " + e.toString();
        }

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        ServicioVinculadoConectar getService() {
            // Return this instance of ServicioVinculadoConectar so clients can call public methods
            return ServicioVinculadoConectar.this;
        }
    }
}