package com.example.applistas;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class BuscadorPersonaje extends AppCompatActivity {

    RequestQueue requestQueue;
    final String URL = "https://dragonball-api.com/api/characters/";

    // Vistas
    EditText edtIdpersonaje, edtNombre, edtKi, edtRaza, edtGenero;
    ImageView imgPersonaje;
    Button btnBuscarPersonaje, btnReiniciar, btnTransformaciones;

    // Almacena las transformaciones en crudo devueltas por la API
    JSONArray transformacionesArray = null;

    private void loadUI() {
        edtIdpersonaje = findViewById(R.id.edtIdpersonaje);
        edtNombre = findViewById(R.id.edtNombre);
        edtKi = findViewById(R.id.edtKi);
        edtRaza = findViewById(R.id.edtRaza);
        edtGenero = findViewById(R.id.edtGenero);
        imgPersonaje = findViewById(R.id.imgPersonaje);
        btnBuscarPersonaje = findViewById(R.id.btnBuscarPersonaje);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        btnTransformaciones = findViewById(R.id.btnTransformaciones);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscador_personaje);

        this.loadUI();
        requestQueue = Volley.newRequestQueue(this);

        btnBuscarPersonaje.setOnClickListener(v -> getDataCharacter());
        btnReiniciar.setOnClickListener(v -> resetUI());
        btnTransformaciones.setOnClickListener(v -> mostrarDialogoTransformaciones());
    }

    private void getDataCharacter() {
        String id = edtIdpersonaje.getText().toString().trim();

        if (id.isEmpty()) {
            edtIdpersonaje.setError("Escribir un ID");
            edtIdpersonaje.requestFocus();
            return;
        }

        String endPoint = URL + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                endPoint,
                null,
                this::showData,
                this::errorWS
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void showData(JSONObject jsonObject) {
        Log.d("ResultadoWS", jsonObject.toString());

        try {
            edtNombre.setText(jsonObject.optString("name", ""));
            edtRaza.setText(jsonObject.optString("race", ""));
            edtGenero.setText(jsonObject.optString("gender", ""));
            edtKi.setText(jsonObject.optString("ki", ""));

            // Cargar imagen del personaje principal
            String imageUrl = jsonObject.optString("image", "");
            if (!imageUrl.isEmpty()) {
                loadImageInto(imageUrl, imgPersonaje);
            } else {
                imgPersonaje.setImageDrawable(null);
            }

            // Procesar transformaciones
            transformacionesArray = jsonObject.optJSONArray("transformations");

            if (transformacionesArray != null && transformacionesArray.length() > 0) {
                btnTransformaciones.setEnabled(true);
            } else {
                btnTransformaciones.setEnabled(false);
            }

        } catch (Exception e) {
            Log.e("ErrorJSON", e.toString());
            Toast.makeText(getApplicationContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
            resetUI();
        }
    }

    private void loadImageInto(String imageUrl, ImageView targetView) {
        if (imageUrl == null || imageUrl.isEmpty()) return;

        ImageRequest imageRequest = new ImageRequest(
                imageUrl,
                targetView::setImageBitmap,
                0,
                0,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.ARGB_8888,
                error -> Log.e("ErrorImg", "No se pudo cargar: " + imageUrl)
        );

        requestQueue.add(imageRequest);
    }

    private void errorWS(VolleyError e) {
        Log.e("ErrorWS", e.toString());

        NetworkResponse response = e.networkResponse;
        if (response != null) {
            int statusCode = response.statusCode;
            if (statusCode == 404 || statusCode == 400) {
                Toast.makeText(getApplicationContext(), "Personaje no encontrado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error en el servidor (" + statusCode + ")", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Sin conexión o servicio no disponible", Toast.LENGTH_SHORT).show();
        }

        resetUI();
    }

    private void resetUI() {
        edtIdpersonaje.setText("");
        edtNombre.setText("");
        edtKi.setText("");
        edtRaza.setText("");
        edtGenero.setText("");
        imgPersonaje.setImageDrawable(null);

        transformacionesArray = null;
        btnTransformaciones.setEnabled(false);

        edtIdpersonaje.requestFocus();
    }

    private void mostrarDialogoTransformaciones() {
        if (transformacionesArray == null || transformacionesArray.length() == 0) {
            Toast.makeText(this, "No posee transformaciones", Toast.LENGTH_SHORT).show();
            return;
        }

        // Desplazamiento horizontal con tarjetas generadas en tiempo de ejecución
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.HORIZONTAL);
        contenedor.setPadding(30, 20, 30, 20);
        scrollView.addView(contenedor);

        for (int i = 0; i < transformacionesArray.length(); i++) {
            JSONObject trans = transformacionesArray.optJSONObject(i);
            if (trans == null) continue;

            String nombre = trans.optString("name", "Sin nombre");
            String ki = trans.optString("ki", "N/A");
            String imagen = trans.optString("image", "");

            // Tarjeta individual
            LinearLayout tarjeta = new LinearLayout(this);
            tarjeta.setOrientation(LinearLayout.VERTICAL);
            tarjeta.setGravity(Gravity.CENTER_HORIZONTAL);
            tarjeta.setPadding(16, 16, 16, 16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            tarjeta.setLayoutParams(params);
            tarjeta.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

            // Imagen de la transformación
            ImageView img = new ImageView(this);
            img.setLayoutParams(new LinearLayout.LayoutParams(360, 420));
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            loadImageInto(imagen, img);

            // Nombre
            TextView tvNombre = new TextView(this);
            tvNombre.setText(nombre);
            tvNombre.setTextColor(Color.BLACK);
            tvNombre.setTextSize(14f);
            tvNombre.setGravity(Gravity.CENTER);
            tvNombre.setPadding(0, 10, 0, 4);

            // Ki
            TextView tvKi = new TextView(this);
            tvKi.setText("Ki: " + ki);
            tvKi.setTextColor(Color.DKGRAY);
            tvKi.setTextSize(12f);
            tvKi.setGravity(Gravity.CENTER);

            tarjeta.addView(img);
            tarjeta.addView(tvNombre);
            tarjeta.addView(tvKi);

            contenedor.addView(tarjeta);
        }

        new AlertDialog.Builder(this)
                .setTitle("Transformaciones de " + edtNombre.getText().toString())
                .setView(scrollView)
                .setPositiveButton("Cerrar", null)
                .show();
    }
}