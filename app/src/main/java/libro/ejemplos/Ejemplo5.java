package libro.ejemplos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.net.Socket;

public class Ejemplo5 extends AppCompatActivity {
    public static final String ESTADO_CONECTADO = "estado_conectado";
    public static final String ESTADO_IP = "estado_ip";
    public static final String ESTADO_PUERTO = "estado_puerto";

    private Socket mSocket = null;
    private String mIp = "192.168.1.130";
    private int mPuerto = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo05);

        if (savedInstanceState != null) {
            boolean conectado =
                    savedInstanceState.getBoolean(ESTADO_CONECTADO);

            if (conectado) {
                //Como se guardó que el socket estaba conectado
                //se recupera la ip y el puerto y se vuelve a establecer
                //la conexión
                mIp = savedInstanceState.getString(ESTADO_IP);
                mPuerto = savedInstanceState.getInt(ESTADO_PUERTO);
                //Se realiza la conexión, más tarde se verá como
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mSocket != null) {
            if (!mSocket.isClosed()) {
                //La conexión sigue activa
                outState.putBoolean(ESTADO_CONECTADO, true);
                outState.putString(ESTADO_IP, mSocket.getInetAddress().toString());
                outState.putInt(ESTADO_PUERTO, mSocket.getPort());
            } else
                outState.putBoolean(ESTADO_CONECTADO, false);
        } else
            outState.putBoolean(ESTADO_CONECTADO, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Si hay una conexión active se debe cerrar
        if (mSocket != null)
            if (!mSocket.isClosed())
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

}
