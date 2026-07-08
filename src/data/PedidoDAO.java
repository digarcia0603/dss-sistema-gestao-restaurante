package data;

import business.model.*;

import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class PedidoDAO implements Map<String, Pedido> {
    private static PedidoDAO singleton = null;

    private PedidoDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {


            stm.executeUpdate("CREATE TABLE IF NOT EXISTS pedidos (" +
                    "Id VARCHAR(50) PRIMARY KEY, " +
                    "Nif VARCHAR(20), " +
                    "IdRestaurante VARCHAR(50), " +
                    "Estado VARCHAR(50), " +
                    "DataHora DATETIME, " +
                    "Total DOUBLE, " +
                    "HoraEntrega DATETIME, " +
                    "TipoEntrega VARCHAR(20), " +
                    "InfoEntrega VARCHAR(50), " +
                    "FOREIGN KEY (IdRestaurante) REFERENCES restaurantes(Id))");

            stm.executeUpdate("CREATE TABLE IF NOT EXISTS itens_pedido (" +
                    "Id INT AUTO_INCREMENT PRIMARY KEY, IdPedido VARCHAR(50), IdProduto VARCHAR(50), Nota VARCHAR(255), " +
                    "FOREIGN KEY (IdPedido) REFERENCES pedidos(Id))");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static PedidoDAO getInstance() {
        if (singleton == null) singleton = new PedidoDAO();
        return singleton;
    }

    @Override
    public Pedido get(Object key) {
        Pedido p = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM pedidos WHERE Id='" + key + "'")) {

            if (rs.next()) {
                String id = rs.getString("Id");
                String nif = rs.getString("Nif");
                String idRest = rs.getString("IdRestaurante");
                String estado = rs.getString("Estado");
                Timestamp ts = rs.getTimestamp("DataHora");
                LocalDateTime dt = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();

                Timestamp tsEnt = rs.getTimestamp("HoraEntrega");
                LocalDateTime dtEnt = (tsEnt != null) ? tsEnt.toLocalDateTime() : null;

                String tipoEnt = rs.getString("TipoEntrega");
                String infoEnt = rs.getString("InfoEntrega");

                // Recuperar Itens
                List<ItemDeVenda> itens = getItensDoPedido(id);

                p = new Pedido(id, nif, idRest, dt, estado, itens);
                p.setHoraEntrega(dtEnt);

                if ("MESA".equals(tipoEnt)) {
                    int numMesa = Integer.parseInt(infoEnt);
                    Mesa m = new Mesa(id, dtEnt, numMesa);
                    p.setEntrega(m);
                } else if ("TAKEAWAY".equals(tipoEnt)) {
                    TakeAway t = new TakeAway(id, dtEnt);
                    p.setEntrega(t);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return p;
    }

    private List<ItemDeVenda> getItensDoPedido(String idPedido) {
        List<ItemDeVenda> lista = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM itens_pedido WHERE IdPedido='" + idPedido + "'")) {
            while (rs.next()) {
                String idItem = rs.getString("IdProduto");
                String nota = rs.getString("Nota");
                ItemCatalogo itemCat = ItemCatalogoDAO.getInstance().get(idItem);
                if (itemCat != null) {
                    ItemDeVenda item = new ItemDeVenda(itemCat);
                    item.setNota(nota);
                    lista.add(item);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public Pedido put(String key, Pedido p) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)) {

            PreparedStatement stm = conn.prepareStatement(
                    "INSERT INTO pedidos (Id, Nif, IdRestaurante, Estado, DataHora, Total, HoraEntrega, TipoEntrega, InfoEntrega) " +
                            "VALUES (?,?,?,?,?,?,?,?,?) " +
                            "ON DUPLICATE KEY UPDATE Nif=VALUES(Nif), IdRestaurante=VALUES(IdRestaurante), Estado=VALUES(Estado), " +
                            "Total=VALUES(Total), HoraEntrega=VALUES(HoraEntrega), TipoEntrega=VALUES(TipoEntrega), InfoEntrega=VALUES(InfoEntrega)");

            stm.setString(1, p.getIdPedido());
            stm.setString(2, p.getNifCliente());
            stm.setString(3, p.get_idRestaurante());
            stm.setString(4, p.get_estado());
            stm.setTimestamp(5, Timestamp.valueOf(p.getDataHora()));
            stm.setDouble(6, p.getTotal());
            if (p.getHoraEntrega() != null) {
                stm.setTimestamp(7, Timestamp.valueOf(p.getHoraEntrega()));
            } else {
                stm.setNull(7, Types.TIMESTAMP);
            }

            Entrega ent = p.getEntrega();
            if (ent instanceof Mesa) {
                stm.setString(8, "MESA");
                stm.setString(9, String.valueOf(((Mesa) ent).getNumeroMesa()));
            } else if (ent instanceof TakeAway) {
                stm.setString(8, "TAKEAWAY");
                stm.setNull(9, Types.VARCHAR);
            } else {
                stm.setNull(8, Types.VARCHAR);
                stm.setNull(9, Types.VARCHAR);
            }

            stm.executeUpdate();

            conn.createStatement().executeUpdate("DELETE FROM itens_pedido WHERE IdPedido='" + key + "'");
            PreparedStatement stmItems = conn.prepareStatement("INSERT INTO itens_pedido (IdPedido, IdProduto, Nota) VALUES (?,?,?)");
            for (ItemDeVenda item : p.getItens()) {
                stmItems.setString(1, key);
                stmItems.setString(2, item.getElemento().getId());
                stmItems.setString(3, item.getNota());
                stmItems.executeUpdate();
            }
        } catch (SQLException e) { return null; }
        return p;
    }

    public List<Pedido> getPedidosPendentes() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE Estado = 'PAGO' OR Estado = 'EM PREPARACAO'";
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("Id");
                String nif = rs.getString("Nif");
                String idRest = rs.getString("IdRestaurante");
                String estado = rs.getString("Estado");
                Timestamp ts = rs.getTimestamp("DataHora");
                LocalDateTime dt = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();
                List<ItemDeVenda> itens = getItensDoPedido(id);

                lista.add(new Pedido(id, nif, idRest, dt, estado, itens));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }


    @Override
    public int size() {
        int total = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM pedidos")) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return total;
    }


    @Override public boolean isEmpty() { return false; }
    @Override public boolean containsKey(Object key) { return false; }
    @Override public boolean containsValue(Object value) { return false; }
    @Override public Pedido remove(Object key) { return null; }
    @Override public void putAll(Map<? extends String, ? extends Pedido> m) {}
    @Override public void clear() {}
    @Override public Set<String> keySet() { return null; }
    @Override public Collection<Pedido> values() { return null; }
    @Override public Set<Entry<String, Pedido>> entrySet() { return null; }
}