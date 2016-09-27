package libro.ejemplo01;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServicioPrimerPlano extends Service {
    public static final String EXTRA_IP = "servicio_ip";
    public static final String EXTRA_PORT = "servicio_puerto";
    protected String mIp = "192.158.1.157";
    protected int mPuerto = 6000;
    private NotificationManager mNM;
    private int mId = 1;

    public ServicioPrimerPlano() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mId=startId; //Se guarda el identificador para poder parar el servicio
        // si fuera necesario

        if (intent != null) {
            if (intent.hasExtra(EXTRA_IP))
                mIp = intent.getStringExtra(EXTRA_IP);

            mPuerto = intent.getIntExtra(EXTRA_PORT, mPuerto);
            InetSocketAddress direccion = new InetSocketAddress(mIp, mPuerto);

            // El PendingIntent a lanzar si el usuario pulsa en la notificación
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);

            NotificationCompat.Builder notificacion =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_stat_communication)
                            .setContentTitle(getText(R.string.servicio_etiqueta))
                            .setContentText("Conectando con " + mIp + ":" + mPuerto)// the contents of the entry
                            .setContentIntent(contentIntent);

            startForeground(mId, notificacion.build());

            new Thread(new HebraConectarSimple(direccion)).start();
        }
        return START_STICKY;
    }

    private void mostrarNotificacion(String mensaje) {

        // Si se quiere que se inicie la actividad cuando se toque en
        // la notificación se crea un PendingIntent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Parando el servicio",Toast.LENGTH_LONG).show();
        mNM.cancelAll();
    }

    /**
     * HebraConectarSimple
     * Esta clase permite la conexión con un servidor TCP y la recepcion de la respuesta del mismo
     */
    public class HebraConectarSimple implements Runnable {

        String mResponse = "";
        String mRespuesta = "";
        private InetSocketAddress mIp = null;

        public HebraConectarSimple(InetSocketAddress ip) {
            mIp = ip;
        }

        @Override
        public synchronized void run() {
            Socket cliente;
            try {
                //Se crea el socket TCP
                cliente = new Socket();
                mostrarNotificacion("Conectando con " + mIp + ":" + mPuerto);
                //Se realiza la conexión al servidor
                cliente.connect(mIp);
                mostrarNotificacion("Conexión correcta, autenticando...");
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                OutputStream os = cliente.getOutputStream();
                mRespuesta = bis.readLine();

                os.write(new String("USER USER\r\n").getBytes());
                os.flush();
                mRespuesta = bis.readLine();

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
                mostrarNotificacion(mRespuesta);
                for (int n = 0; n < 10; n++) {
                    os.write(new String("ECHO " + n + "\r\n").getBytes());
                    os.flush();
                    mRespuesta = bis.readLine();
                    mostrarNotificacion(mRespuesta);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                os.write(new String("QUIT\r\n").getBytes());
                os.flush();
                mRespuesta = bis.readLine();
                mostrarNotificacion(mRespuesta);
                bis.close();
                os.close();
                cliente.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                mRespuesta = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                mRespuesta = "IOException: " + e.toString();
            } finally {
                stopForeground(true);
                stopSelf(mId);//Una vez la hebra termina, se detiene el servicio
            }

        }
    }

}
