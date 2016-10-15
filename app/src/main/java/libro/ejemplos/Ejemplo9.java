package libro.ejemplos;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Ejemplo9 extends AppCompatActivity {
    TextView mResponse = null;
    private String mIp = "192.168.1.155";
    private int mPuerto = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo09);

        mResponse = (TextView)findViewById(R.id.e09_response);
    }
    public void onConnect(View view) {

        InetSocketAddress direccion = new InetSocketAddress(mIp,mPuerto);
        Conectar conectar = new Conectar();
        conectar.execute(direccion);

    }

    public class Conectar extends AsyncTask<InetSocketAddress, Void, String> {

        @Override
        protected String doInBackground(InetSocketAddress... arg0) {

            Socket cliente = null;
            String respuesta=null;

            try {
                //Se crea el socket TCP
                cliente = new Socket();
                //Se realiza la conexión al servidor
                //arg0[0] contiene la dirección IP pasada como parámetro
                cliente.connect(arg0[0]);
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(
                        new InputStreamReader(cliente.getInputStream()));
                OutputStream os = cliente.getOutputStream();
                respuesta = bis.readLine();
                os.write(new String("QUIT\r\n").getBytes());
                os.flush();
                respuesta = respuesta +"\r\n"+ bis.readLine();
                bis.close();
                os.close();
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
                respuesta = "IOException: " + e.toString();
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Se actualizan los datos leídos en el campo de texto
            mResponse.setText(result);
        }
    }

}
