package com.example.arthur.biblia_digital;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class ListaVersiculosFragment extends ListFragment {

    private List<String> listaVersiculos = new ArrayList<String>();
    private List<String> listaAuxiliar = new ArrayList<String>();
    private ListView listView;
    private int idVersiculo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_lista_versiculos_fragment, container, false);


        NodeList nodesLivros = MainActivity.nodeLivros;
        NodeList nodesCapitulos = ListaCapitulos.nodeCapitulos;


        /********** Código para criar lista de versículos ***************/
        for (int temp = 0; temp < nodesLivros.getLength(); temp++) {
            Node nNode = nodesLivros.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("n").equalsIgnoreCase(ListaVersiculos.livro.toLowerCase())) {
                    for (int i = 0; i < nodesCapitulos.getLength(); i++) {
                        Node nNode2 = nodesCapitulos.item(i);
                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement2 = (Element) nNode2;
                            NodeList versiculos = eElement2.getChildNodes();
                            if (Integer.parseInt(eElement2.getAttribute("n")) == ListaVersiculos.capitulo) {
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
       //listView = (ListView) findViewById(R.id.list);

        /*listView.setAdapter(adapter);
        /registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idVersiculo = position;
                //System.out.println(position);
                //alertMessage();
            }
        });*/


        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, listaAuxiliar);
        setListAdapter(adapter);
    }
}
