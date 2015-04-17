package com.example.arthur.biblia_digital;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ListaCapitulos extends ActionBarActivity {

    public static String livro;
    public static NodeList nodeCapitulos;
    private final Context context = this;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_capitulos);
        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        livro = params.getString("livro");
        setTitle(livro);

        /********** Código para armazenar os capítulos em um array*********/
        int qntCapitulos = 0;
        for (int temp = 0; temp < MainActivity.nodeLivros.getLength(); temp++) {
            Node nNode = MainActivity.nodeLivros.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("n").equalsIgnoreCase(livro)) {
                    nodeCapitulos = eElement.getChildNodes();
                    for (int i = 0; i < nodeCapitulos.getLength(); i++) {
                        Node nNode2 = nodeCapitulos.item(i);
                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            //element = (Element) nNode2;
                            qntCapitulos++;
                        }
                    }
                    break;
                }
            }
        }

        String[] arrayCapitulos = new String[qntCapitulos];
        for (int i = 1; i < qntCapitulos+1; i++) {
            arrayCapitulos[i - 1] = "Capítulo " + i;
        }

        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, arrayCapitulos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ListaVersiculos.class);
                Bundle params = new Bundle();
                params.putInt("capitulo", position + 1);
                intent.putExtras(params);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_capitulos, menu);
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
}
