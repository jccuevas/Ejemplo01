package libro.ejemplos;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.net.InetSocketAddress;
import java.net.Socket;

public class Ejemplo19 extends AppCompatActivity {

    private Socket mSocket = null;
    private String mIp = "192.168.1.162";
    private int mPuerto = 6000;

    private ServicioVinculadoSimple mServicio = null;
    private boolean mVinculado = false;
    /**
     * Define las llamadas para a vinculación del servicio pasado en
     * bindService()
     */
    private ServiceConnection mConexion = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // Para obtener la instancia al Binder del servicio basta con poner un casting
            ServicioVinculadoSimple.SocketBinder binder =
                    (ServicioVinculadoSimple.SocketBinder) service;
            mServicio = binder.getService();
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
        setContentView(R.layout.activity_ejemplo19);
    }

    public void onConnect(View view) {

        if (mVinculado) {
            //Se invoca el método conectar del servicio
            mServicio.conectar(new InetSocketAddress(mIp, mPuerto));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Al iniciar la actividad se conecta al servicio
        Intent intent = new Intent(this, ServicioVinculadoSimple.class);
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
