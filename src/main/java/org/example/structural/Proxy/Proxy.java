package org.example.structural.Proxy;// Proxy: fornece um substituto (ou "representante") de outro objeto para
// controlar o acesso a ele. O proxy implementa a mesma interface do objeto real.

enum Papel {
    FUNCIONARIO,
    GERENTE
}

// Interface comum ao objeto real e aos proxies
interface Relatorio {
    String ler();
}

// Objeto real (RealSubject): criar é caro, pois simula uma leitura lenta do disco
class RelatorioReal implements Relatorio {
    private final String conteudo;

    RelatorioReal(String titulo) {
        System.out.println("   [REAL] carregando '" + titulo + "' do disco (operação lenta)...");
        dormir(200);
        this.conteudo = "Conteúdo confidencial de " + titulo;
    }

    @Override
    public String ler() {
        return conteudo;
    }

    private static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Proxy VIRTUAL: adia a criação do objeto caro até o primeiro uso (lazy loading)
class RelatorioVirtualProxy implements Relatorio {
    private final String titulo;
    private RelatorioReal real; // só é criado quando necessário

    RelatorioVirtualProxy(String titulo) {
        this.titulo = titulo;
        System.out.println("   [PROXY VIRTUAL] criado para '" + titulo + "' (nada foi carregado)");
    }

    @Override
    public String ler() {
        if (real == null) {
            real = new RelatorioReal(titulo);
        }
        return real.ler();
    }
}

// Proxy de PROTEÇÃO: controla quem pode acessar o objeto
class RelatorioProtecaoProxy implements Relatorio {
    private final Relatorio alvo;
    private final Papel papelDoUsuario;

    RelatorioProtecaoProxy(Relatorio alvo, Papel papelDoUsuario) {
        this.alvo = alvo;
        this.papelDoUsuario = papelDoUsuario;
    }

    @Override
    public String ler() {
        if (papelDoUsuario != Papel.GERENTE) {
            throw new SecurityException("Acesso negado para o papel " + papelDoUsuario);
        }
        return alvo.ler();
    }
}

public class Proxy {
    public static void main(String[] args) {
        // Os proxies são encadeados: Proteção -> Virtual -> Real
        Relatorio virtual = new RelatorioVirtualProxy("Balanço 2026");

        System.out.println("\n1) Funcionário tenta ler:");
        Relatorio paraFuncionario = new RelatorioProtecaoProxy(virtual, Papel.FUNCIONARIO);
        try {
            paraFuncionario.ler();
        } catch (SecurityException e) {
            // O acesso foi barrado ANTES de carregar o relatório, então não houve custo
            System.out.println("   " + e.getMessage());
        }

        System.out.println("\n2) Gerente lê pela primeira vez (carrega agora):");
        Relatorio paraGerente = new RelatorioProtecaoProxy(virtual, Papel.GERENTE);
        System.out.println("   " + paraGerente.ler());

        System.out.println("\n3) Gerente lê de novo (já carregado, sem custo):");
        System.out.println("   " + paraGerente.ler());
    }
}