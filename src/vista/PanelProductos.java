package vista;

import controlador.Ctrl_Categoria;
import controlador.Ctrl_Producto;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import modelo.Categoria;
import modelo.Producto;

public class PanelProductos extends JPanel {

    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);
    
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalProductos;
    private JLabel lblStockBajo;
    private JLabel lblTotalCategorias;
    private JTextField txtBuscar;
    private JComboBox<Object> cbCategorias;
    private JTable tablaProductos;
    private JButton btnEditarProducto;
    private JButton btnEliminarProducto;

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
        
        lblStockBajo = new JLabel("0");
        lblStockBajo.setForeground(new Color(231, 76, 60));
        lblStockBajo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 36));
        
        lblTotalCategorias = new JLabel("0");
        lblTotalCategorias.setForeground(colorAzulPrincipal);
        lblTotalCategorias.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 36));
        
        panelWidgets.add(crearWidgetPanel("Total productos", lblTotalProductos));
        panelWidgets.add(crearWidgetPanel("Stock bajo", lblStockBajo));
        panelWidgets.add(crearWidgetPanel("Categorías", lblTotalCategorias));

        // --- BUSCADOR ---
        JPanel panelBuscador = new JPanel(new BorderLayout(15, 0));
        panelBuscador.setBackground(colorFondo);
        
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        txtBuscar.setBackground(Color.WHITE);
        txtBuscar.setForeground(colorTexto);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorAzulPrincipal, 2),
                new EmptyBorder(5, 10, 5, 10)
        ));
        txtBuscar.setPreferredSize(new Dimension(0, 45));
        txtBuscar.setToolTipText("Buscar por nombre, descripción o categoría");

        cbCategorias = new JComboBox<>();
        cbCategorias.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        cbCategorias.setBackground(Color.WHITE);
        cbCategorias.setForeground(colorTexto);
        cbCategorias.setPreferredSize(new Dimension(250, 45));
        cargarCategorias();

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(colorAzulPrincipal);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnBuscar.addActionListener(e -> cargarDatosTabla());

        btnEditarProducto = new JButton("Editar");
        btnEditarProducto.setBackground(new Color(52, 152, 219));
        btnEditarProducto.setForeground(Color.WHITE);
        btnEditarProducto.setFocusPainted(false);
        btnEditarProducto.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnEditarProducto.addActionListener(e -> editarProductoSeleccionado());

        btnEliminarProducto = new JButton("Eliminar");
        btnEliminarProducto.setBackground(new Color(231, 76, 60));
        btnEliminarProducto.setForeground(Color.WHITE);
        btnEliminarProducto.setFocusPainted(false);
        btnEliminarProducto.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnEliminarProducto.addActionListener(e -> eliminarProductoSeleccionado());

        panelBuscador.add(txtBuscar, BorderLayout.CENTER);
        JPanel panelDerecha = new JPanel();
        panelDerecha.setLayout(new BoxLayout(panelDerecha, BoxLayout.Y_AXIS));
        panelDerecha.setBackground(colorFondo);
        panelDerecha.add(cbCategorias);
        panelDerecha.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 0));
        panelBotones.setBackground(colorFondo);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnEditarProducto);
        panelBotones.add(btnEliminarProducto);
        panelDerecha.add(panelBotones);
        panelBuscador.add(panelDerecha, BorderLayout.EAST);

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
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setBackground(Color.WHITE);
        tablaProductos.setForeground(colorTexto);
        tablaProductos.setRowHeight(40);
        tablaProductos.setGridColor(new Color(230, 230, 230));
        tablaProductos.setSelectionBackground(new Color(200, 220, 255));
        tablaProductos.setSelectionForeground(Color.BLACK);
        tablaProductos.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editarProductoSeleccionado();
                }
            }
        });

        JTableHeader header = tablaProductos.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 45));

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void cargarDatosTabla() {
        // Limpiar la tabla
        modeloTabla.setRowCount(0);
        
        Ctrl_Producto ctrl = new Ctrl_Producto();
        String filtro = txtBuscar != null ? txtBuscar.getText() : "";
        int categoriaId = 0;
        Object seleccionado = cbCategorias != null ? cbCategorias.getSelectedItem() : null;
        if (seleccionado instanceof Categoria) {
            categoriaId = ((Categoria) seleccionado).getIdCategoria();
        }

        List<Object[]> datos = ctrl.buscarProductos(filtro, categoriaId);
        List<Object[]> stockBajo = ctrl.obtenerStockBajo();
        int totalCategorias = ctrl.getTotalCategorias();
        
        lblTotalProductos.setText(String.valueOf(datos.size()));
        lblStockBajo.setText(String.valueOf(stockBajo.size()));
        lblTotalCategorias.setText(String.valueOf(totalCategorias));
        
        for (Object[] fila : datos) {
            Object[] filaTabla = new Object[7];
            filaTabla[0] = fila[0]; // idProducto
            filaTabla[1] = fila[1]; // nombre
            filaTabla[2] = fila[3]; // precio
            filaTabla[3] = fila[2]; // cantidad
            filaTabla[4] = fila[4]; // categoría
            filaTabla[5] = fila[6]; // estado
            filaTabla[6] = "Editar / Borrar";
            
            modeloTabla.addRow(filaTabla);
        }
    }

    private void cargarCategorias() {
        cbCategorias.removeAllItems();
        cbCategorias.addItem("Todas las categorías");
        Ctrl_Categoria ctrl = new Ctrl_Categoria();
        for (Categoria categoria : ctrl.obtenerTodas()) {
            cbCategorias.addItem(categoria);
        }
    }

    private void editarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para editar.");
            return;
        }
        int idProducto = (int) modeloTabla.getValueAt(fila, 0);
        Ctrl_Producto ctrl = new Ctrl_Producto();
        Producto p = ctrl.obtenerPorId(idProducto);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el producto seleccionado.");
            return;
        }
        FrmNuevoProducto frm = new FrmNuevoProducto((Frame) SwingUtilities.getWindowAncestor(this), p);
        frm.setVisible(true);
        cargarDatosTabla();
    }

    private void eliminarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.");
            return;
        }
        int idProducto = (int) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar este producto?", "Eliminar producto", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        Ctrl_Producto ctrl = new Ctrl_Producto();
        if (ctrl.eliminar(idProducto)) {
            JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
            cargarDatosTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el producto.");
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
