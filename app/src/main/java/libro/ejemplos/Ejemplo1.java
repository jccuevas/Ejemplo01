package libro.ejemplos;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cliente.Cliente;

public class Ejemplo1 extends AppCompatActivity {

    public static final String EXTRA_NOMBRE = "nombre";
    public static final String EXTRA_CLAVE = "clave";
    public static final String EXTRA_SESION = "sesion";
    public static final String ESTADO_CONECTADO = "estado_conectado";
    public static final String ESTADO_IP = "estado_ip";
    public static final String ESTADO_PUERTO = "estado_puerto";
    private static Handler mHandler = null;
    protected Cliente mCliente;
    TextView mResponse = null;
    Button mBotonConectar = null;
    Button mBotonEnviar = null;
    private Socket mSocket = null;
    //private String mIp = "192.168.1.157";
    private String mIp = "192.168.1.162";
    private int mPuerto = 6000;

    private ServicioVinculadoConectar mServicio=null;
    private boolean mVinculado=false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResponse = (TextView) findViewById(R.id.main_response);
        mBotonConectar = (Button) findViewById(R.id.main_connect);
        mBotonEnviar = (Button) findViewById(R.id.main_send);

//        if (savedInstanceState != null) {
//            boolean conectado = savedInstanceState.getBoolean(ESTADO_CONECTADO);
//
//            if (conectado) {
//                //Como se guardó que el socket estaba conectado
//                //se recupera la ip y el puerto y se vuelve a establecer
//                //la conexión
//                mIp = savedInstanceState.getString(ESTADO_IP);
//                mPuerto = savedInstanceState.getInt(ESTADO_PUERTO);
//                InetSocketAddress direccion = new InetSocketAddress(mIp.substring(1), mPuerto);
//                CreaSocket conectar = new CreaSocket();
//                conectar.execute(direccion);
//            }
//        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message inputMessage) {
                mResponse = (TextView)findViewById(R.id.main_response);
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
        actualizaInterfaz();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

//    @Override
//    protected void onStop() {
//        super.onStop();
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://libro.ejemplo/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        if (mSocket != null)
//            if (!mSocket.isClosed())
//                try {
//                    mSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.disconnect();
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void actualizaInterfaz() {
        if (mSocket != null) {
            if (!mSocket.isClosed()) {
                mBotonConectar.setText(getString(R.string.main_button_disconnect));
                mBotonEnviar.setVisibility(View.VISIBLE);
            } else {
                mBotonConectar.setText(getString(R.string.main_button_connect));
                mBotonEnviar.setVisibility(View.INVISIBLE);
            }
        } else {
            mBotonConectar.setText(getString(R.string.main_button_connect));
            mBotonEnviar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Este método se llamará cuando se toque el botón conectar
     *
     * @param view vista que generó el evento
     */
//    public void onConnect(View view) {
//
//        InetSocketAddress direccion = new InetSocketAddress(mIp,mPuerto);
//        if(mSocket!=null) {
//            if (mSocket.isConnected())
//                try {
//                    mSocket.close();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    actualizaInterfaz();
//                    mResponse.setText(getString(R.string.main_disconnected));
//                    mSocket=null;
//                }
//        }else {
//            CreaSocket conectar = new CreaSocket();
//            conectar.execute(direccion);
//        }
//    }


    public void onConnect(View view) {
        //InetSocketAddress direccion = new InetSocketAddress(mIp, mPuerto);
        //new Thread(new HebraConectar(direccion)).start();
        //new Thread(new HebraConectarSimple2(direccion)).start();
//        ServicioNotificaciones servicio = new ServicioNotificaciones();
//        Intent conecta= new Intent(this,ServicioNotificaciones.class);
//        conecta.putExtra(ServicioNotificaciones.EXTRA_IP,mIp);
//        conecta.putExtra(ServicioNotificaciones.EXTRA_PORT,mPuerto);


//        Intent conecta= new Intent(this,ServicioPrimerPlano.class);
//        conecta.putExtra(ServicioPrimerPlano.EXTRA_IP,mIp);
//        conecta.putExtra(ServicioPrimerPlano.EXTRA_PORT,mPuerto);

//        Intent conecta = new Intent(this, IntentServiceConectar.class);
//        conecta.putExtra(IntentServiceConectar.EXTRA_IP, mIp);
//        conecta.putExtra(IntentServiceConectar.EXTRA_PORT, mPuerto);
//
//        startService(conecta);

        //Servicio vinculado
        if (mVinculado) {
            //Se invoca el método conectar del servicio
            mServicio.conectar(mHandler,new InetSocketAddress(mIp,mPuerto));
        }

        InetSocketAddress direccion = new InetSocketAddress(mIp, mPuerto);
        new Thread(new Ejemplo1.HebraConectar(direccion)).start();

//        Intent conecta= new Intent(this,ServicioConectar.class);
//        conecta.putExtra(ServicioConectar.EXTRA_IP,mIp);
//        conecta.putExtra(ServicioConectar.EXTRA_PORT,mPuerto);
//        startService(conecta);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Al iniciar la actividad se conecta al servicio
        Intent intent = new Intent(this, ServicioVinculadoConectar.class);
        bindService(intent, mConexion, BIND_AUTO_CREATE |BIND_ADJUST_WITH_ACTIVITY);
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

    public void onSend(View view) {

        Ejemplo1.Autenticar enviar = new Ejemplo1.Autenticar();
        enviar.execute(mSocket);

    }

    public void nuevoUsuario(View view) {
        Intent intent = new Intent(this, NuevoUsuarioActivity.class);
        EditText campo_nombre = (EditText) findViewById(R.id.edit_nombre);
        EditText campo_clave = (EditText) findViewById(R.id.edit_clave);
        String nombre = campo_nombre.getText().toString();
        String clave = campo_clave.getText().toString();
        intent.putExtra(EXTRA_NOMBRE, nombre);
        intent.putExtra(EXTRA_CLAVE, clave);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)//Actividad NuevoUsuarioActivity
            if (resultCode == RESULT_OK) {
                String sesion = data.getStringExtra(MainActivity.EXTRA_SESION);
                Toast.makeText(this, "Nueva sesión: " + sesion, Toast.LENGTH_LONG).show();
            }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_LONG).show();
        }

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://libro.ejemplo/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }

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
            String respuesta = "";

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
                respuesta = respuesta + "\r\n" + bis.readLine();
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

    /**
     * HebraConectar
     */
    public class HebraConectar implements Runnable {

        private InetSocketAddress mIp = null;

        public HebraConectar(InetSocketAddress ip) {
            mIp = ip;

        }

        protected void enviaRespuesta(String respuesta) {
            Message recibido = Message.obtain(mHandler, 1);
            Bundle datos = new Bundle();
            datos.putString("response", respuesta);
            recibido.setData(datos);
            recibido.sendToTarget();
        }

        @Override
        public void run() {
            String respuesta = "";
            Socket cliente;

            try {
                //Se crea el socket TCP
                cliente = new Socket();
                //Se realiza la conexión al servidor
                cliente.connect(mIp);
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                OutputStream os = cliente.getOutputStream();
                respuesta = bis.readLine();
                enviaRespuesta(respuesta);

                os.write(new String("USER USER\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();
                enviaRespuesta(respuesta);

                os.write(new String("PASS 12345\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();
                if (respuesta != null) {
                    if (respuesta.startsWith("OK"))
                        respuesta = "Autenticado correctamente";
                    else {
                        respuesta = "Error de autenticación";
                    }
                }
                enviaRespuesta(respuesta);

                for (int n = 0; n < 10; n++) {
                    os.write(new String("ECHO " + n + "\r\n").getBytes());
                    os.flush();
                    respuesta = bis.readLine();
                    enviaRespuesta(respuesta);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                os.write(new String("QUIT\r\n").getBytes());
                os.flush();
                respuesta = bis.readLine();
                enviaRespuesta(respuesta);
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
        }


    }

    /**
     * HebraConectarSimple
     * Esta clase permite la conexión con un servidor TCP y la recepcion de la respuesta del mismo
     */
    public class HebraConectarSimple implements Runnable {

        String mRespuesta = "";
        private InetSocketAddress mIp = null;

        public HebraConectarSimple(InetSocketAddress ip) {
            mIp = ip;
        }

        @Override
        public void run() {
            Socket cliente;
            try {
                //Se crea el socket TCP
                cliente = new Socket();
                //Se realiza la conexión al servidor
                cliente.connect(mIp);
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                OutputStream os = cliente.getOutputStream();
                mRespuesta = bis.readLine();
                mResponse.post(new Runnable() {
                    //mResponse es un atributo de la actividad
                    // vinculado a un campo de texto
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });

                os.write(new String("QUIT\r\n").getBytes());
                os.flush();
                mRespuesta = bis.readLine();
                mResponse.post(new Runnable() {
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });
                bis.close();
                os.close();
                cliente.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                mRespuesta = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                mRespuesta = "IOException: " + e.toString();
            }

            mResponse.post(new Runnable() {
                @Override
                public void run() {
                    mResponse.setText(mRespuesta);
                }
            });
        }
    }


    /**
     * HebraConectarSimple
     * Esta clase permite la conexión con un servidor TCP y la recepcion de la respuesta del mismo
     */
    public class HebraConectarSimple2 implements Runnable {

        String mRespuesta = "";
        private InetSocketAddress mIp = null;

        public HebraConectarSimple2(InetSocketAddress ip) {
            mIp = ip;
        }

        @Override
        public void run() {
            Socket cliente;
            try {
                //Se crea el socket TCP
                cliente = new Socket();
                //Se realiza la conexión al servidor
                cliente.connect(mIp);
                //Se leen los datos del buffer de entrada
                BufferedReader bis = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                OutputStream os = cliente.getOutputStream();
                mRespuesta = bis.readLine();
                mResponse.post(new Runnable() {
                    //mResponse es un atributo de la actividad
                    // vinculado a un campo de texto
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });

                os.write(new String("USER USER\r\n").getBytes());
                os.flush();
                mRespuesta = bis.readLine();
                mResponse.post(new Runnable() {
                    //mResponse es un atributo de la actividad
                    // vinculado a un campo de texto
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });

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
                mResponse.post(new Runnable() {
                    //mResponse es un atributo de la actividad
                    // vinculado a un campo de texto
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });

                for (int n = 0; n < 10; n++) {
                    os.write(new String("ECHO " + n + "\r\n").getBytes());
                    os.flush();
                    mRespuesta = bis.readLine();
                    mResponse.post(new Runnable() {
                        //mResponse es un atributo de la actividad
                        // vinculado a un campo de texto
                        @Override
                        public void run() {
                            mResponse.setText(mRespuesta);
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                os.write(new String("QUIT\r\n").getBytes());
                os.flush();
                mRespuesta = bis.readLine();
                mResponse.post(new Runnable() {
                    @Override
                    public void run() {
                        mResponse.setText(mRespuesta);
                    }
                });
                bis.close();
                os.close();
                cliente.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                mRespuesta = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                mRespuesta = "IOException: " + e.toString();
            }

            mResponse.post(new Runnable() {
                @Override
                public void run() {
                    mResponse.setText(mRespuesta);
                }
            });
        }
    }
}
