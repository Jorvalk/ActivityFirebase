package com.example.activityfirebase;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etNombreMascota, etCodigoChip, etNombreDueno, etDireccionDueno;
    private Button btnEnviarDatos, btnCargarDatos;
    private LinearLayout llMascotas;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Referencias a los elementos del layout
        etNombreMascota = findViewById(R.id.etNombreMascota);
        etCodigoChip = findViewById(R.id.etCodigoChip);
        etNombreDueno = findViewById(R.id.etNombreDueno);
        etDireccionDueno = findViewById(R.id.etDireccionDueno);
        btnEnviarDatos = findViewById(R.id.btnEnviarDatos);
        btnCargarDatos = findViewById(R.id.btnCargarDatos);
        llMascotas = findViewById(R.id.llMascotas);

        // Configurar el botón para enviar datos
        btnEnviarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos();
            }
        });

        // Configurar el botón para cargar datos
        btnCargarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarDatos();
            }
        });
    }

    private void guardarDatos() {
        String nombreMascota = etNombreMascota.getText().toString();
        String codigoChip = etCodigoChip.getText().toString();
        String nombreDueno = etNombreDueno.getText().toString();
        String direccionDueno = etDireccionDueno.getText().toString();

        if (nombreMascota.isEmpty() || codigoChip.isEmpty() || nombreDueno.isEmpty() || direccionDueno.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que el código de chip contenga al menos una letra mayúscula
        if (!codigoChip.matches(".*[A-Z].*")) {
            Toast.makeText(this, "El código de chip debe contener al menos una letra mayúscula", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa de datos para Firestore
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("nombreMascota", nombreMascota);
        mascota.put("codigoChip", codigoChip);
        mascota.put("nombreDueno", nombreDueno);
        mascota.put("direccionDueno", direccionDueno);

        // Guardar en Firestore
        db.collection("mascotas")
                .add(mascota)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void cargarDatos() {
        db.collection("mascotas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Limpiar la lista antes de cargar nuevos datos
                        llMascotas.removeAllViews();

                        // Iterar sobre los documentos y agregar cada uno a la lista
                        for (DocumentSnapshot document : queryDocumentSnapshots) {  // Cambio aquí
                            Map<String, Object> datos = document.getData();

                            if (datos != null) {
                                // Crear un TextView para cada mascota
                                TextView tvMascota = new TextView(MainActivity.this);
                                tvMascota.setText("Mascota: " + datos.get("nombreMascota") + "\n" +
                                        "Código de chip: " + datos.get("codigoChip") + "\n" +
                                        "Dueño: " + datos.get("nombreDueno") + "\n" +
                                        "Dirección: " + datos.get("direccionDueno"));
                                tvMascota.setPadding(0, 10, 0, 10); // Espaciado entre elementos
                                llMascotas.addView(tvMascota);  // Agregar al contenedor
                            }
                        }
                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No hay datos guardados", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
