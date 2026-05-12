package vista;

import controlador.Ctrl_Dashboard;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import conexion.conexion;
import modelo.Usuario;

public class PanelVentasDelDia extends JPanel {

    private Usuario usuarioLogueado;
    private Menu menuPrincipal;
    
    private JTable tablaFacturas;
    private DefaultTableModel modeloFacturas;
    
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorFondo = new Color(255, 255, 255);
    private Color colorTexto = new Color(50, 50, 50);

    public PanelVentasDelDia(Usuario usuario, Menu menu) {
        this.usuarioLogueado = usuario;
        this.menuPrincipal = menu;
        
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(colorFondo);
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
    }

    // Método que se llama cuando se muestra el panel para refrescar datos
    public void refrescarDatos() {
        cargarHistorial();
    }

    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Ventas del Día");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        JLabel lblSubtitulo = new JLabel("Haz doble clic en una factura para ver los detalles");
        lblSubtitulo.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(Color.GRAY);
        
        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setBackground(colorFondo);
        panelTextos.add(lblTitulo);
        panelTextos.add(lblSubtitulo);
        
        panelNorte.add(panelTextos, BorderLayout.WEST);
        this.add(panelNorte, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "N° FACTURA", "FECHA / HORA", "TOTAL", "MÉTODO PAGO"};
        modeloFacturas = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaFacturas = new JTable(modeloFacturas);
        tablaFacturas.setBackground(Color.WHITE);
        tablaFacturas.setForeground(colorTexto);
        tablaFacturas.setRowHeight(35);
        tablaFacturas.setGridColor(new Color(230, 230, 230));
        tablaFacturas.setSelectionBackground(new Color(200, 220, 255));
        tablaFacturas.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));

        JTableHeader header = tablaFacturas.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        // Evento de doble clic para abrir detalles
        tablaFacturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    abrirDetalles();
                }
            }
        });
        
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBorder(new EmptyBorder(20, 0, 0, 0));
        panelCentro.setBackground(colorFondo);
        panelCentro.add(scrollPane, BorderLayout.CENTER);

        this.add(panelCentro, BorderLayout.CENTER);
    }

    private void cargarHistorial() {
        modeloFacturas.setRowCount(0);
        Ctrl_Dashboard ctrl = new Ctrl_Dashboard();
        
        for (Object[] fila : ctrl.getFacturasDia()) {
            Object[] filaFormato = new Object[5];
            filaFormato[0] = fila[0]; // ID
            filaFormato[1] = fila[1]; // Número Factura
            filaFormato[2] = fila[2]; // Fecha
            filaFormato[3] = "$ " + String.format("%.0f", (double) fila[3]); // Total con formato
            filaFormato[4] = fila[4]; // Método Pago
            modeloFacturas.addRow(filaFormato);
        }
    }

    private void abrirDetalles() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1) return;
        
        int idFactura = (int) tablaFacturas.getValueAt(fila, 0);
        String numFactura = tablaFacturas.getValueAt(fila, 1).toString();
        
        menuPrincipal.mostrarDetalleFactura(idFactura, numFactura);
    }
}
