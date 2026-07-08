package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {

    private static final String URL = DAOconfig.URL;
    private static final String USERNAME = DAOconfig.USERNAME;
    private static final String PASSWORD = DAOconfig.PASSWORD;

    public void povoarBaseDeDados() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement stm = conn.createStatement()) {


            System.out.println(">> A limpar base de dados...");
            limparTabelas(stm);

            // 2. Inserir Funcionários (Gestor e Funcionários normais)
            System.out.println(">> A povoar Funcionários...");
            String sqlFunc = "INSERT INTO funcionarios (Id, Nome, Funcao, Username, Password, Tipo) VALUES " +
                    "('F1', 'João Cozinheiro', 'Cozinheiro', NULL, NULL, 'FUNCIONARIO'), " +
                    "('F2', 'Maria Mesa', 'EmpregadoMesa', NULL, NULL, 'FUNCIONARIO'), " +
                    "('G1', 'Carlos Chefe', 'Gerente', 'admin', 'admin', 'GESTOR')";
            stm.executeUpdate(sqlFunc);

            // 3. Inserir Restaurantes
            System.out.println(">> A povoar Restaurantes...");
            String sqlRest = "INSERT INTO restaurantes (Id, Localizacao, Balcao) VALUES " +
                    "('R1', 'Lisboa - Baixa', 20), " +
                    "('R2', 'Porto - Ribeira', 15)";
            stm.executeUpdate(sqlRest);

            // 4. Inserir Ingredientes
            System.out.println(">> A povoar Ingredientes...");
            String sqlIng = "INSERT INTO ingredientes (Id, Stock) VALUES " +
                    "('ING-BATATA', 500), " +
                    "('ING-ARROZ', 200), " +
                    "('ING-CARNE', 100), " +
                    "('ING-PEIXE', 80)";
            stm.executeUpdate(sqlIng);

            // 5. Inserir Catálogo (Produtos e Menus)
            System.out.println(">> A povoar Catálogo...");
            String sqlCat = "INSERT INTO itens_catalogo (Id, Nome, Preco, Tipo) VALUES " +
                    "('P1', 'Bitoque', 12.50, 'PRODUTO'), " +
                    "('P2', 'Bacalhau à Brás', 14.00, 'PRODUTO'), " +
                    "('P3', 'Refrigerante', 2.00, 'PRODUTO'), " +
                    "('M1', 'Menu Estudante', 8.50, 'MENU')";
            stm.executeUpdate(sqlCat);

            // 6. Inserir Receitas (Ligação Produto -> Ingredientes)
            System.out.println(">> A povoar Receitas...");
            // Cria a tabela aqui (ou no DAO, mas aqui garante que existe para o teste)
            stm.executeUpdate("CREATE TABLE IF NOT EXISTS receitas (" +
                    "IdProduto VARCHAR(50), IdIngrediente VARCHAR(50))");

            // Limpa receitas antigas
            stm.executeUpdate("DELETE FROM receitas");

            // Bitoque (P1) gasta Batata e Carne
            stm.executeUpdate("INSERT INTO receitas (IdProduto, IdIngrediente) VALUES ('P1', 'ING-BATATA')");
            stm.executeUpdate("INSERT INTO receitas (IdProduto, IdIngrediente) VALUES ('P1', 'ING-CARNE')");

            // Bacalhau (P2) gasta Peixe e Batata
            stm.executeUpdate("INSERT INTO receitas (IdProduto, IdIngrediente) VALUES ('P2', 'ING-PEIXE')");
            stm.executeUpdate("INSERT INTO receitas (IdProduto, IdIngrediente) VALUES ('P2', 'ING-BATATA')");

            System.out.println(">> Base de dados povoada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao povoar BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limparTabelas(Statement stm) throws SQLException {
        stm.executeUpdate("DELETE FROM itens_pedido");
        stm.executeUpdate("DELETE FROM pedidos");


        stm.executeUpdate("DELETE FROM itens_catalogo");
        stm.executeUpdate("DELETE FROM ingredientes");
        stm.executeUpdate("DELETE FROM restaurantes");
        stm.executeUpdate("DELETE FROM funcionarios");
    }
}