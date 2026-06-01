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
    private JTable tablaUsuarios;
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
        
        btnNuevoUsuario.addActionListener(e -> crearNuevoUsuario());
        
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(btnNuevoUsuario, BorderLayout.EAST);

        // --- BUSCADOR Y ACCIONES ---
        JPanel panelBuscador = new JPanel(new BorderLayout());
        panelBuscador.setBackground(colorFondo);
        panelBuscador.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Panel Izquierdo: Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFiltros.setBackground(colorFondo);
        
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        lblBuscar.setForeground(colorTexto);
        
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        txtBuscar.setBackground(Color.WHITE);
        txtBuscar.setForeground(colorTexto);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        txtBuscar.setPreferredSize(new Dimension(250, 35));
        txtBuscar.setToolTipText("Buscar por nombre, apellido o usuario");

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(colorAzulPrincipal);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnBuscar.setPreferredSize(new Dimension(100, 35));
        btnBuscar.addActionListener(e -> cargarDatosTabla(txtBuscar.getText()));
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(231, 76, 60)); // Rojo
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnLimpiar.setPreferredSize(new Dimension(100, 35));
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarDatosTabla("");
        });

        panelFiltros.add(lblBuscar);
        panelFiltros.add(txtBuscar);
        panelFiltros.add(btnBuscar);
        panelFiltros.add(btnLimpiar);

        // Panel Derecho: Acciones (Editar/Eliminar)
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelAcciones.setBackground(colorFondo);

        JButton btnEditarUsuario = new JButton("Editar");
        btnEditarUsuario.setBackground(new Color(52, 152, 219));
        btnEditarUsuario.setForeground(Color.WHITE);
        btnEditarUsuario.setFocusPainted(false);
        btnEditarUsuario.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnEditarUsuario.setPreferredSize(new Dimension(100, 35));
        btnEditarUsuario.addActionListener(e -> editarUsuarioSeleccionado());

        JButton btnEliminarUsuario = new JButton("Eliminar");
        btnEliminarUsuario.setBackground(new Color(231, 76, 60));
        btnEliminarUsuario.setForeground(Color.WHITE);
        btnEliminarUsuario.setFocusPainted(false);
        btnEliminarUsuario.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        btnEliminarUsuario.setPreferredSize(new Dimension(100, 35));
        btnEliminarUsuario.addActionListener(e -> eliminarUsuarioSeleccionado());

        panelAcciones.add(btnEditarUsuario);
        panelAcciones.add(btnEliminarUsuario);

        panelBuscador.add(panelFiltros, BorderLayout.WEST);
        panelBuscador.add(panelAcciones, BorderLayout.EAST);

        // --- PANEL NORTE ---
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBackground(colorFondo);
        
        panelNorte.add(panelHeader);
        panelNorte.add(panelBuscador);
        
        this.add(panelNorte, BorderLayout.NORTH);

        // --- TABLA DE USUARIOS ---
        String[] columnas = {"ID", "NOMBRE", "APELLIDO", "USUARIO", "TELÉFONO", "ROL", "ESTADO", "ESTADO TURNO"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setBackground(Color.WHITE);
        tablaUsuarios.setForeground(colorTexto);
        tablaUsuarios.setRowHeight(40);
        tablaUsuarios.setGridColor(new Color(230, 230, 230));
        tablaUsuarios.setSelectionBackground(new Color(200, 220, 255));
        tablaUsuarios.setSelectionForeground(Color.BLACK);
        tablaUsuarios.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));

        JTableHeader header = tablaUsuarios.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(colorAzulPrincipal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 45));

        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        this.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void crearNuevoUsuario() {
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtUsuario = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtTelefono = new JTextField();
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 10));
        panel.add(new JLabel("Nombre:")); panel.add(txtNombre);
        panel.add(new JLabel("Apellido:")); panel.add(txtApellido);
        panel.add(new JLabel("Usuario:")); panel.add(txtUsuario);
        panel.add(new JLabel("Contraseña:")); panel.add(txtPassword);
        panel.add(new JLabel("Teléfono:")); panel.add(txtTelefono);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Crear Nuevo Cajero", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (txtNombre.getText().trim().isEmpty() || txtUsuario.getText().trim().isEmpty() || new String(txtPassword.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre, Usuario y Contraseña son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                java.sql.Connection cn = conexion.conexion.conectar();
                java.sql.PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO tb_usuario (nombre, apellido, usuario, password, telefono, rol, estado) VALUES (?,?,?,?,?,?,1)"
                );
                ps.setString(1, txtNombre.getText().trim());
                ps.setString(2, txtApellido.getText().trim());
                ps.setString(3, txtUsuario.getText().trim());
                ps.setString(4, new String(txtPassword.getPassword()));
                ps.setString(5, txtTelefono.getText().trim());
                ps.setString(6, "Cajero"); // Siempre cajero
                
                if(ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Usuario creado correctamente.");
                    cargarDatosTabla(txtBuscar.getText());
                }
                cn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarUsuarioSeleccionado() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idUsuario = Integer.parseInt(tablaUsuarios.getValueAt(fila, 0).toString());
        Ctrl_Usuario ctrl = new Ctrl_Usuario();
        modelo.Usuario u = ctrl.obtenerUsuario(idUsuario);
        
        if (u != null) {
            JTextField txtNombre = new JTextField(u.getNombre());
            JTextField txtApellido = new JTextField(u.getApellido());
            JTextField txtUsuario = new JTextField(u.getUsuario());
            JTextField txtTelefono = new JTextField(u.getTelefono());
            
            // Campo de contraseña con ojito
            JPasswordField txtPassword = new JPasswordField(u.getPassword());
            JButton btnOjo = new JButton("👁");
            btnOjo.setFocusPainted(false);
            btnOjo.setBackground(new Color(230, 230, 230));
            btnOjo.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    txtPassword.setEchoChar((char) 0); // Mostrar
                }
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    txtPassword.setEchoChar('•'); // Ocultar
                }
            });
            JPanel panelPass = new JPanel(new BorderLayout());
            panelPass.add(txtPassword, BorderLayout.CENTER);
            panelPass.add(btnOjo, BorderLayout.EAST);

            JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
            cbEstado.setSelectedIndex(u.getEstado() == 1 ? 0 : 1);
            
            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 10));
            panel.add(new JLabel("Nombre:")); panel.add(txtNombre);
            panel.add(new JLabel("Apellido:")); panel.add(txtApellido);
            panel.add(new JLabel("Usuario:")); panel.add(txtUsuario);
            panel.add(new JLabel("Contraseña:")); panel.add(panelPass);
            panel.add(new JLabel("Teléfono:")); panel.add(txtTelefono);
            panel.add(new JLabel("Estado:")); panel.add(cbEstado);
            
            int result = JOptionPane.showConfirmDialog(this, panel, "Editar Usuario ID: " + idUsuario, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                u.setNombre(txtNombre.getText().trim());
                u.setApellido(txtApellido.getText().trim());
                u.setUsuario(txtUsuario.getText().trim());
                u.setPassword(new String(txtPassword.getPassword()));
                u.setTelefono(txtTelefono.getText().trim());
                u.setEstado(cbEstado.getSelectedIndex() == 0 ? 1 : 0);
                
                if (ctrl.actualizar(u)) {
                    JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
                    cargarDatosTabla(txtBuscar.getText());
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void eliminarUsuarioSeleccionado() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla para eliminar o desactivar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "¿Está seguro de cambiar el estado de este usuario a INACTIVO?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            int idUsuario = Integer.parseInt(tablaUsuarios.getValueAt(fila, 0).toString());
            try {
                java.sql.Connection cn = conexion.conexion.conectar();
                java.sql.PreparedStatement ps = cn.prepareStatement("UPDATE tb_usuario SET estado = 0 WHERE idUsuario = ?");
                ps.setInt(1, idUsuario);
                if(ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Usuario desactivado correctamente.");
                    cargarDatosTabla(txtBuscar.getText());
                }
                cn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void cargarDatosTabla() {
        cargarDatosTabla("");
    }

    public void cargarDatosTabla(String filtro) {
        modeloTabla.setRowCount(0);
        Object[] filaCarga = new Object[]{"Cargando...", "", "", "", "", "", "", ""};
        modeloTabla.addRow(filaCarga);
        
        javax.swing.SwingWorker<java.util.List<Object[]>, Void> worker = new javax.swing.SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                Ctrl_Usuario ctrl = new Ctrl_Usuario();
                if (filtro == null || filtro.trim().isEmpty()) {
                    return ctrl.obtenerTodos();
                } else {
                    return ctrl.buscarUsuarios(filtro);
                }
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Object[]> datos = get();
                    modeloTabla.setRowCount(0);
                    for (Object[] fila : datos) {
                        modeloTabla.addRow(fila);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
