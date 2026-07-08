package data;

import business.model.ItemCatalogo;
import business.model.Produto;
import business.model.Menu;
import java.sql.*;
import java.util.*;

public class ItemCatalogoDAO implements Map<String, ItemCatalogo> {
    private static ItemCatalogoDAO singleton = null;

    private ItemCatalogoDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS itens_catalogo (" +
                    "Id VARCHAR(50) NOT NULL PRIMARY KEY," +
                    "Nome VARCHAR(100)," +
                    "Preco DOUBLE," +
                    "Tipo VARCHAR(10) CHECK (Tipo IN ('PRODUTO', 'MENU')))";
            stm.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ItemCatalogoDAO getInstance() {
        if (singleton == null) singleton = new ItemCatalogoDAO();
        return singleton;
    }

    @Override
    public ItemCatalogo get(Object key) {
        ItemCatalogo item = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM itens_catalogo WHERE Id='" + key + "'")) {

            if (rs.next()) {
                String nome = rs.getString("Nome");
                double preco = rs.getDouble("Preco");
                String tipo = rs.getString("Tipo");

                if ("PRODUTO".equals(tipo)) {
                    Produto p = new Produto(nome);
                    p.setPrecoBase(preco);
                    p.setId((String) key);

                    List<String> ings = getIngredientesDoProduto((String) key);
                    p.setIngredientes(ings);

                    item = p;
                } else if ("MENU".equals(tipo)) {
                    item = new Menu(nome, preco);
                }

                if (item != null) {
                    item.setId((String) key); // Guarda o ID "P1" dentro do objeto
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public List<Produto> getProdutos() {
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM itens_catalogo WHERE Tipo='PRODUTO'")) {
            while (rs.next()) {
                Produto p = new Produto(rs.getString("Nome"));
                p.setPrecoBase(rs.getDouble("Preco"));
                String id = rs.getString("Id");
                p.setId(id);

                p.setIngredientes(getIngredientesDoProduto(id));

                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Menu> getMenus() {
        List<Menu> lista = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM itens_catalogo WHERE Tipo='MENU'")) {

            while (rs.next()) {
                String nome = rs.getString("Nome");
                double preco = rs.getDouble("Preco");

                Menu m = new Menu(nome, preco);
                m.setId(rs.getString("Id"));
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    @Override
    public ItemCatalogo put(String key, ItemCatalogo value) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement stm = conn.prepareStatement(
                     "INSERT INTO itens_catalogo (Id, Nome, Preco, Tipo) VALUES (?,?,?,?) " +
                             "ON DUPLICATE KEY UPDATE Nome=VALUES(Nome), Preco=VALUES(Preco), Tipo=VALUES(Tipo)")) {

            stm.setString(1, key);
            stm.setString(2, value.getNome());
            stm.setDouble(3, value.getPrecoBase());

            // Distinguir se é Produto ou Menu
            if (value instanceof Menu) {
                stm.setString(4, "MENU");
            } else {
                stm.setString(4, "PRODUTO");
            }

            stm.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    private List<String> getIngredientesDoProduto(String idProduto) {
        List<String> lista = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             ResultSet rs = conn.createStatement().executeQuery("SELECT IdIngrediente FROM receitas WHERE IdProduto='" + idProduto + "'")) {
            while (rs.next()) {
                lista.add(rs.getString("IdIngrediente"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }


    @Override public int size() { return 0; }
    @Override public boolean isEmpty() { return false; }
    @Override public boolean containsKey(Object key) { return false; }
    @Override public boolean containsValue(Object value) { return false; }
    @Override public ItemCatalogo remove(Object key) { return null; }
    @Override public void putAll(Map<? extends String, ? extends ItemCatalogo> m) {}
    @Override public void clear() {}
    @Override public Set<String> keySet() { return null; }
    @Override public Collection<ItemCatalogo> values() { return null; }
    @Override public Set<Entry<String, ItemCatalogo>> entrySet() { return null; }
}