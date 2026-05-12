package vista;

import controlador.Ctrl_Producto;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import modelo.Producto;

public class PanelProductos extends JPanel {

    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);
    
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalProductos;

    public PanelProductos() {
        this.setBackground(colorFondo);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
        cargarDatosTabla();
    }

    private void inicializarComponentes() {
        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Gestión de Productos");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        JButton btnNuevoProducto = new JButton("+ Nuevo Producto");
        btnNuevoProducto.setBackground(colorAzulPrincipal);
        btnNuevoProducto.setForeground(Color.WHITE);
        btnNuevoProducto.setFocusPainted(false);
        btnNuevoProducto.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        btnNuevoProducto.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        btnNuevoProducto.addActionListener(e -> {
            FrmNuevoProducto frm = new FrmNuevoProducto((Frame) SwingUtilities.getWindowAncestor(this));
            frm.setVisible(true);
            cargarDatosTabla(); // Recargar datos después de cerrar el formulario
        });
        
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(btnNuevoProducto, BorderLayout.EAST);

        // --- WIDGETS ---
        JPanel panelWidgets = new JPanel(new GridLayout(1, 3, 20, 0));
        panelWidgets.setBackground(colorFondo);
        panelWidgets.setPreferredSize(new Dimension(0, 100));
        
        lblTotalProductos = new JLabel("0");
        lblTotalProductos.setForeground(colorAzulPrincipal);
        lblTotalProductos.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 36));
        
        panelWidgets.add(crearWidgetPanel("Total productos", lblTotalProductos));
        panelWidgets.add(crearWidgetTextoPlano("Stock bajo", "0"));
        panelWidgets.add(crearWidgetTextoPlano("Categorías", "1"));

        // --- BUSCADOR ---
        JPanel panelBuscador = new JPanel(new BorderLayout(15, 0));
        panelBuscador.setBackground(colorFondo);
        
        JTextField txtBuscar = new JTextField(" Buscar por nombre, código o categoría...");
        txtBuscar.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        txtBuscar.setBackground(Color.WHITE);
        txtBuscar.setForeground(colorTexto);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorAzulPrincipal, 2),
                new EmptyBorder(5, 10, 5, 10)
        ));
        txtBuscar.setPreferredSize(new Dimension(0, 45));
        
        JComboBox<String> cbCategorias = new JComboBox<>(new String[]{"Todas las categorías", "General"});
        cbCategorias.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        cbCategorias.setBackground(Color.WHITE);
        cbCategorias.setForeground(colorTexto);
        cbCategorias.setPreferredSize(new Dimension(250, 45));
        
        panelBuscador.add(txtBuscar, BorderLayout.CENTER);
        panelBuscador.add(cbCategorias, BorderLayout.EAST);

        // --- PANEL NORTE (Header + Widgets + Buscador) ---
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBackground(colorFondo);
        
        panelNorte.add(panelHeader);
        panelNorte.add(Box.createRigidArea(new Dimension(0, 20)));
        panelNorte.add(panelWidgets);
        panelNorte.add(Box.createRigidArea(new Dimension(0, 20)));
        panelNorte.add(panelBuscador);
        
        this.add(panelNorte, BorderLayout.NORTH);

        // --- TABLA DE PRODUCTOS ---
        String[] columnas = {"ID", "NOMBRE", "PRECIO", "STOCK", "CATEGORÍA", "ESTADO", "ACCIONES"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evitar que se edite directamente en la celda
            }
        };
        
        JTable tabla = new JTable(modeloTabla);
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(colorTexto);
        tabla.setRowHeight(40);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setSelectionForeground(Color.BLACK);
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

    public void cargarDatosTabla() {
        // Limpiar la tabla
        modeloTabla.setRowCount(0);
        
        Ctrl_Producto ctrl = new Ctrl_Producto();
        List<Producto> lista = ctrl.obtenerTodos();
        
        lblTotalProductos.setText(String.valueOf(lista.size()));
        
        for (Producto p : lista) {
            Object[] fila = new Object[7];
            fila[0] = p.getIdProducto();
            fila[1] = p.getNombre();
            fila[2] = "$ " + p.getPrecio();
            fila[3] = p.getCantidad();
            fila[4] = p.getIdCategoria(); // Temporal, deberia cruzar con tabla categoria
            fila[5] = p.getEstado() == 1 ? "Activo" : "Inactivo";
            fila[6] = "Editar / Borrar";
            
            modeloTabla.addRow(fila);
        }
    }

    private JPanel crearWidgetPanel(String titulo, JLabel lblValor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 255), 2, true),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(colorTexto);
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 16));

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel crearWidgetTextoPlano(String titulo, String valor) {
        JLabel lbl = new JLabel(valor);
        lbl.setForeground(colorAzulPrincipal);
        lbl.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 36));
        return crearWidgetPanel(titulo, lbl);
    }
}
