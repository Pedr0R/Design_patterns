package org.example.behavioral.Strategy;

import java.util.LinkedHashMap;
import java.util.Map;

// Strategy: define uma família de algoritmos, encapsula cada um em uma classe
// e os torna intercambiáveis. O contexto escolhe qual usar sem conhecer os detalhes.

// Interface da estratégia. Com um único método, também pode ser uma lambda.
@FunctionalInterface
interface EstrategiaDeFrete {
    double calcular(double pesoKg, double distanciaKm);
}

// Estratégias concretas: cada uma é um algoritmo diferente

class FreteEconomico implements EstrategiaDeFrete {
    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        return 5.00 + 0.50 * pesoKg + 0.02 * distanciaKm;
    }
}

class FreteExpresso implements EstrategiaDeFrete {
    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        return 15.00 + 1.20 * pesoKg + 0.05 * distanciaKm;
    }
}

class RetiradaNaLoja implements EstrategiaDeFrete {
    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        return 0.0;
    }
}

// Contexto: usa a estratégia, mas não sabe como o frete é calculado
class Pedido {
    private final double subtotal;
    private final double pesoKg;
    private final double distanciaKm;
    private EstrategiaDeFrete frete;

    Pedido(double subtotal, double pesoKg, double distanciaKm) {
        this.subtotal = subtotal;
        this.pesoKg = pesoKg;
        this.distanciaKm = distanciaKm;
    }

    // A estratégia pode ser trocada a qualquer momento, em tempo de execução
    void setFrete(EstrategiaDeFrete frete) {
        this.frete = frete;
    }

    double valorDoFrete() {
        return frete.calcular(pesoKg, distanciaKm);
    }

    double total() {
        return subtotal + valorDoFrete();
    }
}

public class Strategy {
    public static void main(String[] args) {
        Pedido pedido = new Pedido(250.00, 3.0, 120);

        // As opções ficam em um mapa: em vez de um switch gigante, a escolha
        // (por exemplo, vinda da tela do cliente) é uma simples busca pela chave.
        Map<String, EstrategiaDeFrete> opcoes = new LinkedHashMap<>();
        opcoes.put("economico", new FreteEconomico());
        opcoes.put("expresso", new FreteExpresso());
        opcoes.put("retirada", new RetiradaNaLoja());
        opcoes.put("promocao", (peso, distancia) -> 9.90); // estratégia como lambda

        for (Map.Entry<String, EstrategiaDeFrete> opcao : opcoes.entrySet()) {
            pedido.setFrete(opcao.getValue());
            System.out.printf("%-10s -> frete R$ %6.2f | total R$ %7.2f%n",
                    opcao.getKey(), pedido.valorDoFrete(), pedido.total());
        }

        // Adicionar uma nova modalidade (frete grátis acima de X, entrega agendada...)
        // é criar uma classe ou lambda nova. O Pedido não muda.
    }
}