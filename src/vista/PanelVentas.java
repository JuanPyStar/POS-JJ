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
    private JButton btnAbrirTurno;
    private JButton btnCerrarTurno;

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

        // Cargar estado inicial del turno (Asíncrono)
        btnAbrirTurno = new JButton("Abrir Turno");
        btnAbrirTurno.setBackground(new Color(0, 153, 51)); // Verde
        btnAbrirTurno.setForeground(Color.WHITE);
        btnAbrirTurno.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnAbrirTurno.setFocusPainted(false);
        btnAbrirTurno.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btnAbrirTurno.setEnabled(false); // Deshabilitado por defecto hasta cargar estado
        
        btnCerrarTurno = new JButton("Cerrar Turno");
        btnCerrarTurno.setBackground(new Color(204, 0, 0)); // Rojo
        btnCerrarTurno.setForeground(Color.WHITE);
        btnCerrarTurno.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnCerrarTurno.setFocusPainted(false);
        btnCerrarTurno.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btnCerrarTurno.setEnabled(false);

        controlador.Ctrl_Turno ctrlTurno = new controlador.Ctrl_Turno();
        javax.swing.SwingWorker<modelo.Turno, Void> workerTurno = new javax.swing.SwingWorker<modelo.Turno, Void>() {
            @Override
            protected modelo.Turno doInBackground() throws Exception {
                return ctrlTurno.getTurnoActivo();
            }
            @Override
            protected void done() {
                try {
                    modelo.Turno turnoActivo = get();
                    if (turnoActivo != null) {
                        btnAbrirTurno.setEnabled(false);
                        btnCerrarTurno.setEnabled(true);
                    } else {
                        btnAbrirTurno.setEnabled(true);
                        btnCerrarTurno.setEnabled(false);
                    }
                } catch (Exception e) {}
            }
        };
        workerTurno.execute();

        btnAbrirTurno.addActionListener(e -> {
            vista.FrmTurno frm = new vista.FrmTurno(null, true, usuario.getIdUsuario());
            frm.setVisible(true);
            // Actualizar botones después de cerrar la ventana
            if (ctrlTurno.getTurnoActivo() != null) {
                btnAbrirTurno.setEnabled(false);
                btnCerrarTurno.setEnabled(true);
            }
        });

        btnCerrarTurno.addActionListener(e -> {
            vista.FrmTurno frm = new vista.FrmTurno(null, true, usuario.getIdUsuario());
            frm.setVisible(true);
            // Actualizar botones después de cerrar la ventana
            if (ctrlTurno.getTurnoActivo() == null) {
                btnAbrirTurno.setEnabled(true);
                btnCerrarTurno.setEnabled(false);
            }
        });

        JPanel pnlIzquierdoHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlIzquierdoHeader.setBackground(colorFondo);
        pnlIzquierdoHeader.add(lblTitulo);
        pnlIzquierdoHeader.add(btnAbrirTurno);
        pnlIzquierdoHeader.add(btnCerrarTurno);

        panelHeader.add(pnlIzquierdoHeader, BorderLayout.WEST);
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
        panelDerecho.setPreferredSize(new Dimension(580, 0));

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
        lblTotalPagar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 36));
        lblTotalPagar.setForeground(colorVerde);
        lblTotalPagar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panelTotales.add(lblTotalPagar);

        panelTotales.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnCobrar = new JButton("Pagar Venta");
        btnCobrar.setBackground(colorVerde);
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 24));
        btnCobrar.setFocusPainted(false);
        btnCobrar.setPreferredSize(new Dimension(0, 65));
        btnCobrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCobrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        
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
        Object[] filaCarga = new Object[]{"", "Cargando...", "", "", "", "", ""};
        modeloBuscar.addRow(filaCarga);
        
        javax.swing.SwingWorker<List<Producto>, Void> worker = new javax.swing.SwingWorker<List<Producto>, Void>() {
            @Override
            protected List<Producto> doInBackground() throws Exception {
                Ctrl_Producto ctrl = new Ctrl_Producto();
                return ctrl.buscarProductos(filtro);
            }

            @Override
            protected void done() {
                try {
                    List<Producto> lista = get();
                    modeloBuscar.setRowCount(0);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void agregarAlCarrito(int filaSelec) {
        if (btnAbrirTurno != null && btnAbrirTurno.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Debe abrir un turno antes de poder registrar productos.", "Turno Cerrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

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
        if (btnAbrirTurno != null && btnAbrirTurno.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Debe abrir un turno antes de realizar una venta.", "Turno Cerrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (detallesCarrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío. Agregue productos.");
            return;
        }
        
        String[] pagoData = mostrarDialogoCobro(totalG);
        
        if (pagoData[0] == null) {
            return; // El usuario canceló o no completó el pago
        }
        
        // Si no es efectivo, concatenamos la referencia obligatoria para que quede en el historial
        String metodo = pagoData[0];
        if (!metodo.equals("Efectivo")) {
            metodo = metodo + " (Ref: " + pagoData[1] + ")";
        }

        Factura factura = new Factura();
        factura.setNumeroFactura("POS-" + System.currentTimeMillis());
        factura.setIdCliente(0);
        factura.setIdUsuario(usuario.getIdUsuario());
        factura.setSubtotal(subtotalG);
        factura.setTotalIva(ivaG);
        factura.setTotalPagar(totalG);

        Ctrl_Factura ctrl = new Ctrl_Factura();
        int idFacturaGenerada = ctrl.guardarVenta(factura, detallesCarrito, metodo);

        if (idFacturaGenerada > 0) {
            String totalCobrado = String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", totalG);
            
            // Imprimir de inmediato (sin preguntar)
            controlador.Ctrl_Impresora.imprimirFactura(idFacturaGenerada);
            
            // Mostrar la alerta obligatoria de venta exitosa justo DESPUÉS de que se termine de imprimir o guardar
            JOptionPane.showMessageDialog(this,
                    "¡VENTA REGISTRADA CON ÉXITO!\n\nTotal cobrado: " + totalCobrado,
                    "Venta Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            
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
        tabla.setRowHeight(45);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));

        JTableHeader header = tabla.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(0, 50));
        
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

    private String[] mostrarDialogoCobro(double totalAPagar) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Procesar Pago", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTotal = new JLabel("Total a cobrar: " + String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", totalAPagar));
        lblTotal.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 32));
        lblTotal.setForeground(new Color(0, 153, 51));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> comboMetodos = new JComboBox<>(new String[]{"Efectivo", "Tarjeta Crédito", "Tarjeta Débito", "Transferencia"});
        comboMetodos.setFont(new Font("Yu Gothic UI", Font.PLAIN, 20));
        comboMetodos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel panelDinamico = new JPanel(new CardLayout());
        panelDinamico.setBorder(BorderFactory.createTitledBorder("Detalles del Pago"));
        
        // --- Panel Efectivo ---
        JPanel pnlEfectivo = new JPanel(new GridLayout(2, 2, 10, 15));
        JLabel lblRecibido = new JLabel("Efectivo Recibido:");
        lblRecibido.setFont(new Font("Yu Gothic UI", Font.PLAIN, 18));
        JTextField txtRecibido = new JTextField();
        txtRecibido.setFont(new Font("Yu Gothic UI", Font.BOLD, 22));
        
        JLabel lblCambioTxt = new JLabel("Cambio a devolver:");
        lblCambioTxt.setFont(new Font("Yu Gothic UI", Font.BOLD, 18));
        JLabel lblCambio = new JLabel("$ 0");
        lblCambio.setForeground(Color.RED);
        lblCambio.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 24));
        
        pnlEfectivo.add(lblRecibido);
        pnlEfectivo.add(txtRecibido);
        pnlEfectivo.add(lblCambioTxt);
        pnlEfectivo.add(lblCambio);

        // --- Panel Referencia ---
        JPanel pnlReferencia = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel lblRef = new JLabel("N° Aprobación / Referencia (Obligatorio):");
        lblRef.setFont(new Font("Yu Gothic UI", Font.PLAIN, 18));
        lblRef.setForeground(new Color(204, 0, 0)); // Resaltar en rojo que es obligatorio
        JTextField txtReferencia = new JTextField();
        txtReferencia.setFont(new Font("Yu Gothic UI", Font.BOLD, 20));
        pnlReferencia.add(lblRef);
        pnlReferencia.add(txtReferencia);

        panelDinamico.add(pnlEfectivo, "Efectivo");
        panelDinamico.add(pnlReferencia, "Referencia");

        CardLayout cl = (CardLayout) panelDinamico.getLayout();

        comboMetodos.addActionListener(e -> {
            if (comboMetodos.getSelectedItem().toString().equals("Efectivo")) {
                cl.show(panelDinamico, "Efectivo");
                txtRecibido.requestFocus();
            } else {
                cl.show(panelDinamico, "Referencia");
                txtReferencia.requestFocus();
            }
        });

        // Evento para calcular cambio en tiempo real y auto-formatear a miles
        txtRecibido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                // 1. Quitar caracteres no numéricos y autoformatear el texto visible
                String textoPuro = txtRecibido.getText().replaceAll("[^0-9]", "");
                if (!textoPuro.isEmpty()) {
                    try {
                        double valor = Double.parseDouble(textoPuro);
                        String textoFormateado = String.format(java.util.Locale.forLanguageTag("es-CO"), "%,.0f", valor);
                        txtRecibido.setText(textoFormateado);
                    } catch (Exception ex) {}
                } else {
                    txtRecibido.setText("");
                }

                // 2. Calcular cambio usando siempre el texto numérico puro
                try {
                    double recibido = Double.parseDouble(textoPuro);
                    double cambio = recibido - totalAPagar;
                    if (cambio >= 0) {
                        lblCambio.setText(String.format(java.util.Locale.forLanguageTag("es-CO"), "$ %,.0f", cambio));
                        lblCambio.setForeground(new Color(0, 153, 51));
                    } else {
                        lblCambio.setText("Falta dinero");
                        lblCambio.setForeground(Color.RED);
                    }
                } catch (NumberFormatException ex) {
                    lblCambio.setText("$ 0");
                    lblCambio.setForeground(Color.RED);
                }
            }
        });

        panelCentro.add(lblTotal);
        panelCentro.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel lblSelec = new JLabel("Método de Pago:");
        lblSelec.setFont(new Font("Yu Gothic UI", Font.PLAIN, 18));
        panelCentro.add(lblSelec);
        panelCentro.add(comboMetodos);
        panelCentro.add(Box.createRigidArea(new Dimension(0, 20)));
        panelCentro.add(panelDinamico);

        // Botones Inferiores
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        JButton btnPagar = new JButton("Completar Pago");
        btnPagar.setBackground(new Color(0, 153, 51));
        btnPagar.setForeground(Color.WHITE);
        btnPagar.setFont(new Font("Yu Gothic UI", Font.BOLD, 18));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(204, 0, 0));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Yu Gothic UI", Font.BOLD, 18));

        String[] resultado = new String[2]; // [0] = Método, [1] = Referencia
        resultado[0] = null; 

        btnPagar.addActionListener(e -> {
            if (comboMetodos.getSelectedItem().toString().equals("Efectivo")) {
                try {
                    // Usar replaceAll("[^0-9]", "") para no estrellarse con los puntos del formato
                    String textoPuro = txtRecibido.getText().replaceAll("[^0-9]", "");
                    double recibido = Double.parseDouble(textoPuro);
                    if (recibido < totalAPagar) {
                        JOptionPane.showMessageDialog(dialog, "El efectivo recibido es insuficiente para pagar la cuenta.", "Dinero Insuficiente", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Por favor ingrese una cantidad numérica válida en efectivo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Validación estricta para tarjetas y transferencias
                if (txtReferencia.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Debe ingresar el número de aprobación o referencia obligatoriamente.", "Referencia Faltante", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            resultado[0] = comboMetodos.getSelectedItem().toString();
            resultado[1] = comboMetodos.getSelectedItem().toString().equals("Efectivo") ? "Efectivo" : txtReferencia.getText().trim();
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        pnlBotones.add(btnPagar);
        pnlBotones.add(btnCancelar);

        dialog.add(panelCentro, BorderLayout.CENTER);
        dialog.add(pnlBotones, BorderLayout.SOUTH);

        // Abrir directamente con foco en efectivo
        java.awt.EventQueue.invokeLater(() -> txtRecibido.requestFocus());
        
        dialog.setVisible(true);
        return resultado;
    }
}
