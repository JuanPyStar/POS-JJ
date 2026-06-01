package vista;

import controlador.Ctrl_Turno;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import modelo.Turno;

public class FrmTurno extends javax.swing.JDialog {

    private Ctrl_Turno ctrlTurno = new Ctrl_Turno();
    private Turno turnoActivo;
    private int idUsuarioActual;

    public FrmTurno(java.awt.Frame parent, boolean modal, int idUsuario) {
        super(parent, modal);
        this.idUsuarioActual = idUsuario;
        initComponents();
        this.setSize(new Dimension(400, 300));
        this.setLocationRelativeTo(parent);
        this.setTitle("Gestión de Turnos");
        cargarDatosTurno();
    }

    private void cargarDatosTurno() {
        turnoActivo = ctrlTurno.getTurnoActivo();
        if (turnoActivo != null) {
            lblEstado.setText("Turno Abierto desde: " + turnoActivo.getFechaApertura());
            txtBase.setText(String.format(java.util.Locale.forLanguageTag("es-CO"), "%,.0f", turnoActivo.getBaseInicial()));
            txtBase.setEnabled(false);
            btnAbrir.setEnabled(false);
            btnCerrar.setEnabled(true);
            
            // calcular ventas
            double ventas = ctrlTurno.calcularVentasTurno(turnoActivo.getIdTurno());
            lblVentas.setText("Ventas del turno: $" + String.format("%.2f", ventas));
        } else {
            lblEstado.setText("Sin turno activo");
            txtBase.setText("0");
            txtBase.setEnabled(true);
            btnAbrir.setEnabled(true);
            btnCerrar.setEnabled(false);
            lblVentas.setText("Ventas del turno: $0.00");
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtBase = new javax.swing.JTextField();
        btnAbrir = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        lblVentas = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Apertura y Cierre de Turno");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 400, -1));

        lblEstado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEstado.setText("Estado:");
        getContentPane().add(lblEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 400, -1));

        jLabel2.setText("Base Inicial:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 110, -1, -1));
        getContentPane().add(txtBase, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 106, 150, 30));

        btnAbrir.setBackground(new java.awt.Color(0, 153, 51));
        btnAbrir.setForeground(new java.awt.Color(255, 255, 255));
        btnAbrir.setText("Abrir Turno");
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });
        getContentPane().add(btnAbrir, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 190, -1, -1));

        btnCerrar.setBackground(new java.awt.Color(204, 0, 0));
        btnCerrar.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrar.setText("Cerrar Turno");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, -1, -1));

        lblVentas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentas.setText("Ventas: ");
        getContentPane().add(lblVentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 400, -1));

        pack();
    }

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtBase.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una base inicial");
            return;
        }
        try {
            // Eliminar comas o puntos de los miles antes de convertir a double
            String textoBase = txtBase.getText().replace(".", "").replace(",", "");
            double base = Double.parseDouble(textoBase);
            Turno t = new Turno();
            t.setIdUsuario(this.idUsuarioActual); 
            t.setBaseInicial(base);
            
            if (ctrlTurno.abrirTurno(t)) {
                JOptionPane.showMessageDialog(this, "Turno abierto correctamente");
                cargarDatosTurno();
            } else {
                JOptionPane.showMessageDialog(this, "Error al abrir turno");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Base inicial inválida");
        }
    }

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {
        if (turnoActivo != null) {
            double ventas = ctrlTurno.calcularVentasTurno(turnoActivo.getIdTurno());
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de cerrar el turno?\nVentas totales: $" + ventas, "Cerrar Turno", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (ctrlTurno.cerrarTurno(turnoActivo.getIdTurno(), ventas)) {
                    JOptionPane.showMessageDialog(this, "Turno cerrado correctamente");
                    cargarDatosTurno();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al cerrar turno");
                }
            }
        }
    }

    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblVentas;
    private javax.swing.JTextField txtBase;
}
