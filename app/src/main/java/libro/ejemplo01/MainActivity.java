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
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.widget.TextView;

import cliente.Cliente;

public class MainActivity extends AppCompatActivity {

    protected Cliente mCliente;
    private Socket mSocket=null;
    private String ip="192.168.1.130";
    private int puerto=6000;
    TextView    mResponse=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResponse= (TextView)findViewById(R.id.main_response);

    }

    /**
     * Este método se llamará cuando se toque el botón conectar
     * @param view vista que generó el evento
     */
    public void onConnect(View view)
    {
//        InetSocketAddress direccion = new InetSocketAddress(ip,puerto);
//        Conectar conecta = new Conectar();
//
//        conecta.execute(direccion);
//        URL url = null;
//        try {
//            url = new URL("http://216.58.214.174/");
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            int codigo=urlConnection.getResponseCode();
//            Toast.makeText(this,"Código: "+codigo,Toast.LENGTH_SHORT).show();
//            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//            String line=null;
//            do {
//                line = in.readLine();
//                System.out.println(line);
//            }while(line!=null);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this,"Excepción: "+e.getMessage(),Toast.LENGTH_SHORT).show();
//        }





        Socket cliente = new Socket();
        InetSocketAddress direccion = new InetSocketAddress(ip,puerto);
        String respuesta = null;
        try {
            cliente.connect(direccion);
            Toast.makeText(this,"Conectado",Toast.LENGTH_SHORT).show();
            BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            respuesta = bis.readLine();
            mResponse.setText(respuesta);


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Excepción: "+e.getMessage(),Toast.LENGTH_SHORT).show();
        } finally {
            if(cliente!=null)
                try {
                    cliente.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }




    public class Conectar extends AsyncTask<InetSocketAddress, Void, String> {

        String dstAddress;
        int dstPort;
        String response = "";
        TextView textResponse;


        @Override
        protected String doInBackground(InetSocketAddress... arg0) {

            Socket cliente = null;
            String respuesta=null;

            try {
                cliente = new Socket();
                cliente.connect(arg0[0]);
                BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                respuesta = bis.readLine();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                respuesta = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                respuesta = "IOException: " + e.toString();
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            mResponse.setText(result);




        }

    }
}
