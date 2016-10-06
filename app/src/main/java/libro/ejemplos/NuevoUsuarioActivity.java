package libro.ejemplos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class NuevoUsuarioActivity extends AppCompatActivity {

    Usuario usuario = new Usuario("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra(MainActivity.EXTRA_NOMBRE);
        String clave  = intent.getStringExtra(MainActivity.EXTRA_CLAVE);

        TextView saludo = (TextView)findViewById(R.id.nuevo_usuario_saludo);
        saludo.setText("Bienvenido "+nombre+" clave="+clave+" Nombre guardado="+usuario.getNombre());

    }

    public void finalizar(View view){

        Random r = new Random();
        Intent data = new Intent();
        data.putExtra(MainActivity.EXTRA_SESION, "idsesion"+r.nextInt(1000));
        setResult(RESULT_OK, data);
        finish();

    }

    public void onSave(View view){

        EditText nombre = (EditText)findViewById(R.id.nuevo_usuario_nombre);
        usuario.setNombre(nombre.getEditableText().toString());
        TextView saludo = (TextView)findViewById(R.id.nuevo_usuario_saludo);
        saludo.setText("Nombre guardado="+usuario.getNombre());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    public class Usuario{
        private String mNombre="";

        Usuario(String nombre){
            mNombre=nombre;
        }

        public String getNombre(){
            return mNombre;
        }

        public void setNombre(String nombre){
            mNombre=nombre;
        }
    }


}
