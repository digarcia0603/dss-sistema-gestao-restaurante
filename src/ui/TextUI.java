package ui;

import business.RestauranteLN;
import business.exceptions.PedidoNaoEncontradoException;
import business.model.*;

import java.util.List;
import java.util.Scanner;

public class TextUI {
    private RestauranteLN sistema;
    private Scanner sc;
    private String idRestauranteAtual;

    public TextUI(RestauranteLN sistema) {
        this.sistema = sistema;
        this.sc = new Scanner(System.in);
        this.idRestauranteAtual = null;
    }

    public void run() throws PedidoNaoEncontradoException {
        System.out.println("Bem-vindo ao Sistema de Gestão de Restaurante!");
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Iniciar Atendimento");
            System.out.println("2. Confecionar Pedidos");
            System.out.println("3. Iniciar Sessão Gestor");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                String line = sc.nextLine();
                opcao = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    menuAtendimento();
                    break;
                case 2:
                    menuProducao();
                    break;
                case 3:
                    menuGestao();
                    break;
                case 0:
                    System.out.println("A encerrar sistema...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }


    private void menuAtendimento() throws PedidoNaoEncontradoException {
        boolean idValido = false;

        while (!idValido) {
            System.out.print("Em que Restaurante estamos a trabalhar? (ex: R1, R2) ou 0 para sair: ");
            String idTemp = sc.nextLine();

            if (idTemp.equals("0")) return;

            boolean existe = false;

            for (Restaurante r : sistema.getGestao().getRestaurantes()) {
                if (r.getIdRestaurante().equals(idTemp)) {
                    existe = true;
                    break;
                }
            }

            if (existe) {
                this.idRestauranteAtual = idTemp;
                idValido = true;
                System.out.println(">> Restaurante " + this.idRestauranteAtual + " definido com sucesso.");
            } else {
                System.out.println(">> Erro: O restaurante '" + idTemp + "' não existe na base de dados. Tente novamente.");
            }
        }
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- ATENDIMENTO ---");
            System.out.println("1. Ver Catálogo de Produtos");
            System.out.println("2. Iniciar Novo Pedido (Cliente chega)");
            System.out.println("3. Ver Catálogo de Menus");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) { opcao = -1; }

            switch (opcao) {
                case 1:
                    listarProdutos();
                    break;
                case 2:
                    fluxoRegistarPedido();
                    break;
                case 3:
                    listarMenus();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void fluxoRegistarPedido() throws PedidoNaoEncontradoException {
        System.out.println("\n>> NOVO PEDIDO (Restaurante: " + this.idRestauranteAtual + ")");

        System.out.print("Insira o NIF do Cliente (ou Enter para anónimo): ");
        String nif = sc.nextLine();


        String idPedido = sistema.getAtendimento().registarPedido(nif, this.idRestauranteAtual);

        if (idPedido != null) {
            System.out.println("Pedido " + idPedido + " inicializado.");


            System.out.println("\nOnde será a refeição?");
            System.out.println("1. Mesa (Atribuição Automática)");
            System.out.println("2. Take-Away");
            System.out.print("Opção: ");
            String opTipo = sc.nextLine();

            try {
                if (opTipo.equals("1")) {
                    sistema.getAtendimento().definirTipoEntrega(idPedido, 1);
                } else {
                    sistema.getAtendimento().definirTipoEntrega(idPedido, 2);
                }

                menuAdicionarItens(idPedido);

            } catch (Exception e) {
                System.out.println("Erro ao definir entrega: " + e.getMessage());
            }

        } else {
            System.out.println("Erro ao criar pedido.");
        }
    }

    private void menuAdicionarItens(String idPedido) throws PedidoNaoEncontradoException {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Gerir Pedido " + idPedido + " ---");
            System.out.println("1. Adicionar Item");
            System.out.println("2. Ver Produtos Disponíveis");
            System.out.println("3. Remover Item");
            System.out.println("4. Adicionar Nota a Item");
            System.out.println("0. Finalizar Pedido");
            System.out.print("Opção: ");

            String op = sc.nextLine();

            switch (op) {
                case "1":
                    System.out.print("ID do Produto (ex: P1, P2): ");
                    String idProd = sc.nextLine();
                    try {
                        sistema.getAtendimento().adicionarItem(idPedido, idProd);
                    } catch (PedidoNaoEncontradoException e) {
                        System.out.println("Erro: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Erro genérico: " + e.getMessage());
                    }
                    break;
                case "2":
                    listarProdutos();
                    break;
                case "3":
                    try {
                        System.out.println(">> Itens no Pedido Atual:");
                        List<business.model.ItemDeVenda> atuais = sistema.getAtendimento().getItensPedido(idPedido);

                        if (atuais.isEmpty()) {
                            System.out.println("   (Vazio)");
                        } else {
                            for (business.model.ItemDeVenda iv : atuais) {
                                System.out.println("   - " + iv.toString() + " [ID Original: " + iv.getElemento().getId() + "]");
                            }
                        }

                        System.out.print("ID do Produto a remover (ex: P1 ou M1): ");
                        String idRem = sc.nextLine();

                        sistema.getAtendimento().removerItem(idPedido, idRem);

                    } catch (PedidoNaoEncontradoException e) {
                        System.out.println("Erro: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Erro genérico: " + e.getMessage());
                    }
                    break;
                case "4":
                    System.out.print("ID do Produto (ex: P1): ");
                    String idNota = sc.nextLine();
                    System.out.print("Nota (ex: Sem sal): ");
                    String texto = sc.nextLine();
                    try {
                        sistema.getAtendimento().adicionarNota(idPedido, idNota, texto);
                    } catch (PedidoNaoEncontradoException e) {
                        System.out.println("❌ Erro: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Erro genérico: " + e.getMessage());
                    }
                    break;
                case "0":
                    boolean sucesso = sistema.getAtendimento().finalizarPedido(idPedido);

                    if (sucesso) {
                        System.out.println(">> Conta fechada com sucesso!");
                        System.out.println(">> Pedido enviado para a Cozinha.");
                        continuar = false;
                    } else {
                        System.out.println("Erro ao finalizar pedido (Pedido não encontrado?).");
                    }
                    break;
            }
        }
    }

    private void menuProducao() throws PedidoNaoEncontradoException {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- COZINHA / PRODUÇÃO ---");
            System.out.println("1. Listar Pedidos Pendentes");
            System.out.println("2. Iniciar Preparação de Pedido");
            System.out.println("3. Registar Entrega (Pronto)");
            System.out.println("4. Registar Rutura de Stock (Ingrediente)");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) { opcao = -1; }

            switch (opcao) {
                case 1:
                    listarPedidosCozinha();
                    break;
                case 2:
                    System.out.print("ID do Pedido a preparar: ");
                    String idPrep = sc.nextLine();
                    try {
                        sistema.getProducao().iniciarPreparacao(idPrep);
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("ID do Pedido entregue: ");
                    String idEnt = sc.nextLine();
                    try {
                        sistema.getProducao().registarEntrega(idEnt);
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.print("ID do Ingrediente em falta (ex: ING-BATATA): ");
                    String idIng = sc.nextLine();
                    sistema.getProducao().registarRuturaStock(idIng);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void menuGestao() {
        System.out.println("\n--- ÁREA DE GESTÃO (Login Necessário) ---");
        System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        boolean autenticado = sistema.getGestao().autenticarGestor(user, pass);

        if (!autenticado) {
            System.out.println(">> Login falhou! Credenciais erradas ou não é Gestor.");
            return;
        }

        System.out.println(">> Login efetuado com sucesso. Bem-vindo, Gestor!");

        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- GESTÃO ---");
            System.out.println("1. Listar Restaurantes");
            System.out.println("2. Ver Estatísticas (Stub)");
            System.out.println("0. Voltar / Logout");
            System.out.print("Opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) { opcao = -1; }

            switch (opcao) {
                case 1:
                    listarRestaurantes();
                    break;
                case 2:
                    System.out.print("ID do Restaurante (ex: R1): ");
                    String r = sc.nextLine();

                    java.time.LocalDate hoje = java.time.LocalDate.now();

                    double total = sistema.getGestao().getFaturacao(r, hoje, hoje);
                    System.out.println(">> Faturação Hoje no " + r + ": " + total + "€");

                    double media = sistema.getGestao().getTempoMedioEspera(r, hoje, hoje);
                    System.out.println(">> Tempo Médio de Espera: " + media + " segundos");
                    break;
                case 0:
                    System.out.println("A fazer logout...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // --- Métodos Auxiliares ---

    private void listarProdutos() {
        try {
            List<Produto> produtos = sistema.getAtendimento().getCatalogoProdutos();
            if (produtos.isEmpty()) {
                System.out.println(">> Catálogo vazio.");
            } else {
                System.out.println(">> Lista de Produtos:");
                for (Produto p : produtos) {
                    System.out.println("- " + p.getNome() + " | " + p.getPrecoBase() + "€");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao aceder à BD: " + e.getMessage());
        }
    }

    private void listarRestaurantes() {
        try {
            List<Restaurante> rests = sistema.getGestao().getRestaurantes();
            if (rests.isEmpty()) {
                System.out.println(">> Nenhum restaurante registado.");
            } else {
                System.out.println(">> Restaurantes:");
                for (Restaurante r : rests) {
                    System.out.println("- ID: " + r.getIdRestaurante() + " | Local: " + r.getLocalizacao());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao aceder à BD: " + e.getMessage());
        }
    }

    private void listarMenus() {
        try {
            List<Menu> menus = sistema.getAtendimento().getCatalogoMenus();
            if (menus.isEmpty()) {
                System.out.println(">> Nenhum Menu disponível.");
            } else {
                System.out.println(">> Lista de Menus:");
                for (Menu m : menus) {
                    System.out.println("- [MENU] " + m.getNome() + " | " + m.getPrecoBase() + "€");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao aceder à BD: " + e.getMessage());
        }
    }


    private void listarPedidosCozinha() {
        List<Pedido> lista = sistema.getProducao().listarPedidosPendentes();

        if (lista.isEmpty()) {
            System.out.println(">> Tudo limpo! Sem pedidos pendentes.");
        } else {
            System.out.println(">> PEDIDOS EM FILA:");

            for (Pedido p : lista) {
                System.out.println("\n>>> PEDIDO #" + p.getIdPedido() + " [" + p.get_estado() + "] <<<");

                List<ItemDeVenda> itens = p.getItens();
                if (itens.isEmpty()) {
                    System.out.println("   (Pedido vazio??)");
                } else {
                    for (ItemDeVenda item : itens) {
                        String nomePrato = item.getElemento().getNome();
                        String nota = item.getNota();


                        if (nota != null && !nota.isEmpty()) {
                            System.out.println("   - " + nomePrato + " -> Nota: " + nota);
                        } else {
                            System.out.println("   - " + nomePrato);
                        }
                    }
                }
                System.out.println("================================");
            }
        }
    }
}