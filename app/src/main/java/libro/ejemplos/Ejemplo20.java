package libro.ejemplos;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetSocketAddress;
import java.net.Socket;

public class Ejemplo20 extends AppCompatActivity implements NetworkInterface{

    private static Handler mHandler = null;
    TextView mResponse = null;
    private Socket mSocket = null;
    private ServicioVinculadoConectar mServicio = null;
    private boolean mVinculado = false;
    /** Define las llamadas para a vinculación del servicio pasado en bindService() */
    private ServiceConnection mConexion = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // Para obtener la instancia al Binder del servicio basta con poner un casting
            ServicioVinculadoConectar.SocketBinder binder = (ServicioVinculadoConectar.SocketBinder) service;
            mServicio = binder.getService();
            mServicio.setHandler(mHandler);
            //           mServicio.iniciarRecepcion();
            //Se emplea para comprobar si el servicio está vinculado o no
            mVinculado = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mVinculado = false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo20);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message inputMessage) {
                mResponse = (TextView)findViewById(R.id.e20_response);
                String respuesta="";
                // Obtiene el mensaje de la hebra de conexión.
                switch (inputMessage.what) {
                    case 1:
                        respuesta = inputMessage.getData().getString("response");
                        if (mResponse != null) {
                            mResponse.setText("Recibido: "+respuesta);
                            Log.d("Handler","Recibido: "+respuesta);
                        }
                        //Toast.makeText(getApplicationContext(),respuesta,Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        respuesta = inputMessage.getData().getString("response");
                        if (mResponse != null) {
                            mResponse.setText("Recibido: "+respuesta);
                            Log.d("Handler","Recibido: "+respuesta);
                        }
                        Toast.makeText(getApplicationContext(),respuesta,Toast.LENGTH_SHORT).show();
                        break;
                }

            }

        };
    }

    public void onConnect(View view) {

        if (mVinculado) {
            //Se invoca el método conectar del servicio
            mServicio.conectar(mHandler,new InetSocketAddress(mIp, mPuerto));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Al iniciar la actividad se conecta al servicio
        Intent intent = new Intent(this, ServicioVinculadoConectar.class);
        bindService(intent, mConexion, BIND_AUTO_CREATE | BIND_ADJUST_WITH_ACTIVITY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Cuando la actividad va a ser parade se desvincula del servicio
        if (mVinculado) {
            unbindService(mConexion);
            mVinculado = false;
        }
    }

}