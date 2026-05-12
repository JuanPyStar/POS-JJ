package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelReportes extends JPanel {

    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);

    public PanelReportes() {
        this.setBackground(colorFondo);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Reportes y Estadísticas");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        JButton btnExportar = new JButton("Exportar a PDF");
        btnExportar.setBackground(new Color(231, 76, 60)); // Rojo PDF
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        btnExportar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(btnExportar, BorderLayout.EAST);

        // --- FILTROS (Tipos de reporte) ---
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelFiltros.setBackground(colorFondo);
        panelFiltros.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel lblTipo = new JLabel("Tipo de Reporte:");
        lblTipo.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        
        JComboBox<String> cbTipoReporte = new JComboBox<>(new String[]{
            "Ventas Diarias", 
            "Ventas Mensuales", 
            "Productos Más Vendidos",
            "Historial Completo"
        });
        cbTipoReporte.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        
        JButton btnGenerar = new JButton("Generar");
        btnGenerar.setBackground(colorAzulPrincipal);
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        
        panelFiltros.add(lblTipo);
        panelFiltros.add(cbTipoReporte);
        panelFiltros.add(btnGenerar);

        // --- PANEL NORTE ---
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBackground(colorFondo);
        
        panelNorte.add(panelHeader);
        panelNorte.add(panelFiltros);
        
        this.add(panelNorte, BorderLayout.NORTH);

        // --- TABLA DE RESULTADOS ---
        String[] columnas = {"ID FACTURA", "FECHA", "CLIENTE", "TOTAL", "VENDEDOR", "MÉTODO PAGO"};
        Object[][] datos = new Object[0][0];

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas);
        JTable tabla = new JTable(modelo);
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(colorTexto);
        tabla.setRowHeight(40);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));

        JTableHeader header = tabla.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 45));

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        this.add(scrollPane, BorderLayout.CENTER);
    }
}
