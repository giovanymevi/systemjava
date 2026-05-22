package vista;

import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaAdmin extends JFrame {
    private Inventario inventario;
    private JTable tablaProductos;
    private DefaultTableModel model;
    private JTextField txtCodigo, txtNombre, txtPrecio, txtStock;
    private JLabel lblContador;

    public VentanaAdmin(Usuario usuario) {
        super("Administrador - " + usuario.getNombre());
        inventario = new Inventario();
        inicializarComponentes();
        cargarProductos();
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Codigo", "Nombre", "Precio", "Stock"}, 0);
        tablaProductos = new JTable(model);
        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del producto"));
        panelForm.setPreferredSize(new Dimension(270, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(new JLabel("Codigo:"), gbc);
        gbc.gridx = 1;
        txtCodigo = new JTextField(10);
        panelForm.add(txtCodigo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelForm.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(10);
        panelForm.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelForm.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        txtPrecio = new JTextField(10);
        panelForm.add(txtPrecio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelForm.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        txtStock = new JTextField(10);
        panelForm.add(txtStock, gbc);

        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 5, 5));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar tabla");
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnCerrar = new JButton("Cerrar sesion");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnCerrar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panelForm.add(panelBotones, gbc);

        lblContador = new JLabel("Productos en inventario: 0 / " + Inventario.MAX_PRODUCTOS);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panelForm.add(lblContador, gbc);

        add(panelForm, BorderLayout.EAST);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnModificar.addActionListener(e -> modificarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnActualizar.addActionListener(e -> cargarProductos());
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnCerrar.addActionListener(e -> cerrarSesion());

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaProductos.getSelectedRow();
                if (fila != -1) {
                    txtCodigo.setText(model.getValueAt(fila, 0).toString());
                    txtNombre.setText(model.getValueAt(fila, 1).toString());
                    txtPrecio.setText(model.getValueAt(fila, 2).toString());
                    txtStock.setText(model.getValueAt(fila, 3).toString());
                }
            }
        });
    }

    private void cargarProductos() {
        model.setRowCount(0);
        for (Producto p : inventario.listarProductos()) {
            model.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock()});
        }
        lblContador.setText("Productos en inventario: " + inventario.getCantidad() + " / " + Inventario.MAX_PRODUCTOS);
    }

    private void agregarProducto() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String stockStr = txtStock.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }
        if (inventario.existeCodigo(codigo)) {
            JOptionPane.showMessageDialog(this, "Ya existe un producto con ese codigo. Use otro codigo o presione Modificar.");
            return;
        }
        if (inventario.getCantidad() >= Inventario.MAX_PRODUCTOS) {
            JOptionPane.showMessageDialog(this, "No se pueden agregar mas productos. Limite alcanzado: " + Inventario.MAX_PRODUCTOS);
            return;
        }

        try {
            double precio = parsePrecio(precioStr);
            int stock = Integer.parseInt(stockStr);
            if (precio < 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Precio y stock no pueden ser negativos");
                return;
            }

            boolean ok = inventario.agregarProducto(new Producto(codigo, nombre, precio, stock));
            if (ok) {
                inventario.guardar();
                cargarProductos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el producto (limite alcanzado)");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y stock deben ser numeros validos");
        }
    }

    private void modificarProducto() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty() || !inventario.existeCodigo(codigo)) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla o ingrese un codigo existente");
            return;
        }

        Producto p = inventario.buscarProducto(codigo);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Error: El producto ya no existe.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String stockStr = txtStock.getText().trim();
        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }

        try {
            double precio = parsePrecio(precioStr);
            int stock = Integer.parseInt(stockStr);
            if (precio < 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Precio y stock no pueden ser negativos");
                return;
            }

            p.setNombre(nombre);
            p.setPrecio(precio);
            p.setStock(stock);
            inventario.guardar();
            cargarProductos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y stock deben ser numeros");
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla");
            return;
        }

        String codigo = (String) model.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar producto " + codigo + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            inventario.eliminarProducto(codigo);
            inventario.guardar();
            cargarProductos();
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        tablaProductos.clearSelection();
        txtCodigo.requestFocus();
    }

    private double parsePrecio(String precioStr) {
        return Double.parseDouble(precioStr.replace(',', '.'));
    }

    private void cerrarSesion() {
        dispose();
        Main.mostrarLogin();
    }
}
