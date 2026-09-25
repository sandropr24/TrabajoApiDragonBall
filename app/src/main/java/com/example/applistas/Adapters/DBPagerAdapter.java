package com.example.applistas.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.applistas.Fragments.BuscadorPersonajeFragment;
import com.example.applistas.Fragments.ListaPersonajesFragment;
import com.example.applistas.Fragments.ListaPlanetasFragment;

//1. Herencia
public class DBPagerAdapter extends FragmentStateAdapter {
    //2.NO hay cambios
    public DBPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
             case 0: return new BuscadorPersonajeFragment();
             case 1: return new ListaPersonajesFragment();
            default: return new ListaPlanetasFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
