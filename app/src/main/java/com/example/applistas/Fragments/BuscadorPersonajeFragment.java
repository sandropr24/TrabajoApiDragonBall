package com.example.applistas.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.applistas.R;

//1.Herencia
public class BuscadorPersonajeFragment extends Fragment {

    Button btnSaludar;


    private void loadUI(@NonNull View view){
        btnSaludar = view.findViewById(R.id.btnSaludar);

    }

    private void saludar(){
        Toast.makeText(requireContext(),"Hola esta es la accion" , Toast.LENGTH_SHORT).show();
    }

    //2.Constructor vacio
    public BuscadorPersonajeFragment(){}

    //3.Implememtar 2 metodos(constructor > ejecutar)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //4.Vinculacion >>> XML
        return inflater.inflate(R.layout.fragment_buscador_personaje, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.loadUI(view); //Fragmento

        btnSaludar.setOnClickListener(v ->{saludar();});
    }
}
