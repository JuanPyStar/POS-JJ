package vista;

import conexion.conexion;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import modelo.Usuario;

public class PanelHistorialFacturas extends JPanel {

    private Usuario usuarioLogueado;
    private Menu menuPrincipal;
    
    private JTable tablaFacturas;
    private DefaultTableModel modeloFacturas;
    
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorFondo = new Color(255, 255, 255);
    private Color colorTexto = new Color(50, 50, 50);

    public PanelHistorialFacturas(Usuario usuario, Menu menu) {
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
        
        JLabel lblTitulo = new JLabel("Historial de Facturas (Mis Ventas)");
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
        
        // Evento de doble clic para abrir detalles en el mismo panel de contenido
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
        Connection cn = conexion.conectar();
        try {
            // Solo facturas del usuario logueado en el día actual
            String sql = "SELECT f.idFactura, f.numeroFactura, f.fechaFactura, f.totalPagar, p.metodoPago " +
                         "FROM tb_factura f " +
                         "LEFT JOIN tb_pago p ON f.idFactura = p.idFactura " +
                         "WHERE DATE(f.fechaFactura) = CURDATE() AND f.idUsuario = ? " +
                         "ORDER BY f.idFactura DESC";
                         
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, usuarioLogueado.getIdUsuario());
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Object[] fila = new Object[5];
                fila[0] = rs.getInt("idFactura");
                fila[1] = rs.getString("numeroFactura");
                fila[2] = rs.getString("fechaFactura");
                fila[3] = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", rs.getDouble("totalPagar"));
                fila[4] = rs.getString("metodoPago") != null ? rs.getString("metodoPago") : "N/A";
                modeloFacturas.addRow(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al cargar historial: " + e);
        }
    }

    private void abrirDetalles() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1) return;
        
        int idFactura = (int) tablaFacturas.getValueAt(fila, 0);
        String numFactura = tablaFacturas.getValueAt(fila, 1).toString();
        
        // Pasamos la orden al Menu para que configure y muestre el PanelDetalleFactura
        menuPrincipal.mostrarDetalleFactura(idFactura, numFactura);
    }
}
