package org.example.singleton;

import java.util.HashMap;
import java.util.Map;

// Singleton: garante que uma classe tenha UMA única instância
// e fornece um ponto de acesso global a ela.

// 1) Eager (inicialização antecipada): simples e thread-safe,
//    mas a instância é criada mesmo que nunca seja usada.
class ConfiguracaoEager {
    private static final ConfiguracaoEager INSTANCIA = new ConfiguracaoEager();

    private ConfiguracaoEager() { }

    static ConfiguracaoEager getInstance() {
        return INSTANCIA;
    }
}

// 2) Lazy com double-checked locking: cria só quando necessário.
//    O "volatile" é obrigatório para funcionar corretamente.
class ConexaoBanco {
    private static volatile ConexaoBanco instancia;

    private ConexaoBanco() {
        System.out.println("Criando conexão com o banco...");
    }

    static ConexaoBanco getInstance() {
        if (instancia == null) {                       // 1ª checagem (sem lock, rápida)
            synchronized (ConexaoBanco.class) {
                if (instancia == null) {               // 2ª checagem (com lock)
                    instancia = new ConexaoBanco();
                }
            }
        }
        return instancia;
    }
}

// 3) Holder idiom (Initialization-on-demand holder): lazy e thread-safe,
//    sem synchronized. A JVM garante a inicialização segura da classe interna.
class Logger {
    private Logger() { }

    private static class Holder {
        static final Logger INSTANCIA = new Logger();
    }

    static Logger getInstance() {
        return Holder.INSTANCIA;
    }

    void log(String mensagem) {
        System.out.println("[LOG] " + mensagem);
    }
}

// 4) Enum: a forma mais simples e robusta (Effective Java).
//    Thread-safe e protegida contra reflexão e serialização.
enum Cache {
    INSTANCIA;

    private final Map<String, String> dados = new HashMap<>();

    void guardar(String chave, String valor) {
        dados.put(chave, valor);
    }

    String buscar(String chave) {
        return dados.get(chave);
    }
}

public class Singleton {
    public static void main(String[] args) throws InterruptedException {
        // Mesma instância sempre
        System.out.println("Eager é a mesma instância? "
                + (ConfiguracaoEager.getInstance() == ConfiguracaoEager.getInstance()));

        System.out.println("Holder é a mesma instância? "
                + (Logger.getInstance() == Logger.getInstance()));
        Logger.getInstance().log("Aplicação iniciada");

        // Enum: estado compartilhado em qualquer ponto do código
        Cache.INSTANCIA.guardar("usuario", "Ana");
        System.out.println("Cache: " + Cache.INSTANCIA.buscar("usuario"));

        // Várias threads disputando a criação: a mensagem deve aparecer UMA só vez
        System.out.println("--- Teste com várias threads ---");
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(ConexaoBanco::getInstance);
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }
}