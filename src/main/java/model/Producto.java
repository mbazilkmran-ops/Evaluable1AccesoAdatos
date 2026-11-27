package model;

public class Producto {
    // Estos nombres coinciden con las propiedades recibidas en el JSON para ver esos datos podemos abrir el json en pagina web .
    // Gson usa esos nombres para colocar los datos dentro del objeto.
    private int id;
    private String title;
    private String description;
    private int stock;
    private double price;


    // Los getters permiten que otras clases lean estos atributos privados.
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    // Gson necesita un constructor vacío para crear Producto y luego rellenar sus atributos.
    public Producto() {
    }











}
