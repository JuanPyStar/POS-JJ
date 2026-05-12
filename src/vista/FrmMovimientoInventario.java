package vista;

import controlador.Ctrl_Dashboard;
import controlador.Ctrl_Producto;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.Producto;

public class FrmMovimientoInventario extends JDialog {

    private JComboBox<Producto> cbProducto;
    private JComboBox<String> cbTipoMovimiento;
    private JTextField txtCantidad;
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorFondo = new Color(255, 255, 255);

    public FrmMovimientoInventario(Frame parent) {
        super(parent, "Registrar Movimiento de Inventario", true);
        this.setSize(420, 320);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(colorFondo);

        inicializarComponentes();
        cargarProductos();
    }

    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 20));
        panelFormulario.setBorder(new EmptyBorder(20, 30, 20, 30));
        panelFormulario.setBackground(colorFondo);

        panelFormulario.add(crearEtiqueta("Producto:"));
        cbProducto = new JComboBox<>();
        cbProducto.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        panelFormulario.add(cbProducto);

        panelFormulario.add(crearEtiqueta("Tipo de Movimiento:"));
        cbTipoMovimiento = new JComboBox<>(new String[]{"ENTRADA", "SALIDA"});
        cbTipoMovimiento.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        panelFormulario.add(cbTipoMovimiento);

        panelFormulario.add(crearEtiqueta("Cantidad:"));
        txtCantidad = new JTextField();
        txtCantidad.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        txtCantidad.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        panelFormulario.add(txtCantidad);

        this.add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setBackground(colorFondo);

        JButton btnGuardar = new JButton("Registrar");
        btnGuardar.setBackground(colorAzulPrincipal);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> registrarMovimiento());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> this.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        this.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarProductos() {
        cbProducto.removeAllItems();
        Ctrl_Producto ctrl = new Ctrl_Producto();
        List<Producto> productos = ctrl.obtenerTodos();
        for (Producto p : productos) {
            cbProducto.addItem(p);
        }
        if (cbProducto.getItemCount() > 0) {
            cbProducto.setSelectedIndex(0);
        }
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        return lbl;
    }

    private void registrarMovimiento() {
        if (cbProducto.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.");
                return;
            }

            Producto producto = (Producto) cbProducto.getSelectedItem();
            String tipo = cbTipoMovimiento.getSelectedItem().toString();
            Ctrl_Dashboard ctrl = new Ctrl_Dashboard();
            boolean ok = ctrl.registrarMovimientoInventario(producto.getIdProducto(), tipo, cantidad);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Movimiento registrado correctamente.");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el movimiento. Verifica el stock o los datos ingresados.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingresa una cantidad válida.");
        }
    }
}
