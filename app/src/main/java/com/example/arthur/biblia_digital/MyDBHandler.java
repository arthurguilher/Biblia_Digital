package com.example.arthur.biblia_digital;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Arthur on 17/04/2015.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.db";
    public static final String TABLE_FAVORITOS = "favoritos";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LIVRO = "livro";
    public static final String COLUMN_CAPITULO = "capitulo";
    public static final String COLUMN_VERSICULO = "versiculo";
    public static final String COLUMN_ID_VERSICULO = "id_versiculo";

    public MyDBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String CREATE_FAVORITOS_TABLE = "CREATE TABLE " +
                TABLE_FAVORITOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_LIVRO
                + " TEXT," + COLUMN_CAPITULO + " INTEGER," + COLUMN_VERSICULO + " TEXT" + ")";*/
        String query = "CREATE TABLE favoritos (id INTEGER PRIMARY KEY AUTOINCREMENT, livro TEXT, capitulo INTEGER, versiculo TEXT, id_versiculo INTEGER)";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITOS);
        onCreate(db);
    }

    public void adicionarFavorito(Favorito favorito) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_LIVRO, favorito.getLivro());
        values.put(COLUMN_CAPITULO, favorito.getCapitulo());
        values.put(COLUMN_VERSICULO, favorito.getVersiculo());
        values.put(COLUMN_ID_VERSICULO, favorito.getId_versiculo());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_FAVORITOS, null, values);
        db.close();
    }

    public ArrayList<Favorito> listaFavoritos() {
        String query = "Select * FROM " + TABLE_FAVORITOS;
        ArrayList<Favorito> listaFavoritos = new ArrayList<Favorito>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Favorito favorito = new Favorito();
                favorito.setId(Integer.parseInt(cursor.getString(0)));
                favorito.setLivro(cursor.getString(1));
                favorito.setCapitulo(Integer.parseInt(cursor.getString(2)));
                favorito.setVersiculo(cursor.getString(3));
                favorito.setId_versiculo(Integer.parseInt(cursor.getString(4)));
                listaFavoritos.add(favorito);
                cursor.moveToNext();
            }
        } else {
            //favorito = null;
        }
        cursor.close();
        db.close();
        return listaFavoritos;
    }

    public ArrayList<String> listaFavoritosLivros() {
        String query = "Select livro FROM " + TABLE_FAVORITOS;
        ArrayList<String> listaFavoritosLivros = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                if (!listaFavoritosLivros.contains(cursor.getString(0))) {
                    listaFavoritosLivros.add(cursor.getString(0));
                }
                cursor.moveToNext();
            }
        } else {
            //favorito = null;
        }
        cursor.close();
        db.close();
        return listaFavoritosLivros;
    }


    public boolean excluirFavorito(Favorito favorito) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITOS, COLUMN_ID + " = ?", new String[] { String.valueOf(favorito.getId()) });
        db.close();
        return true;
    }

}
