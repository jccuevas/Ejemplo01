package libro.ejemplo01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

public class NuevoUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra(MainActivity.EXTRA_NOMBRE);
        String clave  = intent.getStringExtra(MainActivity.EXTRA_CLAVE);

        TextView saludo = (TextView)findViewById(R.id.nuevo_usuario_saludo);
        saludo.setText("Bienvenido "+nombre+" clave="+clave);

    }

    public void finalizar(View view){

        Random r = new Random();
        Intent data = new Intent();
        data.putExtra(MainActivity.EXTRA_SESION, "idsesion"+r.nextInt(1000));
        setResult(RESULT_OK, data);
        finish();

    }
}
