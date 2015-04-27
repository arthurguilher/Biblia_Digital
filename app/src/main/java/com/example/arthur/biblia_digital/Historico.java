package com.example.arthur.biblia_digital;

/**
 * Created by Arthur on 17/04/2015.
 */
public class Historico {

    private int id;
    private int capitulo;
    private String livro;

    public Historico(int id, String livro, int capitulo) {
        this.id = id;
        this.livro = livro;
        this.capitulo = capitulo;
    }

    public Historico(String livro, int capitulo) {
        this.livro = livro;
        this.capitulo = capitulo;
    }

    public Historico(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(int capitulo) {
        this.capitulo = capitulo;
    }

    public String getLivro() {
        return livro;
    }

    public void setLivro(String livro) {
        this.livro = livro;
    }
}
