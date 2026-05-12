package modelo;

public class Categoria {

    private int idCategoria;
    private String descripcion;
    private int estado;

    public Categoria() {
        this.idCategoria = 0;
        this.descripcion = "";
        this.estado = 0;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
    
    @Override
    public String toString() {
        return this.descripcion; // Para que el JComboBox muestre el nombre
    }
}
