package vista;

import controlador.Ctrl_Factura;
import controlador.Ctrl_Producto;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import modelo.DetalleFactura;
import modelo.Factura;
import modelo.Producto;
import modelo.Usuario;

public class PanelVentas extends JPanel {

    private Usuario usuario;
    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);
    private Color colorVerde = new Color(46, 204, 113);
    private Color colorRojo = new Color(231, 76, 60);
    private Color colorGrisClaro = new Color(245, 245, 245);

    private JTextField txtBuscarProd;
    private JTable tablaBuscar;
    private DefaultTableModel modeloBuscar;
    
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    
    private JLabel lblSubtotalValor;
    private JLabel lblIvaValor;
    private JLabel lblTotalPagar;
    private JComboBox<String> cbMetodoPago;

    private double subtotalG = 0.0;
    private double ivaG = 0.0;
    private double totalG = 0.0;

    // Lista en memoria para guardar el carrito y mandarlo a la BD
    private List<DetalleFactura> detallesCarrito;

    public PanelVentas(Usuario usuario) {
        this.usuario = usuario;
        this.detallesCarrito = new ArrayList<>();
        
        this.setBackground(colorFondo);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
        cargarProductosBusqueda(""); 
    }

    private void inicializarComponentes() {
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Punto de Venta");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);

        panelHeader.add(lblTitulo, BorderLayout.WEST);
        this.add(panelHeader, BorderLayout.NORTH);

        JPanel panelSplit = new JPanel(new BorderLayout(20, 0));
        panelSplit.setBackground(colorFondo);

        // --- IZQUIERDA ---
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 15));
        panelIzquierdo.setBackground(colorFondo);
        
        txtBuscarProd = new JTextField(" Buscar por nombre...");
        txtBuscarProd.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        txtBuscarProd.setBackground(Color.WHITE);
        txtBuscarProd.setForeground(colorTexto);
        txtBuscarProd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorAzulPrincipal, 2),
                new EmptyBorder(5, 10, 5, 10)
        ));
        txtBuscarProd.setPreferredSize(new Dimension(0, 45));
        
        txtBuscarProd.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                cargarProductosBusqueda(txtBuscarProd.getText().replace(" Buscar por nombre...", "").trim());
            }
        });

        txtBuscarProd.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtBuscarProd.getText().equals(" Buscar por nombre...")) {
                    txtBuscarProd.setText("");
                }
            }
        });
        
        String[] colBuscar = { "ID", "PRODUCTO", "PRECIO", "STOCK", "IVA", "QUITAR", "AÑADIR" };
        modeloBuscar = new DefaultTableModel(null, colBuscar) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaBuscar = crearTabla(modeloBuscar);
        // Ajustar columnas
        tablaBuscar.getColumnModel().getColumn(0).setPreferredWidth(30);  // ID
        tablaBuscar.getColumnModel().getColumn(1).setPreferredWidth(140); // PRODUCTO
        tablaBuscar.getColumnModel().getColumn(2).setPreferredWidth(70);  // PRECIO
        tablaBuscar.getColumnModel().getColumn(3).setPreferredWidth(50);  // STOCK
        tablaBuscar.getColumnModel().getColumn(4).setPreferredWidth(40);  // IVA
        tablaBuscar.getColumnModel().getColumn(5).setPreferredWidth(50);  // QUITAR (-)
        tablaBuscar.getColumnModel().getColumn(6).setPreferredWidth(60);  // AÑADIR (+)

        JScrollPane scrollBuscar = new JScrollPane(tablaBuscar);
        scrollBuscar.getViewport().setBackground(Color.WHITE);
        scrollBuscar.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        tablaBuscar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaBuscar.rowAtPoint(e.getPoint());
                int col = tablaBuscar.columnAtPoint(e.getPoint());
                if (fila >= 0) {
                    if (col == 5) { // QUITAR (-)
                        restarDesdeBuscador(fila);
                    } else if (col == 6) { // AÑADIR (+)
                        agregarAlCarrito(fila);
                    }
                }
            }
        });

        panelIzquierdo.add(txtBuscarProd, BorderLayout.NORTH);
        panelIzquierdo.add(scrollBuscar, BorderLayout.CENTER);

        // --- DERECHA ---
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 15));
        panelDerecho.setBackground(colorGrisClaro);
        panelDerecho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        panelDerecho.setPreferredSize(new Dimension(480, 0));

        JLabel lblCarrito = new JLabel("Factura Actual");
        lblCarrito.setForeground(colorAzulPrincipal);
        lblCarrito.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 20));

        String[] colCarrito = {"ID", "CANT", "PRODUCTO", "SUBTOTAL", "-", "+"};
        modeloCarrito = new DefaultTableModel(null, colCarrito) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCarrito = crearTabla(modeloCarrito);
        tablaCarrito.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        tablaCarrito.getColumnModel().getColumn(1).setPreferredWidth(40); // CANT
        tablaCarrito.getColumnModel().getColumn(2).setPreferredWidth(120); // PRODUCTO
        tablaCarrito.getColumnModel().getColumn(3).setPreferredWidth(80); // SUBTOTAL
        tablaCarrito.getColumnModel().getColumn(4).setPreferredWidth(40); // -
        tablaCarrito.getColumnModel().getColumn(5).setPreferredWidth(40); // +

        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.getViewport().setBackground(colorGrisClaro);
        scrollCarrito.setBorder(BorderFactory.createEmptyBorder());

        tablaCarrito.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaCarrito.rowAtPoint(e.getPoint());
                int col = tablaCarrito.columnAtPoint(e.getPoint());
                if (fila >= 0) {
                    if (col == 4) { // -
                        eliminarDelCarrito(fila);
                    } else if (col == 5) { // +
                        sumarDesdeCarrito(fila);
                    }
                }
            }
        });

        JPanel panelTotales = new JPanel();
        panelTotales.setLayout(new BoxLayout(panelTotales, BoxLayout.Y_AXIS));
        panelTotales.setBackground(colorGrisClaro);
        
        lblSubtotalValor = new JLabel("$ 0");
        lblIvaValor = new JLabel("$ 0");
        
        panelTotales.add(crearFilaTotal("Subtotal:", lblSubtotalValor));
        panelTotales.add(crearFilaTotal("IVA:", lblIvaValor));
        panelTotales.add(Box.createRigidArea(new Dimension(0, 10)));
        
        lblTotalPagar = new JLabel("Total: $ 0");
        lblTotalPagar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTotalPagar.setForeground(colorVerde);
        lblTotalPagar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panelTotales.add(lblTotalPagar);

        panelTotales.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] metodos = {"Efectivo", "Tarjeta Crédito", "Tarjeta Débito", "Transferencia"};
        cbMetodoPago = new JComboBox<>(metodos);
        cbMetodoPago.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        cbMetodoPago.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelTotales.add(cbMetodoPago);
        
        panelTotales.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnCobrar = new JButton("Confirmar Venta");
        btnCobrar.setBackground(colorVerde);
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 18));
        btnCobrar.setFocusPainted(false);
        btnCobrar.setPreferredSize(new Dimension(0, 50));
        btnCobrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCobrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        btnCobrar.addActionListener(e -> confirmarVenta());
        
        panelTotales.add(btnCobrar);

        panelDerecho.add(lblCarrito, BorderLayout.NORTH);
        panelDerecho.add(scrollCarrito, BorderLayout.CENTER);
        panelDerecho.add(panelTotales, BorderLayout.SOUTH);

        panelSplit.add(panelIzquierdo, BorderLayout.CENTER);
        panelSplit.add(panelDerecho, BorderLayout.EAST);

        this.add(panelSplit, BorderLayout.CENTER);
    }

    private void cargarProductosBusqueda(String filtro) {
        modeloBuscar.setRowCount(0);
        Ctrl_Producto ctrl = new Ctrl_Producto();
        List<Producto> lista = ctrl.buscarProductos(filtro);
        
        for (Producto p : lista) {
            Object[] fila = new Object[7];
            fila[0] = p.getIdProducto();
            fila[1] = p.getNombre();
            fila[2] = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", p.getPrecio());
            fila[3] = p.getCantidad();
            fila[4] = p.getPorcentajeIva() + "%";
            fila[5] = " - ";
            
            // Revisar cuántos de este producto ya están en el carrito
            int cantidadEnCarrito = 0;
            for (DetalleFactura d : detallesCarrito) {
                if (d.getIdProducto() == p.getIdProducto()) {
                    cantidadEnCarrito = d.getCantidad();
                    break;
                }
            }
            if (cantidadEnCarrito > 0) {
                fila[6] = "+ (" + cantidadEnCarrito + ")";
            } else {
                fila[6] = "+";
            }
            
            modeloBuscar.addRow(fila);
        }
    }

    private void agregarAlCarrito(int filaSelec) {
        int idProd = (int) tablaBuscar.getValueAt(filaSelec, 0);
        String nombre = tablaBuscar.getValueAt(filaSelec, 1).toString();
        // Limpiar el formato de precio de "$ 4.500" a "4500" para cálculos
        String precioStr = tablaBuscar.getValueAt(filaSelec, 2).toString().replace("$ ", "").replace(".", "").replace(",", "");
        double precio = Double.parseDouble(precioStr);
        int stock = (int) tablaBuscar.getValueAt(filaSelec, 3);
        String ivaStr = tablaBuscar.getValueAt(filaSelec, 4).toString().replace("%", "");
        int ivaPorcentaje = Integer.parseInt(ivaStr);

        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "Producto agotado (Stock 0).");
            return;
        }

        int cantAAgregar = 1;

        // Comprobar si ya existe en el carrito
        int indexExistente = -1;
        int cantidadEnCarrito = 0;
        for (int i = 0; i < detallesCarrito.size(); i++) {
            if (detallesCarrito.get(i).getIdProducto() == idProd) {
                indexExistente = i;
                cantidadEnCarrito = detallesCarrito.get(i).getCantidad();
                break;
            }
        }

        if ((cantAAgregar + cantidadEnCarrito) > stock) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente.");
            return;
        }

        if (indexExistente != -1) {
            // Ya existe, sumar 1
            DetalleFactura detalle = detallesCarrito.get(indexExistente);
            int nuevaCant = detalle.getCantidad() + 1;
            double nuevoSubtotal = nuevaCant * precio;
            double nuevoIvaProd = nuevoSubtotal * (ivaPorcentaje / 100.0);
            double nuevoTotal = nuevoSubtotal + nuevoIvaProd;
            
            detalle.setCantidad(nuevaCant);
            detalle.setSubtotal(nuevoSubtotal);
            detalle.setIva(nuevoIvaProd);
            detalle.setTotal(nuevoTotal);
            
            // Actualizar tabla visual del carrito
            modeloCarrito.setValueAt(nuevaCant, indexExistente, 1);
            modeloCarrito.setValueAt(String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", nuevoSubtotal), indexExistente, 3);
        } else {
            // Producto nuevo en el carrito
            double subtotal = cantAAgregar * precio;
            double ivaProd = subtotal * (ivaPorcentaje / 100.0);
            double totalDetalle = subtotal + ivaProd;

            DetalleFactura detalle = new DetalleFactura();
            detalle.setIdProducto(idProd);
            detalle.setCantidad(cantAAgregar);
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);
            detalle.setDescuento(0);
            detalle.setIva(ivaProd);
            detalle.setTotal(totalDetalle);
            detallesCarrito.add(detalle);

            Object[] filaC = { idProd, cantAAgregar, nombre, String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", subtotal), " - ", " + " };
            modeloCarrito.addRow(filaC);
        }

        // Actualizar el texto del "+" en la tabla de búsqueda
        int totalAhora = cantidadEnCarrito + 1;
        tablaBuscar.setValueAt("+ (" + totalAhora + ")", filaSelec, 6);

        recalcularTotalesDesdeLista();
    }

    private void restarDesdeBuscador(int filaSelecBuscador) {
        int idProd = (int) tablaBuscar.getValueAt(filaSelecBuscador, 0);
        
        // Buscar este producto en el carrito
        int filaEnCarrito = -1;
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            if ((int) modeloCarrito.getValueAt(i, 0) == idProd) {
                filaEnCarrito = i;
                break;
            }
        }
        
        if (filaEnCarrito != -1) {
            eliminarDelCarrito(filaEnCarrito);
        }
    }
    
    private void sumarDesdeCarrito(int filaSelecCarrito) {
        int idProd = (int) tablaCarrito.getValueAt(filaSelecCarrito, 0);
        
        // Buscar este producto en el buscador para simular clic en +
        for (int i = 0; i < modeloBuscar.getRowCount(); i++) {
            if ((int) modeloBuscar.getValueAt(i, 0) == idProd) {
                agregarAlCarrito(i);
                return;
            }
        }
    }

    private void eliminarDelCarrito(int filaSelec) {
        DetalleFactura detalle = detallesCarrito.get(filaSelec);
        
        int nuevaCant = detalle.getCantidad() - 1;
        
        if (nuevaCant <= 0) {
            // Si llega a 0, se elimina completamente del carrito
            detallesCarrito.remove(filaSelec);
            modeloCarrito.removeRow(filaSelec);
            
            // Refrescar buscador para quitar el (+x)
            actualizarFilaBuscadorSiExiste(detalle.getIdProducto(), 0);
        } else {
            // Si aún queda, solo restamos 1 y actualizamos valores
            double precio = detalle.getPrecioUnitario();
            double nuevoSubtotal = nuevaCant * precio;
            
            // Recuperar el porcentaje original matemáticamente (evitar cero de división, etc.)
            // Es mejor recalcular, o usar el que calculamos. 
            // Como el iva se calculó como subtotal * ivaPct, sacamos el porcentaje
            double ivaPct = (detalle.getIva() / detalle.getSubtotal()); 
            double nuevoIvaProd = nuevoSubtotal * ivaPct;
            double nuevoTotal = nuevoSubtotal + nuevoIvaProd;
            
            detalle.setCantidad(nuevaCant);
            detalle.setSubtotal(nuevoSubtotal);
            detalle.setIva(nuevoIvaProd);
            detalle.setTotal(nuevoTotal);
            
            modeloCarrito.setValueAt(nuevaCant, filaSelec, 1);
            modeloCarrito.setValueAt(String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", nuevoSubtotal), filaSelec, 3);
            
            actualizarFilaBuscadorSiExiste(detalle.getIdProducto(), nuevaCant);
        }
        
        recalcularTotalesDesdeLista();
    }
    
    // Para que al quitar del carrito baje el número de (+3) a (+2) en el buscador
    private void actualizarFilaBuscadorSiExiste(int idProducto, int cantEnCarrito) {
        for (int i = 0; i < modeloBuscar.getRowCount(); i++) {
            int idBusqueda = (int) modeloBuscar.getValueAt(i, 0);
            if (idBusqueda == idProducto) {
                if (cantEnCarrito > 0) {
                    modeloBuscar.setValueAt("+ (" + cantEnCarrito + ")", i, 6);
                } else {
                    modeloBuscar.setValueAt("+", i, 6);
                }
                break;
            }
        }
    }
    
    private void recalcularTotalesDesdeLista() {
        subtotalG = 0;
        ivaG = 0;
        totalG = 0;
        
        for (DetalleFactura d : detallesCarrito) {
            subtotalG += d.getSubtotal();
            ivaG += d.getIva();
            totalG += d.getTotal();
        }
        
        lblSubtotalValor.setText(String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", subtotalG));
        lblIvaValor.setText(String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", ivaG));
        lblTotalPagar.setText(String.format(java.util.Locale.forLanguageTag("es-CO"), "Total: $ %,.0f", totalG));
    }

    private void confirmarVenta() {
        if (detallesCarrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío. Agregue productos.");
            return;
        }

        Factura factura = new Factura();
        factura.setNumeroFactura("POS-" + System.currentTimeMillis());
        factura.setIdCliente(0);
        factura.setIdUsuario(usuario.getIdUsuario());
        factura.setSubtotal(subtotalG);
        factura.setTotalIva(ivaG);
        factura.setTotalPagar(totalG);

        String metodo = cbMetodoPago.getSelectedItem().toString();
        Ctrl_Factura ctrl = new Ctrl_Factura();
        boolean exito = ctrl.guardarVenta(factura, detallesCarrito, metodo);

        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Venta registrada con éxito!\nTotal cobrado: " + String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", totalG));
            
            detallesCarrito.clear();
            modeloCarrito.setRowCount(0);
            recalcularTotalesDesdeLista();
            
            cargarProductosBusqueda("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la venta. Intente nuevamente.");
        }
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(colorTexto);
        tabla.setRowHeight(35);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));

        JTableHeader header = tabla.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 40));
        
        return tabla;
    }

    private JPanel crearFilaTotal(String texto, JLabel lblValor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorGrisClaro);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setForeground(colorTexto);
        lblTexto.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        
        lblValor.setForeground(colorTexto);
        lblValor.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        
        panel.add(lblTexto, BorderLayout.WEST);
        panel.add(lblValor, BorderLayout.EAST);
        
        return panel;
    }
}
