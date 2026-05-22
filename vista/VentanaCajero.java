package vista;

import modelo.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class VentanaCajero extends JFrame {
    private Inventario inventario;
    private Venta ventaActual;
    private JTable tablaProductos, tablaVenta;
    private DefaultTableModel modelProductos, modelVenta;
    private JLabel lblTotal, lblContador;
    private JTextField txtCodigo, txtCantidad, txtBuscar;
    private List<Producto> todosLosProductos = new ArrayList<>();

    public VentanaCajero(Usuario usuario) {
        super("Modulo Cajero - " + usuario.getNombre());
        inventario = new Inventario();
        ventaActual = new Venta();
        inicializarComponentes();
        cargarProductos();
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        JPanel panelIzq = new JPanel(new BorderLayout());
        panelIzq.setBorder(BorderFactory.createTitledBorder("Productos disponibles"));
        panelIzq.setPreferredSize(new Dimension(430, 0));

        JPanel panelBusqueda = new JPanel(new BorderLayout());
        txtBuscar = new JTextField();
        txtBuscar.setToolTipText("Buscar por codigo o nombre");
        panelBusqueda.add(new JLabel("Buscar: "), BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelIzq.add(panelBusqueda, BorderLayout.NORTH);

        modelProductos = new DefaultTableModel(new String[]{"Codigo", "Nombre", "Precio", "Stock"}, 0);
        tablaProductos = new JTable(modelProductos);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelIzq.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        lblContador = new JLabel("Productos en inventario: 0");
        panelIzq.add(lblContador, BorderLayout.SOUTH);

        add(panelIzq, BorderLayout.WEST);

        JPanel panelDer = new JPanel(new BorderLayout());
        panelDer.setBorder(BorderFactory.createTitledBorder("Venta actual"));
        modelVenta = new DefaultTableModel(new String[]{"Producto", "Cantidad", "Subtotal"}, 0);
        tablaVenta = new JTable(modelVenta);
        panelDer.add(new JScrollPane(tablaVenta), BorderLayout.CENTER);

        JPanel panelTotal = new JPanel(new FlowLayout());
        lblTotal = new JLabel("Total: $0.00");
        panelTotal.add(lblTotal);
        panelDer.add(panelTotal, BorderLayout.SOUTH);
        add(panelDer, BorderLayout.CENTER);

        JPanel panelAgregar = new JPanel(new BorderLayout(5, 5));
        panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar producto a la venta"));
        JPanel panelCampos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCampos.add(new JLabel("Codigo:"));
        txtCodigo = new JTextField(8);
        panelCampos.add(txtCodigo);
        panelCampos.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField("1", 4);
        panelCampos.add(txtCantidad);

        JButton btnAgregar = new JButton("Agregar");
        panelCampos.add(btnAgregar);
        panelAgregar.add(panelCampos, BorderLayout.NORTH);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        panelAcciones.add(btnEliminar);

        JButton btnFinalizar = new JButton("Finalizar venta");
        panelAcciones.add(btnFinalizar);

        JButton btnCancelar = new JButton("Cancelar venta");
        panelAcciones.add(btnCancelar);

        JButton btnCerrar = new JButton("Cerrar sesion");
        panelAcciones.add(btnCerrar);
        panelAgregar.add(panelAcciones, BorderLayout.CENTER);

        add(panelAgregar, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnFinalizar.addActionListener(e -> finalizarVenta());
        btnCancelar.addActionListener(e -> cancelarVenta());
        btnCerrar.addActionListener(e -> cerrarSesion());

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filtrarProductos(); }
            public void insertUpdate(DocumentEvent e) { filtrarProductos(); }
            public void removeUpdate(DocumentEvent e) { filtrarProductos(); }
        });

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                copiarCodigoSeleccionado();
            }
        });

        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaProductos.getSelectedRow() != -1) {
                    copiarCodigoSeleccionado();
                    agregarProducto();
                }
            }
        });
    }

    private void cargarProductos() {
        todosLosProductos.clear();
        for (Producto p : inventario.listarProductos()) {
            todosLosProductos.add(p);
        }
        filtrarProductos();
        lblContador.setText("Productos en inventario: " + inventario.getCantidad() + " / " + Inventario.MAX_PRODUCTOS);
    }

    private void filtrarProductos() {
        modelProductos.setRowCount(0);
        String filtro = txtBuscar.getText().trim().toLowerCase();
        for (Producto p : todosLosProductos) {
            if (filtro.isEmpty()
                    || p.getCodigo().toLowerCase().contains(filtro)
                    || p.getNombre().toLowerCase().contains(filtro)) {
                modelProductos.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock()});
            }
        }
    }

    private void copiarCodigoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila != -1) {
            txtCodigo.setText(modelProductos.getValueAt(fila, 0).toString());
            if (txtCantidad.getText().trim().isEmpty()) {
                txtCantidad.setText("1");
            }
        }
    }

    private void agregarProducto() {
        String codigo = txtCodigo.getText().trim();
        String cantStr = txtCantidad.getText().trim();
        if (codigo.isEmpty() || cantStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto o ingrese codigo y cantidad");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad invalida");
            return;
        }

        Producto p = inventario.buscarProducto(codigo);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Producto no encontrado");
            return;
        }

        try {
            ventaActual.agregarProducto(p, cantidad);
            actualizarVistaVenta();
            cargarProductos();
            txtCodigo.setText("");
            txtCantidad.setText("1");
            txtCodigo.requestFocus();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void eliminarProducto() {
        int fila = tablaVenta.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la venta");
            return;
        }

        String nombreProducto = (String) modelVenta.getValueAt(fila, 0);
        Producto productoAEliminar = null;
        for (Map.Entry<Producto, Integer> entry : ventaActual.getItems().entrySet()) {
            if (entry.getKey().getNombre().equals(nombreProducto)) {
                productoAEliminar = entry.getKey();
                break;
            }
        }

        if (productoAEliminar != null) {
            int cantidadEnVenta = ventaActual.getItems().get(productoAEliminar);
            productoAEliminar.aumentarStock(cantidadEnVenta);
            ventaActual.eliminarProducto(productoAEliminar);
            actualizarVistaVenta();
            cargarProductos();
        }
    }

    private void actualizarVistaVenta() {
        modelVenta.setRowCount(0);
        for (Map.Entry<Producto, Integer> entry : ventaActual.getItems().entrySet()) {
            Producto p = entry.getKey();
            int cant = entry.getValue();
            double sub = p.getPrecio() * cant;
            modelVenta.addRow(new Object[]{p.getNombre(), cant, sub});
        }
        lblTotal.setText(String.format("Total: $%.2f", ventaActual.getTotal()));
    }

    private void finalizarVenta() {
        if (ventaActual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos en la venta");
            return;
        }

        String ticket = ventaActual.generarTicket();
        new TicketDialog(this, ticket).setVisible(true);
        inventario.guardar();
        ventaActual.limpiar();
        actualizarVistaVenta();
        cargarProductos();
    }

    private void cancelarVenta() {
        if (!ventaActual.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Cancelar la venta actual?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (Map.Entry<Producto, Integer> entry : ventaActual.getItems().entrySet()) {
                    entry.getKey().aumentarStock(entry.getValue());
                }
                ventaActual.limpiar();
                actualizarVistaVenta();
                cargarProductos();
                inventario.guardar();
            }
        }
    }

    private void cerrarSesion() {
        dispose();
        Main.mostrarLogin();
    }
}
