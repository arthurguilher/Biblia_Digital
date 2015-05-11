package com.example.arthur.biblia_digital;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;


public class ListaHistorico extends ActionBarActivity implements ActionBar.OnNavigationListener {

    private ArrayList<Historico> listaHistorico = new ArrayList<Historico>();
    private ListView listView;
    private final Context context = this;
    private int index;
    // action bar
    private ActionBar actionBar;

    // Title navigation Spinner data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_historico);
        setTitle("Histórico");

        //Pega a lista do histórico do banco de dados
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        listaHistorico = dbHandler.listaHistorico();

        ArrayList<String> listaString = new ArrayList<String>();
        for (int i = 0; i < listaHistorico.size(); i++){
            Historico historico = listaHistorico.get(i);
            listaString.add(historico.getLivro() + " - Capítulo " + historico.getCapitulo());
        }

        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listaString);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_historico, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            alertMessage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            index = acmi.position;
            menu.setHeaderTitle("Opções do histórico");
            menu.add("Abrir capítulo");
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        Intent intent;
        if (item.getTitle().equals("Abrir capítulo")){

            for (int temp = 0; temp < MainActivity.nodeLivros.getLength(); temp++) {
                Node nNode = MainActivity.nodeLivros.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getAttribute("n").equalsIgnoreCase(listaHistorico.get(index).getLivro())) {
                        MainActivity.nodeCapitulos = eElement.getChildNodes();
                    }
                }
            }


            intent = new Intent(context, ListaVersiculos.class);
            Bundle params = new Bundle();
            params.putInt("busca", 1);
            params.putInt("capitulo", listaHistorico.get(index).getCapitulo());
            params.putString("livro", listaHistorico.get(index).getLivro());
            params.putInt("qntCapitulos", MainActivity.qntCapitulos(listaHistorico.get(index).getLivro()));
            intent.putExtras(params);
            startActivity(intent);
        }
        return false;
    }

    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        MyDBHandler db = new MyDBHandler(context, null, null, 1);
                        db.limparHistorico();
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent);
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Deseja limpar todo o histórico?").setPositiveButton("Sim", dialogClickListener).setNegativeButton("Não", dialogClickListener).show();
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        return false;
    }
}
