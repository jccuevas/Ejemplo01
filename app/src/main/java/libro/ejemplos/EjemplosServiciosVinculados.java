package libro.ejemplos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EjemplosServiciosVinculados extends AppCompatActivity implements NetworkInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplos_servicios_vinculados);
    }

    public void onEjemplo19(View view) {
        Intent conecta= new Intent(this,ServicioVinculadoSimple.class);
        startService(conecta);
    }
    public void onEjemplo20(View view) {
        Intent conecta = new Intent(this, ServicioVinculadoConectar.class);
        startService(conecta);
    }
}
