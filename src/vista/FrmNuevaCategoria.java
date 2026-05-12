package vista;

import controlador.Ctrl_Categoria;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.Categoria;

public class FrmNuevaCategoria extends JDialog {

    private JTextField txtDescripcion;
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorFondo = new Color(255, 255, 255);

    public FrmNuevaCategoria(JDialog parent) {
        super(parent, "Registrar Nueva Categoría", true);
        this.setSize(350, 200);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(colorFondo);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridLayout(2, 1, 10, 10));
        panelFormulario.setBorder(new EmptyBorder(20, 30, 10, 30));
        panelFormulario.setBackground(colorFondo);

        JLabel lbl = new JLabel("Nombre de la Categoría:");
        lbl.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        
        txtDescripcion = new JTextField();
        txtDescripcion.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 5, 5, 5)
        ));

        panelFormulario.add(lbl);
        panelFormulario.add(txtDescripcion);

        this.add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(colorFondo);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(colorAzulPrincipal);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> guardarCategoria());
        btnCancelar.addActionListener(e -> this.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        this.add(panelBotones, BorderLayout.SOUTH);
    }

    private void guardarCategoria() {
        if (txtDescripcion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío");
            return;
        }

        Categoria c = new Categoria();
        c.setDescripcion(txtDescripcion.getText().trim());
        c.setEstado(1); // 1 = Activo

        Ctrl_Categoria ctrl = new Ctrl_Categoria();
        if (ctrl.guardar(c)) {
            JOptionPane.showMessageDialog(this, "Categoría guardada correctamente");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la categoría");
        }
    }
}
