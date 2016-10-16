package libro.ejemplos;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class IntentServiceConectar extends IntentService {
    public static final String EXTRA_IP = "ip";
    public static final String EXTRA_PORT = "puerto";

    private String mIp ="";
    private int mPuerto = 6000;
    private NotificationManager mNM;
    private int mId=0;

    public IntentServiceConectar() {
        super("IntentServiceConectar");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mId=startId;
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        if (intent != null) {
            Toast.makeText(this,getString(R.string.nuevo_servicio), Toast.LENGTH_SHORT).show();

            if (intent != null) {
                if (intent.hasExtra(EXTRA_IP)) {
                    mIp = intent.getStringExtra(EXTRA_IP);

                    mPuerto = intent.getIntExtra(EXTRA_PORT, mPuerto);
                    InetSocketAddress direccion = new InetSocketAddress(mIp, mPuerto);

                    mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    conectar(direccion);
                }
            }
        }
    }

    protected void conectar(InetSocketAddress direccion){
        String respuesta ="";
        Socket cliente;
        try {
            //Se crea el socket TCP
            cliente = new Socket();
            mostrarNotificacion("Conectando con " + mIp + ":" + mPuerto);
            //Se realiza la conexión al servidor
            cliente.connect(direccion);
            mostrarNotificacion("Conexión correcta, autenticando...");
            //Se leen los datos del buffer de entrada
            BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            OutputStream os = cliente.getOutputStream();
            respuesta = bis.readLine();

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
            mostrarNotificacion(respuesta);
            for (int n = 0; n < 10; n++) {
                os.write(new String("ECHO " + n + "\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();
                mostrarNotificacion(respuesta);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            os.write(new String("QUIT\r\n").getBytes());
            os.flush();
            respuesta = bis.readLine();
            mostrarNotificacion(respuesta);
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
    private void mostrarNotificacion(String mensaje) {

        // Si se quiere que se inicie la actividad cuando se toque en
        // la notificación se crea un PendingIntent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, EjemplosServicios.class), 0);

        // Preparar la información a mostrar en el panel de notificaciones.
        NotificationCompat.Builder notificacion =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_communication) // Icono a mostrar
                        .setContentTitle(getText(R.string.servicio_etiqueta)) //Titulo
                        .setContentText("Recibido: " + mensaje)// Contenido
                        .setContentIntent(contentIntent); //Intent a abrir

        // Enviar la notificación
        mNM.notify(mId, notificacion.build());
    }

}
