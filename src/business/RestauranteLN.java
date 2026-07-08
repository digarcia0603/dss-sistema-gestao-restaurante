package business;

import data.*; // Importa todos os DAOs

public class RestauranteLN {
    private IGestAtendimentoD atendimento;
    private IGestProducaoD producao;
    private IGestGestaoD gestao;
    private DBManager dbManager;

    public RestauranteLN() {

        try {
            ItemCatalogoDAO.getInstance(); // Cria tabela 'itens_catalogo'
            RestauranteDAO.getInstance();  // Cria tabela 'restaurantes'
            FuncionarioDAO.getInstance();  // Cria tabela 'funcionarios'
            IngredienteDAO.getInstance();  // Cria tabela 'ingredientes'
            PedidoDAO.getInstance();       // Cria tabelas 'pedidos' e 'itens_pedido'
        } catch (Exception e) {
            e.printStackTrace();
        }


        this.dbManager = new DBManager();
        this.dbManager.povoarBaseDeDados(); // se apagássemos esta linha oos (pedidos pendentes, etc.) iria persistir caso corressemos o main outra vez.

        this.atendimento = new AtendimentoFacadeD();
        this.producao = new ProducaoFacadeD();
        this.gestao = new GestaoFacadeD();
    }

    public IGestAtendimentoD getAtendimento() { return atendimento; }
    public IGestProducaoD getProducao() { return producao; }
    public IGestGestaoD getGestao() { return gestao; }
}