package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PanelConfiguracion extends JPanel {

    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);

    public PanelConfiguracion() {
        this.setBackground(colorFondo);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(30, 30, 30, 30));

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Configuración del Sistema");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        JLabel lblSubtitulo = new JLabel("Ajustes generales de la empresa y preferencias");
        lblSubtitulo.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(Color.GRAY);

        JPanel panelTextosHeader = new JPanel(new GridLayout(2, 1));
        panelTextosHeader.setBackground(colorFondo);
        panelTextosHeader.add(lblTitulo);
        panelTextosHeader.add(lblSubtitulo);

        panelHeader.add(panelTextosHeader, BorderLayout.WEST);
        
        this.add(panelHeader, BorderLayout.NORTH);

        // --- FORMULARIO DE CONFIGURACIÓN ---
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 20, 20));
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(30, 40, 30, 40)
        ));

        panelFormulario.add(crearEtiqueta("Nombre de la Empresa:"));
        panelFormulario.add(crearCampoTexto("Mi Empresa POS"));

        panelFormulario.add(crearEtiqueta("NIT / RUC:"));
        panelFormulario.add(crearCampoTexto("123456789-0"));

        panelFormulario.add(crearEtiqueta("Teléfono:"));
        panelFormulario.add(crearCampoTexto("+57 300 000 0000"));

        panelFormulario.add(crearEtiqueta("Dirección:"));
        panelFormulario.add(crearCampoTexto("Calle Falsa 123"));

        panelFormulario.add(crearEtiqueta("Mensaje del Ticket:"));
        panelFormulario.add(crearCampoTexto("¡Gracias por su compra, vuelva pronto!"));

        JButton btnGuardar = new JButton("Guardar Configuración");
        btnGuardar.setBackground(colorAzulPrincipal);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));

        panelFormulario.add(new JLabel("")); // Espacio vacío
        panelFormulario.add(btnGuardar);

        // --- PANEL CENTRO PARA ALINEAR ARRIBA ---
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(colorFondo);
        panelCentro.add(panelFormulario, BorderLayout.NORTH);

        this.add(panelCentro, BorderLayout.CENTER);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Yu Gothic UI", Font.BOLD, 16));
        lbl.setForeground(colorTexto);
        return lbl;
    }

    private JTextField crearCampoTexto(String placeholder) {
        JTextField txt = new JTextField(placeholder);
        txt.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return txt;
    }
}
