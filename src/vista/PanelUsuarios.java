package vista;

import controlador.Ctrl_Usuario;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelUsuarios extends JPanel {

    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);
    
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;

    public PanelUsuarios() {
        this.setBackground(colorFondo);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
        cargarDatosTabla("");
    }

    private void inicializarComponentes() {
        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        JButton btnNuevoUsuario = new JButton("+ Nuevo Usuario");
        btnNuevoUsuario.setBackground(colorAzulPrincipal);
        btnNuevoUsuario.setForeground(Color.WHITE);
        btnNuevoUsuario.setFocusPainted(false);
        btnNuevoUsuario.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        btnNuevoUsuario.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        btnNuevoUsuario.addActionListener(e -> {
            // TODO: Abrir formulario para crear nuevo usuario
            cargarDatosTabla();
        });
        
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(btnNuevoUsuario, BorderLayout.EAST);

        // --- BUSCADOR ---
        JPanel panelBuscador = new JPanel(new BorderLayout(15, 0));
        panelBuscador.setBackground(colorFondo);
        panelBuscador.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        txtBuscar.setBackground(Color.WHITE);
        txtBuscar.setForeground(colorTexto);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorAzulPrincipal, 2),
                new EmptyBorder(5, 10, 5, 10)
        ));
        txtBuscar.setPreferredSize(new Dimension(0, 45));
        txtBuscar.setToolTipText("Buscar por nombre, apellido o usuario");

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(colorAzulPrincipal);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnBuscar.addActionListener(e -> cargarDatosTabla(txtBuscar.getText()));

        panelBuscador.add(txtBuscar, BorderLayout.CENTER);
        panelBuscador.add(btnBuscar, BorderLayout.EAST);

        // --- PANEL NORTE ---
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBackground(colorFondo);
        
        panelNorte.add(panelHeader);
        panelNorte.add(panelBuscador);
        
        this.add(panelNorte, BorderLayout.NORTH);

        // --- TABLA DE USUARIOS ---
        String[] columnas = {"ID", "NOMBRE", "APELLIDO", "USUARIO", "TELÉFONO", "ROL", "ESTADO"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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
        cargarDatosTabla("");
    }

    public void cargarDatosTabla(String filtro) {
        modeloTabla.setRowCount(0);
        Ctrl_Usuario ctrl = new Ctrl_Usuario();
        
        for (Object[] fila : filtro == null || filtro.trim().isEmpty() ? ctrl.obtenerTodos() : ctrl.buscarUsuarios(filtro)) {
            modeloTabla.addRow(fila);
        }
    }
}
