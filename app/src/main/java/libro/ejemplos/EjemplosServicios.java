package libro.ejemplos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class EjemplosServicios extends AppCompatActivity implements NetworkInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemploservicios);
    }

    public void onEjemplo15(View view) {
        Intent conecta= new Intent(this,ServicioConectar.class);
        conecta.putExtra(ServicioConectar.EXTRA_IP,mIp);
        conecta.putExtra(ServicioConectar.EXTRA_PORT,mPuerto);
        startService(conecta);
    }
    public void onEjemplo16(View view) {
        Intent conecta = new Intent(this, ServicioNotificaciones.class);
        conecta.putExtra(ServicioNotificaciones.EXTRA_IP, mIp);
        conecta.putExtra(ServicioNotificaciones.EXTRA_PORT, mPuerto);
        startService(conecta);
    }
    public void onEjemplo17(View view) {
        Intent conecta = new Intent(this, ServicioPrimerPlano.class);
        conecta.putExtra(ServicioPrimerPlano.EXTRA_IP, mIp);
        conecta.putExtra(ServicioPrimerPlano.EXTRA_PORT, mPuerto);
        startService(conecta);
    }
    public void onEjemplo18(View view) {
        Intent conecta = new Intent(this, IntentServiceConectar.class);
        conecta.putExtra(IntentServiceConectar.EXTRA_IP, mIp);
        conecta.putExtra(IntentServiceConectar.EXTRA_PORT, mPuerto);
        startService(conecta);
    }
}
