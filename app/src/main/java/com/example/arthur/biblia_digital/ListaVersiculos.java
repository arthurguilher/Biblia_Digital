package com.example.arthur.biblia_digital;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class ListaVersiculos extends ActionBarActivity {

    private int capitulo;
    private int idVersiculo;
    private String versiculo;
    private List<String> listaVersiculos = new ArrayList<String>();
    private List<String> listaAuxiliar = new ArrayList<String>();
    private ListView listView;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_versiculos);
        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        capitulo = params.getInt("capitulo");
        setTitle(ListaCapitulos.livro + " - Capítulo " + capitulo);
        NodeList nodesLivros = MainActivity.nodeLivros;
        NodeList nodesCapitulos = ListaCapitulos.nodeCapitulos;


        /********** Código para criar lista de versículos ***************/
        for (int temp = 0; temp < nodesLivros.getLength(); temp++) {
            Node nNode = nodesLivros.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("n").equalsIgnoreCase(ListaCapitulos.livro.toLowerCase())) {
                    for (int i = 0; i < nodesCapitulos.getLength(); i++) {
                        Node nNode2 = nodesCapitulos.item(i);
                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement2 = (Element) nNode2;
                            NodeList versiculos = eElement2.getChildNodes();
                            if (Integer.parseInt(eElement2.getAttribute("n")) == capitulo) {
                                for (int j = 0; j < versiculos.getLength(); j++) {
                                    Node nNode3 = versiculos.item(j);
                                    if (nNode3.getNodeType() == Node.ELEMENT_NODE) {
                                        Element eElement3 = (Element) nNode3;
                                        listaVersiculos.add(eElement2.getElementsByTagName("v").item(Integer.parseInt(eElement3.getAttribute("n")) - 1).getTextContent());
                                        listaAuxiliar.add(eElement3.getAttribute("n") + " - " + eElement2.getElementsByTagName("v").item(Integer.parseInt(eElement3.getAttribute("n")) - 1).getTextContent());
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }

        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listaAuxiliar);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idVersiculo = position;
                System.out.println(position);
                alertMessage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_versiculos, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        MyDBHandler db = new MyDBHandler(context, null, null, 1);
                        Favorito favorito = new Favorito(ListaCapitulos.livro, capitulo, listaVersiculos.get(idVersiculo), idVersiculo);
                        db.adicionarFavorito(favorito);
                        Toast.makeText(context, "O versículo foi adicionado aos favoritos", Toast.LENGTH_LONG).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE: // No button clicked // do nothing
                        //Toast.makeText(context, "No Clicked", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deseja adicionar este versículo aos favoritos?").setPositiveButton("Sim", dialogClickListener).setNegativeButton("Não", dialogClickListener).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            //YourObject obj = (YourObject) lv.getItemAtPosition(acmi.position);

            menu.setHeaderTitle("Opções do versículo");
            menu.add(lv.getItemAtPosition(acmi.position).toString());
            System.out.println(acmi.position);
            //menu.add(obj.name);
        }
    }

}
