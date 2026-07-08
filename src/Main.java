import business.RestauranteLN;
import ui.TextUI;

public class Main {
    public static void main(String[] args) {
        try {
            // Inicializa o sistema e carrega dados de teste (Reset da BD a cada execução para fins de demonstração)
            RestauranteLN sistema = new RestauranteLN();

            // 2. Inicializa a UI passando o sistema
            TextUI ui = new TextUI(sistema);

            // 3. Arranca o menu
            ui.run();

        } catch (Exception e) {
            System.out.println("Erro ao iniciar a aplicação:");
            e.printStackTrace();
        }
    }
}
