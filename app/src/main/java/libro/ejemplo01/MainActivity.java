package libro.ejemplo01;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cliente.Cliente;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_NOMBRE="nombre";
    public static final String EXTRA_CLAVE="clave";
    public static final String EXTRA_SESION="sesion";
    public static final String ESTADO_CONECTADO="estado_conectado";
    public static final String ESTADO_IP="estado_ip";
    public static final String ESTADO_PUERTO="estado_puerto";
    protected Cliente mCliente;
    TextView    mResponse=null;
    Button mBotonConectar =null;
    Button mBotonEnviar =null;
    private Socket mSocket=null;
    private String mIp="192.168.1.130";
    private int mPuerto=6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResponse= (TextView)findViewById(R.id.main_response);
        mBotonConectar = (Button)findViewById(R.id.main_connect);
        mBotonEnviar = (Button)findViewById(R.id.main_send);

        if(savedInstanceState!=null)
        {
            boolean conectado = savedInstanceState.getBoolean(ESTADO_CONECTADO);

            if(conectado){
                //Como se guardó que el socket estaba conectado
                //se recupera la ip y el puerto y se vuelve a establecer
                //la conexión
                mIp= savedInstanceState.getString(ESTADO_IP);
                mPuerto = savedInstanceState.getInt(ESTADO_PUERTO);
                InetSocketAddress direccion = new InetSocketAddress(mIp,mPuerto);
                CreaSocket conectar = new CreaSocket();
                conectar.execute(direccion);
            }
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        if(mSocket!=null) {
            if (!mSocket.isClosed()) {
                //La conexión sigue activa
                outState.putBoolean(ESTADO_CONECTADO, false);
                outState.putString(ESTADO_IP, mSocket.getInetAddress().toString());
                outState.putInt(ESTADO_PUERTO, mSocket.getPort());

            } else
                outState.putBoolean(ESTADO_CONECTADO, false);
        } else
            outState.putBoolean(ESTADO_CONECTADO,false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        actualizaInterfaz();

    }

    private void actualizaInterfaz()
    {
        if(mSocket!=null) {
            if (!mSocket.isClosed()) {
                mBotonConectar.setText(getString(R.string.main_button_disconnect));
                mBotonEnviar.setVisibility(View.VISIBLE);
            } else {
                mBotonConectar.setText(getString(R.string.main_button_connect));
                mBotonEnviar.setVisibility(View.INVISIBLE);
            }
        }else {
            mBotonConectar.setText(getString(R.string.main_button_connect));
            mBotonEnviar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Este método se llamará cuando se toque el botón conectar
     * @param view vista que generó el evento
     */
    public void onConnect(View view) {

        InetSocketAddress direccion = new InetSocketAddress(mIp,mPuerto);
        if(mSocket!=null) {
            if (mSocket.isConnected())
                try {
                    mSocket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    actualizaInterfaz();
                    mResponse.setText(getString(R.string.main_disconnected));
                    mSocket=null;
                }
        }else {
            CreaSocket conectar = new CreaSocket();
            conectar.execute(direccion);
        }
    }

    public void onSend(View view) {

        Autenticar enviar = new Autenticar();
        enviar.execute(mSocket);

    }

    public void nuevoUsuario(View view) {
        Intent intent = new Intent(this, NuevoUsuarioActivity.class);
        EditText campo_nombre = (EditText) findViewById(R.id.edit_nombre);
        EditText campo_clave = (EditText) findViewById(R.id.edit_clave);
        String nombre = campo_nombre.getText().toString();
        String clave  = campo_clave.getText().toString();
        intent.putExtra(EXTRA_NOMBRE, nombre);
        intent.putExtra(EXTRA_CLAVE, clave);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)//Actividad NuevoUsuarioActivity
            if(resultCode==RESULT_OK)
            {
                String sesion=data.getStringExtra(MainActivity.EXTRA_SESION);
                Toast.makeText(this,"Nueva sesión: "+sesion, Toast.LENGTH_LONG).show();
            }
            if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this,"Operación cancelada", Toast.LENGTH_LONG).show();
            }

    }

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





//        Socket cliente = new Socket();
//        InetSocketAddress direccion = new InetSocketAddress(ip,puerto);
//        String respuesta = null;
//        try {
//            cliente.connect(direccion);
//            Toast.makeText(this,"Conectado",Toast.LENGTH_SHORT).show();
//            BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
//            respuesta = bis.readLine();
//            mResponse.setText(respuesta);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this,"Excepción: "+e.getMessage(),Toast.LENGTH_SHORT).show();
//        } finally {
//            if(cliente!=null)
//                try {
//                    cliente.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//    }




    public class Conectar extends AsyncTask<InetSocketAddress, Void, String> {

        @Override
        protected String doInBackground(InetSocketAddress... arg0) {

            Socket cliente;
            String respuesta="";

            try {
                //Se crea el socket TCP
                cliente = new Socket();
                //Se realiza la conexión al servidor
                //arg0[0] contiene la dirección IP pasada como parámetro
                cliente.connect(arg0[0]);
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                OutputStream os = cliente.getOutputStream();
                respuesta = bis.readLine();
                os.write(new String("QUIT\r\n").getBytes());
                os.flush();
                respuesta = respuesta +"\r\n"+ bis.readLine();
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
            return respuesta;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mResponse.setText(result);
            actualizaInterfaz();
        }

    }

    public class CreaSocket extends AsyncTask<InetSocketAddress, Void, Socket> {

        private String mRespuesta=null;

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
                mRespuesta=e.getMessage();
                cliente=null;
                e.printStackTrace();
            }
            return cliente;
        }

        @Override
        protected void onPostExecute(Socket result) {
            super.onPostExecute(result);
            mResponse.setText(mRespuesta);
            mSocket=result;
            actualizaInterfaz();
//            if(mSocket!=null){
//                if(!mSocket.isClosed()){
//                    mBotonConectar.setText(getString(R.string.main_button_disconnect));
//                }else{
//                    mBotonConectar.setText(getString(R.string.main_button_connect));
//                }
//            }else{
//                mBotonConectar.setText(getString(R.string.main_button_connect));
//            }
        }

    }

    public class Autenticar extends AsyncTask<Socket, Void, Socket> {

        private String mRespuesta="";

        @Override
        protected Socket doInBackground(Socket... arg0) {

            Socket cliente=arg0[0];

            if(cliente!=null) {
                if(cliente.isConnected()) {
                    try {
                        BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                        OutputStream os = cliente.getOutputStream();

                        os.write(new String("USER USER\r\n").getBytes());
                        os.flush();
                        mRespuesta = bis.readLine();
                        os.write(new String("PASS 12345\r\n").getBytes());
                        os.flush();
                        mRespuesta = bis.readLine();
                        if(mRespuesta!=null) {
                            if (mRespuesta.startsWith("OK"))
                                mRespuesta = "Autenticado correctamente";
                            else {
                                mRespuesta = "Error de autenticación";
                            }
                        }
                    } catch (IOException e) {
                        mRespuesta=e.getMessage();
                        e.printStackTrace();
                    }
                } else {
                    mRespuesta="No está conectado";
                }
            }else {
                mRespuesta = "No está conectado";
            }
            return cliente;
        }

        @Override
        protected void onPostExecute(Socket result) {
            super.onPostExecute(result);
            mResponse.setText(mRespuesta);
            mSocket=result;
            actualizaInterfaz();
        }

    }


}
