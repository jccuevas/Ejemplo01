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

public class Ejemplo10 extends AppCompatActivity implements NetworkInterface{
    TextView mResponse = null;
    Button mBotonConectar = null;
    Button mBotonEnviar = null;

    private Socket mSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo10);

        mResponse = (TextView) findViewById(R.id.e10_response);
        mBotonConectar = (Button) findViewById(R.id.e10_connect);
        mBotonEnviar = (Button) findViewById(R.id.e10_send);
    }

    public void onConnect(View view) {
        if (mSocket == null){
                InetSocketAddress direccion = new InetSocketAddress(mIp, mPuerto);
                CreaSocket creaSocket = new CreaSocket();
                creaSocket.execute(direccion);
            } else if(!mSocket.isClosed())
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    mSocket=null;
                }
        actualizaInterfaz();
    }

    public void onSend(View view) {
        if(mSocket!=null)
            if(!mSocket.isClosed()) {
                Autenticar enviar = new Autenticar();
                enviar.execute(mSocket);
            }
        actualizaInterfaz();
    }

    private void actualizaInterfaz() {
        if (mSocket != null) {
            if (!mSocket.isClosed()) {
                mBotonConectar.setText(getString(R.string.e10_button_disconnect));
                mBotonEnviar.setVisibility(View.VISIBLE);
            } else {
                mBotonConectar.setText(getString(R.string.e10_button_connect));
                mBotonEnviar.setVisibility(View.INVISIBLE);
            }
        } else {
            mBotonConectar.setText(getString(R.string.e10_button_connect));
            mBotonEnviar.setVisibility(View.INVISIBLE);
        }
    }

    public class CreaSocket extends AsyncTask<InetSocketAddress, Void, Socket> {

        private String mRespuesta = null;

        @Override
        protected Socket doInBackground(InetSocketAddress... arg0) {

            Socket cliente;

            try {
                //Se crea el socket TCP
                cliente = new Socket();
                //Se realiza la conexión al servidor
                //arg0[0] contiene la dirección IP pasada como parámetro
                cliente.connect(arg0[0]);
                //Se lee la respuesta del servidor
                BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                mRespuesta = bis.readLine();
            } catch (IOException e) {
                mRespuesta = e.getMessage();
                cliente = null;
                e.printStackTrace();
            }
            return cliente;
        }

        @Override
        protected void onPostExecute(Socket result) {
            super.onPostExecute(result);
            mResponse.setText(mRespuesta);
            mSocket = result;
            actualizaInterfaz();

        }

    }

    public class Autenticar extends AsyncTask<Socket, Void, Socket> {

        private String mRespuesta = "";

        @Override
        protected Socket doInBackground(Socket... arg0) {

            Socket cliente = arg0[0];

            if (cliente != null) {
                if (cliente.isConnected()) {
                    try {
                        BufferedReader bis =
                                new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                        OutputStream os = cliente.getOutputStream();

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
                    } catch (IOException e) {
                        mRespuesta = e.getMessage();
                        e.printStackTrace();
                    }
                } else {
                    mRespuesta = "No está conectado";
                }
            } else {
                mRespuesta = "No está conectado";
            }
            return cliente;
        }

        @Override
        protected void onPostExecute(Socket result) {
            super.onPostExecute(result);
            mResponse.setText(mRespuesta);
            mSocket = result;
            actualizaInterfaz();
        }

    }

}
