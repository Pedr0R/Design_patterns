package org.example.structural.Decorator;

import java.util.HashMap;
import java.util.Map;

// Decorator: adiciona comportamentos a um objeto dinamicamente,
// "embrulhando-o" em outros objetos que implementam a mesma interface.

// Componente (interface comum)
interface BuscadorDeProdutos {
    String buscar(int id);
}

// Componente concreto: o objeto "de verdade", que faz o trabalho principal.
// Simula um banco lento e que falha na primeira chamada.
class BuscadorBancoDados implements BuscadorDeProdutos {
    private boolean primeiraChamada = true;

    @Override
    public String buscar(int id) {
        System.out.println("   [BANCO] consultando produto " + id);
        dormir(100);
        if (primeiraChamada) {
            primeiraChamada = false;
            throw new RuntimeException("Timeout no banco");
        }
        return "Produto-" + id;
    }

    private static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Decorator base: implementa a mesma interface e guarda o objeto embrulhado
abstract class BuscadorDecorator implements BuscadorDeProdutos {
    protected final BuscadorDeProdutos interno;

    protected BuscadorDecorator(BuscadorDeProdutos interno) {
        this.interno = interno;
    }
}

// Decorator concreto 1: cache em memória
class CacheDecorator extends BuscadorDecorator {
    private final Map<Integer, String> cache = new HashMap<>();

    CacheDecorator(BuscadorDeProdutos interno) {
        super(interno);
    }

    @Override
    public String buscar(int id) {
        if (cache.containsKey(id)) {
            System.out.println("   [CACHE] acerto para o produto " + id);
            return cache.get(id);
        }
        String resultado = interno.buscar(id);
        cache.put(id, resultado);
        return resultado;
    }
}

// Decorator concreto 2: tenta de novo em caso de falha
class RetryDecorator extends BuscadorDecorator {
    private final int maxTentativas;

    RetryDecorator(BuscadorDeProdutos interno, int maxTentativas) {
        super(interno);
        this.maxTentativas = maxTentativas;
    }

    @Override
    public String buscar(int id) {
        RuntimeException ultimaFalha = null;
        for (int tentativa = 1; tentativa <= maxTentativas; tentativa++) {
            try {
                return interno.buscar(id);
            } catch (RuntimeException e) {
                ultimaFalha = e;
                System.out.println("   [RETRY] tentativa " + tentativa + " falhou: " + e.getMessage());
            }
        }
        throw ultimaFalha;
    }
}

// Decorator concreto 3: registra o tempo de cada chamada
class LogDecorator extends BuscadorDecorator {
    LogDecorator(BuscadorDeProdutos interno) {
        super(interno);
    }

    @Override
    public String buscar(int id) {
        long inicio = System.nanoTime();
        String resultado = interno.buscar(id);
        long ms = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("[LOG] buscar(" + id + ") -> " + resultado + " em " + ms + "ms");
        return resultado;
    }
}

public class Decorator {
    public static void main(String[] args) {
        // Os decorators são empilhados como camadas: Log -> Cache -> Retry -> Banco
        BuscadorDeProdutos servico =
                new LogDecorator(
                        new CacheDecorator(
                                new RetryDecorator(
                                        new BuscadorBancoDados(), 3)));

        System.out.println("1ª chamada (banco falha, retry resolve):");
        servico.buscar(1);

        System.out.println("\n2ª chamada (mesmo produto, vem do cache):");
        servico.buscar(1);

        System.out.println("\n3ª chamada (produto novo, vai ao banco):");
        servico.buscar(2);

        // Quem usa "servico" só conhece BuscadorDeProdutos e não sabe
        // quantas camadas existem. Dá para montar outras combinações
        // (só cache, só log, retry + log...) sem criar nenhuma classe nova.
    }
}