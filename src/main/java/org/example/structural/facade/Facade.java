package org.example.structural.facade;

import java.util.HashMap;
import java.util.Map;

// Facade: fornece uma interface simples para um conjunto complexo de classes
// (um subsistema), escondendo os detalhes de como elas se coordenam.

// ===== Subsistemas: classes especializadas, cada uma com sua API =====

class ServicoEstoque {
    private final Map<String, Integer> saldo = new HashMap<>();

    ServicoEstoque() {
        saldo.put("NOTEBOOK", 5);
        saldo.put("MOUSE", 50);
    }

    boolean reservar(String sku, int quantidade) {
        int atual = saldo.getOrDefault(sku, 0);
        if (atual < quantidade) {
            System.out.println("   [ESTOQUE] sem saldo de " + sku + " (disponível: " + atual + ")");
            return false;
        }
        saldo.put(sku, atual - quantidade);
        System.out.println("   [ESTOQUE] reservadas " + quantidade + " un. de " + sku);
        return true;
    }

    void liberar(String sku, int quantidade) {
        saldo.merge(sku, quantidade, Integer::sum);
        System.out.println("   [ESTOQUE] reserva liberada: " + quantidade + " un. de " + sku);
    }
}

class ServicoFrete {
    double calcular(String cep) {
        return cep.startsWith("2") ? 19.90 : 39.90; // CEPs iniciados em 2 = região mais próxima
    }

    void agendarEntrega(String cep) {
        System.out.println("   [FRETE] entrega agendada para o CEP " + cep);
    }
}

class ServicoPagamento {
    private static final double LIMITE_APROVACAO = 5000.00;

    boolean cobrar(String cliente, double valor) {
        if (valor > LIMITE_APROVACAO) {
            System.out.printf("   [PAGAMENTO] R$ %.2f recusado para %s%n", valor, cliente);
            return false;
        }
        System.out.printf("   [PAGAMENTO] R$ %.2f aprovado para %s%n", valor, cliente);
        return true;
    }
}

class ServicoNotificacao {
    void enviar(String email, String mensagem) {
        System.out.println("   [NOTIFICAÇÃO] para " + email + ": " + mensagem);
    }
}

// ===== Facade: uma única operação que orquestra tudo =====
class CheckoutFacade {
    private final ServicoEstoque estoque;
    private final ServicoFrete frete;
    private final ServicoPagamento pagamento;
    private final ServicoNotificacao notificacao;

    CheckoutFacade() {
        this.estoque = new ServicoEstoque();
        this.frete = new ServicoFrete();
        this.pagamento = new ServicoPagamento();
        this.notificacao = new ServicoNotificacao();
    }

    boolean finalizarCompra(String cliente, String email, String sku,
                            int quantidade, double precoUnitario, String cep) {
        // 1) Reserva o estoque
        if (!estoque.reservar(sku, quantidade)) {
            notificacao.enviar(email, "Item indisponível no momento.");
            return false;
        }

        // 2) Calcula o total e cobra
        double total = precoUnitario * quantidade + frete.calcular(cep);
        if (!pagamento.cobrar(cliente, total)) {
            estoque.liberar(sku, quantidade); // desfaz a reserva
            notificacao.enviar(email, "Pagamento recusado. Tente outro meio de pagamento.");
            return false;
        }

        // 3) Agenda a entrega e avisa o cliente
        frete.agendarEntrega(cep);
        notificacao.enviar(email, String.format("Compra confirmada! Total: R$ %.2f", total));
        return true;
    }
}

public class Facade {
    public static void main(String[] args) {
        // O cliente conhece apenas a fachada, não os quatro subsistemas
        CheckoutFacade checkout = new CheckoutFacade();

        System.out.println("Cenário 1: compra normal");
        checkout.finalizarCompra("Ana", "ana@exemplo.com", "NOTEBOOK", 1, 3500.00, "20040-020");

        System.out.println("\nCenário 2: pagamento recusado (estoque é devolvido)");
        checkout.finalizarCompra("Bruno", "bruno@exemplo.com", "NOTEBOOK", 2, 3500.00, "20040-020");

        System.out.println("\nCenário 3: sem estoque");
        checkout.finalizarCompra("Carla", "carla@exemplo.com", "NOTEBOOK", 10, 3500.00, "20040-020");

        // Sem a fachada, TODO código que precisasse finalizar uma compra teria de
        // repetir essa sequência: reservar, calcular frete, cobrar, desfazer a reserva
        // se falhar, agendar entrega e notificar, na ordem certa.
    }
}