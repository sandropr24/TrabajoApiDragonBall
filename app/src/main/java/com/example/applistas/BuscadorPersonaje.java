package com.example.applistas;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BuscadorPersonaje extends AppCompatActivity {

    RequestQueue requestQueue;
    final String URL ="https://dragonball-api.com/api/characters/";
    //Java
    EditText edtIdpersonaje, edtNombre , edtKi, edtRaza , edtGenero;
    Button btnBuscarPersonaje;

    private void loadUI(){
        edtIdpersonaje = findViewById(R.id.edtIdpersonaje);
        edtNombre = findViewById(R.id.edtNombre);
        edtKi = findViewById(R.id.edtKi);
        edtRaza = findViewById(R.id.edtRaza);
        edtGenero = findViewById(R.id.edtGenero);
        btnBuscarPersonaje= findViewById(R.id.btnBuscarPersonaje);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscador_personaje);

        this.loadUI();

        //Eventos
        btnBuscarPersonaje.setOnClickListener(v -> {getDataCharacter();});
    }//on create

    private void getDataCharacter(){
        //Comunicacion Dragon Ball API
        if(edtIdpersonaje.getText().toString().isEmpty()){
            edtIdpersonaje.setError("Escribir un ID");
            edtIdpersonaje.requestFocus();
            return;
        }

        String endPoint = URL + edtIdpersonaje.getText().toString(); //Se agrega el id

        //Abrir canal de comunicacion
        requestQueue = Volley.newRequestQueue(this);

        //¿Que tipo de datos me devuelve el API ?
        //VOLLEY LAS SOLICITUDES TIENEN 5 PARTES
        //VERBO, URL , JSONENVIADO,RESULTADO, ERROR
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                endPoint,
                null,
                this::showData,
                this::errorWS
        );

        //Enviamos la solicitud
        requestQueue.add(jsonObjectRequest);
    }

    //this::showData,  se activa cuando el servicio retorna  2XX
    private void showData( JSONObject jsonObject){
        Log.d("ResultadoWS",jsonObject.toString());

        //Java puede gestionar JSON solo en entornos seguro
        try{
            edtNombre.setText(jsonObject.getString("name"));
            edtRaza.setText(jsonObject.getString("race"));
            edtGenero.setText(jsonObject.getString("gender"));
            edtKi.setText(jsonObject.getString("ki"));
        }catch(Exception e){
            //Log.e("ErrorJSON" , e.toString());
        }
    }

    private void errorWS(VolleyError e){
        Log.e("ErrorWS", e.toString());

        //Para gestionar errores , necesitamos de un objeto
        NetworkResponse response = e.networkResponse;

        //si existe una respuesta (existe un error)
        if(response != null && response.data != null){
            int statusCode = response.statusCode;

            if(statusCode == 400){
                String dataError = new String(response.data);
                try {
                    JSONObject jsonError = new JSONObject(dataError);
                    Toast.makeText(getApplicationContext(), jsonError.getString("message"), Toast.LENGTH_SHORT).show();
                    Log.e("ErrorWS" , dataError);
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }

            }
        }
    }

}//Buscador de personaje