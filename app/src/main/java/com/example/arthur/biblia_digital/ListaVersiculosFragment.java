package com.example.arthur.biblia_digital;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ListaVersiculosFragment extends Fragment {

    private List<String> listaVersiculos = new ArrayList<String>();
    private int idVersiculo;
    private int capitulo;
    private String livro;
    private ArrayList<String> listaAuxiliar = new ArrayList<String>();
    private String clipboad;

    public ListaVersiculosFragment(ArrayList<String> listaVersiculos, ArrayList<String> listaAuxiliar, String livro, int capitulo){
        this.listaVersiculos = listaVersiculos;
        this.livro = livro;
        this.capitulo = capitulo;
        this.listaAuxiliar = listaAuxiliar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_lista_versiculos_fragment, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, listaVersiculos);
        listView.setAdapter(adapter);
        if (ListaVersiculos.busca == 1) {
            listView.setSelection(ListaVersiculos.versiculoFavorito);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            idVersiculo = position;
                            Toast.makeText(getActivity(), capitulo + " " + idVersiculo, Toast.LENGTH_LONG).show();
                            clipboad = "\""+listaAuxiliar.get(idVersiculo)+"\" ("+livro+" " + capitulo+":"+(idVersiculo+1)+")" ;
                            final String [] items = new String[] {"Adicionar aos favoritos", "Copiar texto", "Compartilhar"};
                            final Integer[] icons = new Integer[] {R.drawable.icone_favorito,
                                    R.drawable.icone_copiar, R.drawable.icone_compartilhar};
                            ListAdapter adapter = new ArrayAdapterWithIcon(getActivity(), items, icons);

                            new AlertDialog.Builder(getActivity()).setTitle("Opções do versículo")
                                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) {
                                            switch (item) {
                                                //Adicionar aos favoritos
                                                case 0:
                                                    MyDBHandler db = new MyDBHandler(getActivity(), null, null, 1);
                                                    Favorito favorito = new Favorito(livro, capitulo, listaAuxiliar.get(idVersiculo), idVersiculo);
                                                    System.out.println("VersiculoFragment: " + livro + " " + capitulo + " " + idVersiculo);
                                                    String msg = "";
                                                    if (db.adicionarFavorito(favorito)) {
                                                        msg = "O versículo foi adicionado aos favoritos";
                                                    } else {
                                                        msg = "Este versículo já está no seus favoritos";
                                                    }
                                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                                                    break;
                                                //Copiar texto
                                                case 1:
                                                    ClipboardManager myClipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                                    ClipData myClip = ClipData.newPlainText("text", clipboad);
                                                    myClipboard.setPrimaryClip(myClip);
                                                    Toast.makeText(getActivity(), "O texto do versículo foi copiado.", Toast.LENGTH_LONG).show();
                                                    break;
                                                //Compartilhar
                                                case 2:
                                                    final String[] items = new String[]{"Facebook", "Gmail", "Mensagem"};
                                                    final Integer[] icons = new Integer[]{R.drawable.icone_facebook, R.drawable.icone_gmail,
                                                           R.drawable.icone_mensagem};
                                                    ListAdapter adapter2 = new ArrayAdapterWithIcon(getActivity(), items, icons);
                                                    new AlertDialog.Builder(getActivity()).setTitle("Compartilhar via")
                                                            .setAdapter(adapter2, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int item) {
                                                                    switch (item) {
                                                                        //Facebook
                                                                        case 0:
                                                                            break;
                                                                        //Gmail
                                                                        case 1:
                                                                            try {
                                                                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                                                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Versículo para sua meditação");
                                                                                emailIntent.putExtra(Intent.EXTRA_TEXT, clipboad);
                                                                                emailIntent.setType("plain/text");
                                                                                final PackageManager pm = getActivity().getPackageManager();
                                                                                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                                                                                ResolveInfo best = null;
                                                                                for (final ResolveInfo info : matches)
                                                                                    if (info.activityInfo.packageName.endsWith(".gm") ||
                                                                                            info.activityInfo.name.toLowerCase().contains("gmail"))
                                                                                        best = info;
                                                                                if (best != null)
                                                                                    emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                                                                                startActivity(emailIntent);
                                                                            } catch (Exception e) {
                                                                                Toast.makeText(getActivity(), "Aplicativo do Gmail não localizado.", Toast.LENGTH_LONG).show();
                                                                            }
                                                                            break;
                                                                        //Mensagem
                                                                        case 2:
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
                                                                            {
                                                                                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getActivity()); //Need to change the build to API 19

                                                                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                                                                sendIntent.setType("text/plain");
                                                                                sendIntent.putExtra(Intent.EXTRA_TEXT, clipboad);

                                                                                if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                                                                                {
                                                                                    sendIntent.setPackage(defaultSmsPackageName);
                                                                                }
                                                                                getActivity().startActivity(sendIntent);

                                                                            } else //For early versions, do what worked for you before.
                                                                            {
                                                                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                                                                sendIntent.setData(Uri.parse("sms:"));
                                                                                sendIntent.putExtra("sms_body", clipboad);
                                                                                getActivity().startActivity(sendIntent);
                                                                            }
                                                                            break;
                                                                    }
                                                                }
                                                            }).show();
                                                    break;


                                            }
                                        }
                                    }).show();
                           }
                    });
        return rootView;
    }

    public class ArrayAdapterWithIcon extends ArrayAdapter<String> {

        private List<Integer> images;

        public ArrayAdapterWithIcon(Context context, List<String> items, List<Integer> images) {
            super(context, android.R.layout.select_dialog_item, items);
            this.images = images;
        }

        public ArrayAdapterWithIcon(Context context, String[] items, Integer[] images) {
            super(context, android.R.layout.select_dialog_item, items);
            this.images = Arrays.asList(images);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setCompoundDrawablesWithIntrinsicBounds(images.get(position), 0, 0, 0);
            textView.setCompoundDrawablePadding(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
            return view;
        }

    }


}
