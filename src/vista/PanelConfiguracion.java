package vista;

import controlador.Ctrl_Usuario;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.Usuario;

public class PanelConfiguracion extends JPanel {

    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);

    private Usuario usuarioLogueado;
    
    // Perfil
    private JTextField txtNombreAdmin;
    private JTextField txtUsuarioAdmin;
    private JPasswordField txtPasswordAdmin;
    
    private boolean perfilDesbloqueado = false;
    
    // Factura
    private JTextField txtEmpresa;
    private JTextField txtNit;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JTextField txtMensaje;

    private static final String ARCHIVO_CONFIG = "config.properties";

    public PanelConfiguracion(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.setBackground(colorFondo);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(30, 30, 30, 30));

        inicializarComponentes();
        cargarDatosPerfil();
        cargarDatosFactura();
    }

    private void inicializarComponentes() {
        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Configuración del Sistema");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        JLabel lblSubtitulo = new JLabel("Ajustes generales de la empresa y administrador");
        lblSubtitulo.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(Color.GRAY);

        JPanel panelTextosHeader = new JPanel(new GridLayout(2, 1));
        panelTextosHeader.setBackground(colorFondo);
        panelTextosHeader.add(lblTitulo);
        panelTextosHeader.add(lblSubtitulo);

        panelHeader.add(panelTextosHeader, BorderLayout.WEST);
        
        this.add(panelHeader, BorderLayout.NORTH);

        // --- TABS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        // Tab 1: Perfil
        JPanel panelPerfil = crearPanelPerfil();
        
        ImageIcon iconoUsuario = null;
        try {
            java.net.URL url = getClass().getResource("/img/usuario.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                iconoUsuario = new ImageIcon(img);
            }
        } catch(Exception e){}
        
        tabbedPane.addTab("Perfil de Administrador", iconoUsuario, panelPerfil);

        // Tab 2: Factura
        JPanel panelFactura = crearPanelFactura();
        
        ImageIcon iconoFactura = null;
        try {
            java.net.URL url = getClass().getResource("/img/reportes.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                iconoFactura = new ImageIcon(img);
            }
        } catch(Exception e){}
        
        tabbedPane.addTab("Datos de Factura", iconoFactura, panelFactura);

        this.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelPerfil() {
        JPanel panelCentral = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        panelCentral.setBackground(colorFondo);
        
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 20, 20));
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(30, 40, 30, 40)
        ));

        panelFormulario.add(crearEtiqueta("Nombre Completo:"));
        txtNombreAdmin = crearCampoTexto("");
        panelFormulario.add(txtNombreAdmin);

        panelFormulario.add(crearEtiqueta("Usuario de Acceso:"));
        txtUsuarioAdmin = crearCampoTexto("");
        panelFormulario.add(txtUsuarioAdmin);

        panelFormulario.add(crearEtiqueta("Nueva Contraseña:"));
        txtPasswordAdmin = new JPasswordField();
        txtPasswordAdmin.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        txtPasswordAdmin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 10, 8, 10)
        ));
        panelFormulario.add(txtPasswordAdmin);
        
        // Bloquear campos inicialmente
        txtNombreAdmin.setEditable(false);
        txtUsuarioAdmin.setEditable(false);
        txtPasswordAdmin.setEditable(false);
        
        java.awt.event.MouseAdapter ma = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                verificarDesbloqueo((Component) e.getSource());
            }
        };
        
        txtNombreAdmin.addMouseListener(ma);
        txtUsuarioAdmin.addMouseListener(ma);
        txtPasswordAdmin.addMouseListener(ma);

        JButton btnGuardarPerfil = new JButton("Actualizar Perfil");
        btnGuardarPerfil.setBackground(colorAzulPrincipal);
        btnGuardarPerfil.setForeground(Color.WHITE);
        btnGuardarPerfil.setFocusPainted(false);
        btnGuardarPerfil.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        btnGuardarPerfil.addActionListener(e -> actualizarPerfil());

        panelFormulario.add(new JLabel("*(Dejar en blanco para no cambiar)*"));
        panelFormulario.add(btnGuardarPerfil);

        panelCentral.add(panelFormulario);
        return panelCentral;
    }
    
    private JPanel crearPanelFactura() {
        JPanel panelCentral = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        panelCentral.setBackground(colorFondo);
        
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 20, 20));
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(30, 40, 30, 40)
        ));

        panelFormulario.add(crearEtiqueta("Nombre de la Empresa:"));
        txtEmpresa = crearCampoTexto("");
        panelFormulario.add(txtEmpresa);

        panelFormulario.add(crearEtiqueta("NIT / RUC:"));
        txtNit = crearCampoTexto("");
        panelFormulario.add(txtNit);

        panelFormulario.add(crearEtiqueta("Teléfono:"));
        txtTelefono = crearCampoTexto("");
        panelFormulario.add(txtTelefono);

        panelFormulario.add(crearEtiqueta("Dirección:"));
        txtDireccion = crearCampoTexto("");
        panelFormulario.add(txtDireccion);

        panelFormulario.add(crearEtiqueta("Mensaje del Ticket:"));
        txtMensaje = crearCampoTexto("");
        panelFormulario.add(txtMensaje);

        JButton btnGuardarFactura = new JButton("Guardar Configuración de Factura");
        btnGuardarFactura.setBackground(new Color(0, 153, 51)); // Verde
        btnGuardarFactura.setForeground(Color.WHITE);
        btnGuardarFactura.setFocusPainted(false);
        btnGuardarFactura.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        btnGuardarFactura.addActionListener(e -> guardarDatosFactura());

        panelFormulario.add(new JLabel("")); // Espacio vacío
        panelFormulario.add(btnGuardarFactura);

        panelCentral.add(panelFormulario);
        return panelCentral;
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
    
    // --- LÓGICA DE DATOS ---

    private void verificarDesbloqueo(Component componenteClickeado) {
        if (perfilDesbloqueado) return;

        JPasswordField pwd = new JPasswordField(15);
        pwd.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
        int action = JOptionPane.showConfirmDialog(this, pwd, "Ingrese su contraseña actual para desbloquear la edición", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (action == JOptionPane.OK_OPTION) {
            String passIngresada = new String(pwd.getPassword()).trim();
            if (passIngresada.equals(usuarioLogueado.getPassword())) {
                perfilDesbloqueado = true;
                txtNombreAdmin.setEditable(true);
                txtUsuarioAdmin.setEditable(true);
                txtPasswordAdmin.setEditable(true);
                componenteClickeado.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error de Seguridad", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarDatosPerfil() {
        if (usuarioLogueado != null) {
            txtNombreAdmin.setText(usuarioLogueado.getNombre());
            txtUsuarioAdmin.setText(usuarioLogueado.getUsuario());
        }
    }

    private void actualizarPerfil() {
        if (!perfilDesbloqueado) {
            JOptionPane.showMessageDialog(this, "Debe desbloquear el perfil primero haciendo clic en algún campo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtNombreAdmin.getText().trim().isEmpty() || txtUsuarioAdmin.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre y el usuario son obligatorios");
            return;
        }

        Usuario u = new Usuario();
        u.setIdUsuario(usuarioLogueado.getIdUsuario());
        u.setNombre(txtNombreAdmin.getText().trim());
        u.setUsuario(txtUsuarioAdmin.getText().trim());
        u.setRol(usuarioLogueado.getRol());
        u.setEstado(usuarioLogueado.getEstado());

        String pass = new String(txtPasswordAdmin.getPassword()).trim();
        if (!pass.isEmpty()) {
            u.setPassword(pass);
        } else {
            u.setPassword(usuarioLogueado.getPassword()); // mantener actual
        }

        Ctrl_Usuario ctrl = new Ctrl_Usuario();
        if (ctrl.actualizar(u)) {
            JOptionPane.showMessageDialog(this, "Perfil actualizado correctamente");
            usuarioLogueado.setNombre(u.getNombre());
            usuarioLogueado.setUsuario(u.getUsuario());
            usuarioLogueado.setPassword(u.getPassword());
            txtPasswordAdmin.setText("");
            
            // Volver a bloquear
            perfilDesbloqueado = false;
            txtNombreAdmin.setEditable(false);
            txtUsuarioAdmin.setEditable(false);
            txtPasswordAdmin.setEditable(false);
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar perfil");
        }
    }

    private void cargarDatosFactura() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(ARCHIVO_CONFIG)) {
            props.load(in);
            txtEmpresa.setText(props.getProperty("empresa_nombre", ""));
            txtNit.setText(props.getProperty("empresa_nit", ""));
            txtTelefono.setText(props.getProperty("empresa_telefono", ""));
            txtDireccion.setText(props.getProperty("empresa_direccion", ""));
            txtMensaje.setText(props.getProperty("empresa_mensaje", ""));
        } catch (Exception e) {
            // No existe o no se puede leer, ignorar.
        }
    }

    private void guardarDatosFactura() {
        Properties props = new Properties();
        props.setProperty("empresa_nombre", txtEmpresa.getText().trim());
        props.setProperty("empresa_nit", txtNit.getText().trim());
        props.setProperty("empresa_telefono", txtTelefono.getText().trim());
        props.setProperty("empresa_direccion", txtDireccion.getText().trim());
        props.setProperty("empresa_mensaje", txtMensaje.getText().trim());

        try (FileOutputStream out = new FileOutputStream(ARCHIVO_CONFIG)) {
            props.store(out, "Configuracion de Factura - POS JJ");
            JOptionPane.showMessageDialog(this, "Datos de factura guardados correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar configuración: " + e.getMessage());
        }
    }
}
