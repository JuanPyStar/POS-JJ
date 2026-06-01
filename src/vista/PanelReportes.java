package vista;

import controlador.Ctrl_Dashboard;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelReportes extends JPanel {

    private Color colorFondo = new Color(255, 255, 255);
    private Color colorAzulPrincipal = new Color(102, 153, 255);
    private Color colorTexto = new Color(50, 50, 50);
    
    private DefaultTableModel modeloTabla;
    private ChartPanel chartPanel;
    private JSplitPane splitPane;

    public PanelReportes() {
        this.setBackground(colorFondo);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(20, 30, 20, 30));

        inicializarComponentes();
        cargarReporteVentasDiarias();
    }

    private void inicializarComponentes() {
        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(colorFondo);
        
        JLabel lblTitulo = new JLabel("Reportes y Estadísticas");
        lblTitulo.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 28));
        lblTitulo.setForeground(colorAzulPrincipal);
        
        JButton btnExportar = new JButton("Exportar a Excel (.xls)");
        btnExportar.setBackground(new Color(39, 174, 96)); // Verde Excel
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
        btnExportar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        btnExportar.addActionListener(e -> exportarAExcelXLS());
        
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(btnExportar, BorderLayout.EAST);

        // --- FILTROS (Tipos de reporte) ---
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelFiltros.setBackground(colorFondo);
        panelFiltros.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel lblTipo = new JLabel("Tipo de Reporte:");
        lblTipo.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        
        JComboBox<String> cbTipoReporte = new JComboBox<>(new String[]{
            "Ventas Diarias", 
            "Ventas Mensuales", 
            "Productos Más Vendidos",
            "Rendimiento de Cajeros",
            "Inventario con Stock Crítico"
        });
        cbTipoReporte.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        
        JButton btnGenerar = new JButton("Generar");
        btnGenerar.setBackground(colorAzulPrincipal);
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        
        btnGenerar.addActionListener(e -> {
            int indice = cbTipoReporte.getSelectedIndex();
            if (indice == 0) {
                cargarReporteVentasDiarias();
            } else if (indice == 1) {
                cargarReporteVentasMensuales();
            } else if (indice == 2) {
                cargarReporteProductosMasVendidos();
            } else if (indice == 3) {
                cargarReporteRendimientoCajeros();
            } else if (indice == 4) {
                cargarReporteStockCritico();
            }
        });
        
        panelFiltros.add(lblTipo);
        panelFiltros.add(cbTipoReporte);
        panelFiltros.add(btnGenerar);

        // --- PANEL NORTE ---
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBackground(colorFondo);
        
        panelNorte.add(panelHeader);
        panelNorte.add(panelFiltros);
        
        this.add(panelNorte, BorderLayout.NORTH);

        // --- TABLA DE RESULTADOS ---
        String[] columnas = {"ID FACTURA", "NÚMERO FACTURA", "FECHA", "CLIENTE", "TOTAL", "VENDEDOR", "MÉTODO PAGO"};
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

        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(400, 0));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, chartPanel);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(10);
        splitPane.setBorder(null);

        this.add(splitPane, BorderLayout.CENTER);
    }
    
    private void cargarReporteVentasDiarias() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(new String[]{"ID FACTURA", "NÚMERO FACTURA", "FECHA", "CLIENTE", "TOTAL", "VENDEDOR", "MÉTODO PAGO"});
        modeloTabla.addRow(new Object[]{"Cargando...", "", "", "", "", "", ""});
        
        javax.swing.SwingWorker<java.util.List<Object[]>, Void> worker = new javax.swing.SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                Ctrl_Dashboard ctrl = new Ctrl_Dashboard();
                return ctrl.getReporteVentas("diario");
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Object[]> datos = get();
                    modeloTabla.setRowCount(0);
                    for (Object[] fila : datos) {
                        modeloTabla.addRow(fila);
                    }
                    chartPanel.setVisible(false);
                    splitPane.setDividerSize(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void cargarReporteVentasMensuales() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(new String[]{"ID FACTURA", "NÚMERO FACTURA", "FECHA", "CLIENTE", "TOTAL", "VENDEDOR", "MÉTODO PAGO"});
        modeloTabla.addRow(new Object[]{"Cargando...", "", "", "", "", "", ""});
        
        javax.swing.SwingWorker<java.util.List<Object[]>, Void> worker = new javax.swing.SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                Ctrl_Dashboard ctrl = new Ctrl_Dashboard();
                return ctrl.getReporteVentas("mensual");
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Object[]> datos = get();
                    modeloTabla.setRowCount(0);
                    for (Object[] fila : datos) {
                        modeloTabla.addRow(fila);
                    }
                    chartPanel.setVisible(false);
                    splitPane.setDividerSize(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void cargarReporteProductosMasVendidos() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(new String[]{"ID PRODUCTO", "NOMBRE", "CANTIDAD VENDIDA", "INGRESO TOTAL"});
        modeloTabla.addRow(new Object[]{"Cargando...", "", "", ""});
        
        javax.swing.SwingWorker<java.util.List<Object[]>, Void> worker = new javax.swing.SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                Ctrl_Dashboard ctrl = new Ctrl_Dashboard();
                return ctrl.getProductosMasVendidos();
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Object[]> datos = get();
                    modeloTabla.setRowCount(0);
                    java.util.List<Object[]> grafica = new java.util.ArrayList<>();
                    for (Object[] fila : datos) {
                        modeloTabla.addRow(fila);
                        grafica.add(new Object[]{fila[1].toString(), Double.valueOf(fila[2].toString())});
                    }
                    chartPanel.setVisible(true);
                    splitPane.setDividerSize(10);
                    splitPane.setDividerLocation(0.7);
                    chartPanel.setDatos(grafica, "Cantidades Vendidas");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    


    private void cargarReporteRendimientoCajeros() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(new String[]{"CAJERO / VENDEDOR", "FACTURAS EMITIDAS", "TOTAL VENDIDO"});
        modeloTabla.addRow(new Object[]{"Cargando...", "", ""});
        
        javax.swing.SwingWorker<java.util.List<Object[]>, Void> worker = new javax.swing.SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                Ctrl_Dashboard ctrl = new Ctrl_Dashboard();
                return ctrl.getReporteRendimientoCajeros();
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Object[]> datos = get();
                    modeloTabla.setRowCount(0);
                    java.util.List<Object[]> grafica = new java.util.ArrayList<>();
                    for (Object[] fila : datos) {
                        modeloTabla.addRow(fila);
                        String valStr = fila[2].toString().replace("$", "").replace(".", "").replace(",", "").trim();
                        try {
                            grafica.add(new Object[]{fila[0].toString(), Double.parseDouble(valStr)});
                        } catch (Exception ex) {}
                    }
                    chartPanel.setVisible(true);
                    splitPane.setDividerSize(10);
                    splitPane.setDividerLocation(0.7);
                    chartPanel.setDatos(grafica, "Total Vendido por Cajero");
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void cargarReporteStockCritico() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(new String[]{"ID PRODUCTO", "NOMBRE", "STOCK ACTUAL", "CATEGORÍA"});
        modeloTabla.addRow(new Object[]{"Cargando...", "", "", ""});
        
        javax.swing.SwingWorker<java.util.List<Object[]>, Void> worker = new javax.swing.SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                Ctrl_Dashboard ctrl = new Ctrl_Dashboard();
                return ctrl.getReporteStockCritico();
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Object[]> datos = get();
                    modeloTabla.setRowCount(0);
                    java.util.List<Object[]> grafica = new java.util.ArrayList<>();
                    for (Object[] fila : datos) {
                        modeloTabla.addRow(fila);
                        grafica.add(new Object[]{fila[1].toString(), Double.valueOf(fila[2].toString())});
                    }
                    chartPanel.setVisible(true);
                    splitPane.setDividerSize(10);
                    splitPane.setDividerLocation(0.7);
                    chartPanel.setDatos(grafica, "Unidades en Stock Crítico");
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void exportarAExcelXLS() {
        if (modeloTabla.getRowCount() == 0 || modeloTabla.getValueAt(0, 0).toString().equals("Cargando...")) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte en Excel (.xls)");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos Excel (*.xls)", "xls"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xls")) {
                filePath += ".xls";
            }
            
            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(filePath), java.nio.charset.StandardCharsets.UTF_8))) {
                pw.write('\ufeff'); // BOM para UTF-8
                
                // Usamos formato HTML-Table, Excel lo abre nativamente conservando colores y formato
                pw.println("<html><head><meta charset=\"UTF-8\"></head><body>");
                pw.println("<table border='1' cellpadding='5' cellspacing='0'>");
                pw.println("<tr style='background-color:#6699ff; color:white; font-weight:bold;'>");
                for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
                    pw.println("<th>" + modeloTabla.getColumnName(i) + "</th>");
                }
                pw.println("</tr>");
                
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    pw.println("<tr>");
                    for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
                        Object val = modeloTabla.getValueAt(i, j);
                        String str = val == null ? "" : val.toString();
                        pw.println("<td>" + str.replace("<", "&lt;").replace(">", "&gt;") + "</td>");
                    }
                    pw.println("</tr>");
                }
                pw.println("</table>");
                pw.println("</body></html>");
                
                JOptionPane.showMessageDialog(this, "Reporte exportado con éxito a:\n" + filePath, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                try {
                    java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
                } catch (Exception ex) {}
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Panel para dibujar la gráfica
    private class ChartPanel extends JPanel {
        private java.util.List<Object[]> datos = new java.util.ArrayList<>();
        private String titulo = "";

        public void setDatos(java.util.List<Object[]> datos, String titulo) {
            this.datos = datos;
            this.titulo = titulo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);

            if (datos == null || datos.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Yu Gothic UI", Font.PLAIN, 16));
                g2.drawString(titulo.isEmpty() ? "No hay datos para graficar" : titulo, 20, h / 2);
                return;
            }

            g2.setColor(colorTexto);
            g2.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 16));
            g2.drawString("Gráfica: " + titulo, 20, 30);

            int padding = 50;
            int maxBarHeight = h - 2 * padding;

            double maxVal = 0;
            for (Object[] d : datos) {
                double val = (double) d[1];
                if (val > maxVal) maxVal = val;
            }
            if (maxVal == 0) maxVal = 1;

            int numBars = datos.size();
            int availableWidth = w - 2 * padding;
            int barWidth = availableWidth / numBars - 10;
            if (barWidth > 60) barWidth = 60;
            if (barWidth < 10) barWidth = 10;

            int startX = padding + (availableWidth - (numBars * (barWidth + 10))) / 2;

            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(padding, h - padding, w - padding, h - padding); // Eje X
            g2.drawLine(padding, h - padding, padding, padding); // Eje Y

            int currentX = startX;
            g2.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

            for (Object[] d : datos) {
                String label = d[0].toString();
                double val = (double) d[1];

                int barH = (int) ((val / maxVal) * maxBarHeight);
                int y = h - padding - barH;

                g2.setColor(new Color(102, 153, 255, 200));
                g2.fillRect(currentX, y, barWidth, barH);
                g2.setColor(colorAzulPrincipal);
                g2.drawRect(currentX, y, barWidth, barH);

                g2.setColor(colorTexto);
                // Dibujar etiqueta truncada si es muy larga
                String displayLabel = label.length() > 8 ? label.substring(0, 8) + ".." : label;
                int textW = g2.getFontMetrics().stringWidth(displayLabel);
                g2.drawString(displayLabel, currentX + (barWidth - textW) / 2, h - padding + 20);

                // Dibujar valor encima de la barra
                String valStr = String.format(java.util.Locale.forLanguageTag("es-CO"), "%,.0f", val);
                int valW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, currentX + (barWidth - valW) / 2, y - 5);

                currentX += barWidth + 10;
            }
        }
    }
}
