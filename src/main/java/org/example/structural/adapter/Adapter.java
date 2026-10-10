package org.example.structural.adapter;// Adapter: converte a interface de uma classe existente na interface
// que o cliente espera, permitindo que classes incompatíveis trabalhem juntas.

// Interface esperada pelo cliente (Target)
interface Pagamento {
    void pagar(double valorEmReais);
}

// Implementação nativa, já compatível com o Target
class PagamentoPix implements Pagamento {
    @Override
    public void pagar(double valorEmReais) {
        System.out.printf("[PIX] Pagamento de R$ %.2f realizado%n", valorEmReais);
    }
}

// Classe existente e incompatível (Adaptee): API legada que não podemos alterar.
// Espera o valor em CENTAVOS e tem outro nome de método.
class SistemaLegado {
    void efetuarCobranca(int valorEmCentavos) {
        System.out.println("[LEGADO] Cobrança de " + valorEmCentavos + " centavos efetuada");
    }
}

// Adapter: implementa o Target e delega para o Adaptee, traduzindo as chamadas
class PagamentoLegadoAdapter implements Pagamento {
    private final SistemaLegado legado;

    PagamentoLegadoAdapter(SistemaLegado legado) {
        this.legado = legado;
    }

    @Override
    public void pagar(double valorEmReais) {
        int centavos = (int) Math.round(valorEmReais * 100); // conversão de unidade
        legado.efetuarCobranca(centavos);                    // tradução da chamada
    }
}

// Cliente: conhece apenas a interface Pagamento
class Checkout {
    private final Pagamento pagamento;

    Checkout(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    void finalizarCompra(double total) {
        System.out.println("Finalizando compra...");
        pagamento.pagar(total);
    }
}

public class Adapter {
    public static void main(String[] args) {
        // Com a implementação nativa
        new Checkout(new PagamentoPix()).finalizarCompra(49.90);

        System.out.println();

        // Com o sistema legado, adaptado: o Checkout nem percebe a diferença
        Pagamento adaptado = new PagamentoLegadoAdapter(new SistemaLegado());
        new Checkout(adaptado).finalizarCompra(49.90);
    }
}