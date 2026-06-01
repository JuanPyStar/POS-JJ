package modelo;

public class Turno {
    private int idTurno;
    private int idUsuario;
    private String fechaApertura;
    private String fechaCierre;
    private double baseInicial;
    private double totalVentas;
    private int estado;

    public Turno() {
        this.idTurno = 0;
        this.idUsuario = 0;
        this.fechaApertura = "";
        this.fechaCierre = "";
        this.baseInicial = 0.0;
        this.totalVentas = 0.0;
        this.estado = 0;
    }

    public int getIdTurno() { return idTurno; }
    public void setIdTurno(int idTurno) { this.idTurno = idTurno; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(String fechaApertura) { this.fechaApertura = fechaApertura; }

    public String getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(String fechaCierre) { this.fechaCierre = fechaCierre; }

    public double getBaseInicial() { return baseInicial; }
    public void setBaseInicial(double baseInicial) { this.baseInicial = baseInicial; }

    public double getTotalVentas() { return totalVentas; }
    public void setTotalVentas(double totalVentas) { this.totalVentas = totalVentas; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}
