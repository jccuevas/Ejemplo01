package libro.ejemplo01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Este método se llamará cuando se toque el botón conectar
     * @param view vista que generó el evento
     */
    public void onConnect(View view)
    {
        Socket cliente = new Socket();
        InetSocketAddress direccion = new InetSocketAddress("192.168.1.130",6000);
        try {
            cliente.connect(direccion);

            Toast.makeText(this,"Conectado",Toast.LENGTH_SHORT).show();
            BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            String recibido = bis.readLine();
            Toast.makeText(this,"Rercibido: "+recibido,Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Excepción: "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }
}
