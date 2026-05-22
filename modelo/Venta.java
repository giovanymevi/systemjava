package modelo;

import java.util.HashMap;
import java.util.Map;

public class Venta {
    private Map<Producto, Integer> items = new HashMap<>();

    public void agregarProducto(Producto p, int cantidad) {
        if (p.getStock() < cantidad) throw new IllegalArgumentException("Stock insuficiente para " + p.getNombre());
        p.disminuirStock(cantidad);
        items.put(p, items.getOrDefault(p, 0) + cantidad);
    }

    public void eliminarProducto(Producto p) { items.remove(p); }

    public Map<Producto, Integer> getItems() { return items; }

    public double getTotal() {
        return items.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrecio() * e.getValue())
                .sum();
    }

    public boolean isEmpty() { return items.isEmpty(); }
    public void limpiar() { items.clear(); }

    public String generarTicket() {
        StringBuilder sb = new StringBuilder("--- TICKET DE VENTA ---\n\n");
        items.forEach((p, c) -> {
            sb.append(String.format("%-15s x%d  $%.2f\n", p.getNombre(), c, p.getPrecio() * c));
        });
        sb.append("\n-----------------------\n");
        sb.append(String.format("TOTAL: $%.2f", getTotal()));
        return sb.toString();
    }
}