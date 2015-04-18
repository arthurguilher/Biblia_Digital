package com.example.arthur.biblia_digital;

/**
 * Created by Arthur on 17/04/2015.
 */
public class Favorito {

    private int id;
    private int capitulo;
    private String livro;
    private String versiculo;
    private int id_versiculo;

    public Favorito(int id, String livro, int capitulo, String versiculo, int id_versiculo) {
        this.id = id;
        this.livro = livro;
        this.capitulo = capitulo;
        this.versiculo = versiculo;
        this.id_versiculo = id_versiculo;
    }

    public Favorito(String livro, int capitulo, String versiculo, int id_versiculo) {
        this.livro = livro;
        this.capitulo = capitulo;
        this.versiculo = versiculo;
        this.id_versiculo = id_versiculo;
    }

    public Favorito(){

    }

    public int getId_versiculo() {
        return id_versiculo;
    }

    public void setId_versiculo(int id_versiculo) {
        this.id_versiculo = id_versiculo;
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

    public String getVersiculo() {
        return versiculo;
    }

    public void setVersiculo(String versiculo) {
        this.versiculo = versiculo;
    }
}
