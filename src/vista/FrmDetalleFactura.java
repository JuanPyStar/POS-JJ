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

public class FrmDetalleFactura extends JDialog {

    private int idFactura;
    private String numFactura;
    
    private JTable tablaDetalles;
    private DefaultTableModel modeloDetalles;
    
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorFondo = new Color(255, 255, 255);
    private Color colorTexto = new Color(50, 50, 50);

    public FrmDetalleFactura(JDialog parent, int idFactura, String numFactura) {
        super(parent, "Detalle de Venta", true);
        this.idFactura = idFactura;
        this.numFactura = numFactura;
        
        this.setSize(700, 450);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout(10, 10));
        this.getContentPane().setBackground(colorFondo);

        inicializarComponentes();
        cargarDetalles();
    }

    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(colorFondo);
        panelNorte.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("Factura: " + numFactura);
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 22));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        panelNorte.add(lblTitulo, BorderLayout.WEST);
        this.add(panelNorte, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"PRODUCTO", "CANTIDAD", "P. UNITARIO", "SUBTOTAL"};
        modeloDetalles = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaDetalles = new JTable(modeloDetalles);
        tablaDetalles.setBackground(Color.WHITE);
        tablaDetalles.setForeground(colorTexto);
        tablaDetalles.setRowHeight(35);
        tablaDetalles.setGridColor(new Color(230, 230, 230));
        tablaDetalles.setSelectionBackground(new Color(200, 220, 255));
        tablaDetalles.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));

        JTableHeader header = tablaDetalles.getTableHeader();
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(tablaDetalles);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBorder(new EmptyBorder(0, 20, 20, 20));
        panelCentro.setBackground(colorFondo);
        panelCentro.add(scrollPane, BorderLayout.CENTER);

        this.add(panelCentro, BorderLayout.CENTER);
        
        // Panel Sur (Botón Imprimir)
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setBackground(colorFondo);
        panelSur.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton btnImprimir = new JButton("Imprimir Factura");
        btnImprimir.setBackground(new Color(46, 204, 113)); // Verde
        btnImprimir.setForeground(Color.WHITE);
        btnImprimir.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        btnImprimir.setPreferredSize(new Dimension(250, 45));
        btnImprimir.setFocusPainted(false);
        
        btnImprimir.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Generando e imprimiendo el Ticket de la factura " + numFactura + "...");
        });
        
        panelSur.add(btnImprimir);
        this.add(panelSur, BorderLayout.SOUTH);
    }

    private void cargarDetalles() {
        Connection cn = conexion.conectar();
        try {
            String sql = "SELECT p.nombre, d.cantidad, d.precioUnitario, d.subtotal " +
                         "FROM tb_detalle_factura d " +
                         "INNER JOIN tb_producto p ON d.idProducto = p.idProducto " +
                         "WHERE d.idFactura = ?";
                         
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idFactura);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getString("nombre");
                fila[1] = rs.getInt("cantidad");
                fila[2] = "$ " + rs.getDouble("precioUnitario");
                fila[3] = "$ " + rs.getDouble("subtotal");
                modeloDetalles.addRow(fila);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al cargar detalles de factura: " + e);
        }
    }
}
