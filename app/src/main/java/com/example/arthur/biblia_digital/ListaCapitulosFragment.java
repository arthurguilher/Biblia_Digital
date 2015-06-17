package com.example.arthur.biblia_digital;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ListaCapitulosFragment extends Fragment {

    private ArrayList<String> listaCapitulos = new ArrayList<String>();
    private String livro;
    private int qntCapitulos;
    private ListView listView;

    public ListaCapitulosFragment(ArrayList<String> listaCapitulos, String livro){
        this.livro = livro;
        this.listaCapitulos = listaCapitulos;
        //System.out.println(livro + " " + qntCapitulos);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_lista_capitulos_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);

        System.out.println(listaCapitulos.size());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, listaCapitulos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ListaVersiculos.class);
                Bundle params = new Bundle();
                params.putInt("busca", 0);
                params.putInt("capitulo", position + 1);
                params.putString("livro", livro);
                params.putInt("qntCapitulos", listaCapitulos.size());
                intent.putExtras(params);
                startActivity(intent);
                MyDBHandler db = new MyDBHandler(getActivity(), null, null, 1);
                if (db.ultimoHistorico().getCapitulo() != position+1 && db.ultimoHistorico().getLivro() != livro) {
                    db.adicionarHistorico(new Historico(livro, position + 1));
                }
                //System.out.println(livro + " " + (position + 1));
            }
        });
        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
