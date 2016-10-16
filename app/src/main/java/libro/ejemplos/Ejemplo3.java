package libro.ejemplos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Ejemplo3 extends AppCompatActivity {

    public static final String EXTRA_NOMBRE = "nombre";
    public static final String EXTRA_CLAVE = "clave";
    public static final String EXTRA_SESION = "sesion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejemplo03);

    }

    public void nuevoUsuario(View view) {
        Intent intent = new Intent(this, NuevoUsuarioActivity.class);
        EditText campo_nombre = (EditText) findViewById(R.id.e01_edit_name);
        EditText campo_clave = (EditText) findViewById(R.id.e01_edit_pass);
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
                String sesion = data.getStringExtra(Ejemplo3.EXTRA_SESION);
                Toast.makeText(this, "Nueva sesión: " + sesion, Toast.LENGTH_LONG).show();
            }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_LONG).show();
        }

    }
}