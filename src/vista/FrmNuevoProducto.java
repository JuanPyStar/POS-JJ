package vista;

import controlador.Ctrl_Categoria;
import controlador.Ctrl_Producto;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.Categoria;
import modelo.Producto;

public class FrmNuevoProducto extends JDialog {

    private JTextField txtNombre, txtCantidad, txtPrecio, txtDescripcion, txtIva;
    private JComboBox<Categoria> cbCategoria;
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorFondo = new Color(255, 255, 255);

    public FrmNuevoProducto(Frame parent) {
        super(parent, "Registrar Nuevo Producto", true);
        this.setSize(450, 550);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(colorFondo);

        inicializarComponentes();
        cargarCategorias();
    }

    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 10, 20));
        panelFormulario.setBorder(new EmptyBorder(20, 30, 20, 30));
        panelFormulario.setBackground(colorFondo);

        txtNombre = crearCampoTexto();
        txtCantidad = crearCampoTexto();
        txtPrecio = crearCampoTexto();
        txtDescripcion = crearCampoTexto();
        txtIva = crearCampoTexto();
        txtIva.setText("19"); // Default IVA
        
        // --- Combobox de Categoría con Botón ---
        cbCategoria = new JComboBox<>();
        cbCategoria.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        
        JButton btnNuevaCat = new JButton("+");
        btnNuevaCat.setBackground(colorAzulPrincipal);
        btnNuevaCat.setForeground(Color.WHITE);
        btnNuevaCat.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        btnNuevaCat.setFocusPainted(false);
        btnNuevaCat.addActionListener(e -> {
            new FrmNuevaCategoria(this).setVisible(true);
            cargarCategorias(); // Recargar después de cerrar
        });
        
        JPanel panelCategoria = new JPanel(new BorderLayout(5, 0));
        panelCategoria.setBackground(colorFondo);
        panelCategoria.add(cbCategoria, BorderLayout.CENTER);
        panelCategoria.add(btnNuevaCat, BorderLayout.EAST);

        // Agregando los componentes
        panelFormulario.add(crearEtiqueta("Nombre:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(crearEtiqueta("Cantidad:"));
        panelFormulario.add(txtCantidad);

        panelFormulario.add(crearEtiqueta("Precio Unitario:"));
        panelFormulario.add(txtPrecio);

        panelFormulario.add(crearEtiqueta("Descripción:"));
        panelFormulario.add(txtDescripcion);

        panelFormulario.add(crearEtiqueta("Porcentaje IVA:"));
        panelFormulario.add(txtIva);

        panelFormulario.add(crearEtiqueta("Categoría:"));
        panelFormulario.add(panelCategoria);

        this.add(panelFormulario, BorderLayout.CENTER);

        // --- Botones inferiores ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setBackground(colorFondo);

        JButton btnGuardar = new JButton("Guardar Producto");
        btnGuardar.setBackground(colorAzulPrincipal);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> guardarProducto());
        btnCancelar.addActionListener(e -> this.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        this.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarCategorias() {
        cbCategoria.removeAllItems();
        Ctrl_Categoria ctrl = new Ctrl_Categoria();
        List<Categoria> lista = ctrl.obtenerTodas();
        
        for (Categoria c : lista) {
            cbCategoria.addItem(c);
        }
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField crearCampoTexto() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return txt;
    }

    private void guardarProducto() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del producto.");
            return;
        }
        
        if (cbCategoria.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar o crear una categoría primero.");
            return;
        }

        try {
            Producto p = new Producto();
            p.setNombre(txtNombre.getText().trim());
            p.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
            p.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            p.setDescripcion(txtDescripcion.getText().trim());
            p.setPorcentajeIva(Integer.parseInt(txtIva.getText().trim()));
            
            Categoria catSeleccionada = (Categoria) cbCategoria.getSelectedItem();
            p.setIdCategoria(catSeleccionada.getIdCategoria());
            p.setEstado(1); // 1 = Activo

            Ctrl_Producto ctrl = new Ctrl_Producto();
            if (ctrl.guardar(p)) {
                JOptionPane.showMessageDialog(this, "Producto guardado correctamente");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el producto. Verifica la consola.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese valores numéricos válidos en Cantidad, Precio e IVA.");
        }
    }
}
