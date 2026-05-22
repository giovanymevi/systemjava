package modelo;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    public static final int MAX_PRODUCTOS = 100;
    private static List<Producto> productos = new ArrayList<>();

    static {
        // Productos iniciales de prueba
        productos.add(new Producto("001", "Leche", 2.50, 50));
        productos.add(new Producto("002", "Pan", 0.50, 100));
    }

    public List<Producto> listarProductos() { return productos; }

    public boolean agregarProducto(Producto p) {
        if (productos.size() < MAX_PRODUCTOS) {
            productos.add(p);
            return true;
        }
        return false;
    }

    public void eliminarProducto(String codigo) {
        productos.removeIf(p -> p.getCodigo().equals(codigo));
    }

    public Producto buscarProducto(String codigo) {
        return productos.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst().orElse(null);
    }

    public boolean existeCodigo(String codigo) {
        return buscarProducto(codigo) != null;
    }

    public int getCantidad() { return productos.size(); }
    
    public void guardar() { /* Simulación de persistencia */ }
}