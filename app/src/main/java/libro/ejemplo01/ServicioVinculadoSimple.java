package libro.ejemplo01;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServicioVinculadoSimple extends Service {
    // Binder que se entraga a los clientes
    private final IBinder mBinder = new SocketBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this,"Cliente vinculado",Toast.LENGTH_LONG).show();
        return mBinder;
    }

    public void conectar(InetSocketAddress direccion){
        Toast.makeText(this,"Conectando con "+direccion.toString(),Toast.LENGTH_LONG).show();
    }



    /**
     * Clase que usa el cliente que se vincula al servicio porque se sabe que siempre
     * se ejecutará en el mismo proceso que sus clientes, por lo que no necesita comnicaciones
     * inter-proceso (IPC).
     */
    public class SocketBinder extends Binder {
        ServicioVinculadoSimple getService() {
            // Devuelve una instancia de ServicioVinculadoConectar para que los clientes puedan llamar
            // a sus métodos públicos.
            return ServicioVinculadoSimple.this;
        }
    }

}