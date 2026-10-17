package org.example.structural.composite;

import java.util.ArrayList;
import java.util.List;

// Composite: compõe objetos em estruturas de árvore e permite tratar
// objetos individuais (folhas) e grupos de objetos (composições) da mesma forma.

// Componente: interface comum a arquivos e pastas
interface ItemSistemaArquivos {
    String getNome();

    long getTamanhoEmBytes();

    int contarArquivos();

    void exibir(String recuo);
}

// Folha (Leaf): não tem filhos
class Arquivo implements ItemSistemaArquivos {
    private final String nome;
    private final long tamanhoEmBytes;

    Arquivo(String nome, long tamanhoEmBytes) {
        this.nome = nome;
        this.tamanhoEmBytes = tamanhoEmBytes;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public long getTamanhoEmBytes() {
        return tamanhoEmBytes;
    }

    @Override
    public int contarArquivos() {
        return 1;
    }

    @Override
    public void exibir(String recuo) {
        System.out.println(recuo + "- " + nome + " (" + Tamanho.formatar(tamanhoEmBytes) + ")");
    }
}

// Composite: contém outros itens (arquivos ou outras pastas) e delega para eles
class Pasta implements ItemSistemaArquivos {
    private final String nome;
    private final List<ItemSistemaArquivos> filhos = new ArrayList<>();

    Pasta(String nome) {
        this.nome = nome;
    }

    Pasta adicionar(ItemSistemaArquivos item) {
        filhos.add(item);
        return this; // permite encadear chamadas
    }

    void remover(ItemSistemaArquivos item) {
        filhos.remove(item);
    }

    @Override
    public String getNome() {
        return nome;
    }

    // O tamanho de uma pasta é a soma dos filhos (recursivo)
    @Override
    public long getTamanhoEmBytes() {
        long total = 0;
        for (ItemSistemaArquivos filho : filhos) {
            total += filho.getTamanhoEmBytes();
        }
        return total;
    }

    @Override
    public int contarArquivos() {
        int total = 0;
        for (ItemSistemaArquivos filho : filhos) {
            total += filho.contarArquivos();
        }
        return total;
    }

    @Override
    public void exibir(String recuo) {
        System.out.println(recuo + "+ " + nome + "/ (" + Tamanho.formatar(getTamanhoEmBytes()) + ")");
        for (ItemSistemaArquivos filho : filhos) {
            filho.exibir(recuo + "    ");
        }
    }
}

// Utilitário para exibir tamanhos de forma legível
class Tamanho {
    static String formatar(long bytes) {
        if (bytes >= 1024L * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        }
        if (bytes >= 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        return bytes + " B";
    }
}

public class Composite {
    public static void main(String[] args) {
        Pasta model = new Pasta("model")
                .adicionar(new Arquivo("Usuario.java", 3_000))
                .adicionar(new Arquivo("Pedido.java", 5_200));

        Pasta src = new Pasta("src")
                .adicionar(new Arquivo("Main.java", 4_096))
                .adicionar(new Arquivo("Util.java", 2_048))
                .adicionar(model);

        Pasta docs = new Pasta("docs")
                .adicionar(new Arquivo("manual.pdf", 2_621_440));

        Pasta projeto = new Pasta("projeto")
                .adicionar(src)
                .adicionar(docs)
                .adicionar(new Arquivo("README.md", 1_024));

        // Exibe a árvore inteira
        projeto.exibir("");

        // Pasta, subpasta e arquivo são tratados exatamente da mesma forma
        System.out.println();
        ItemSistemaArquivos[] itens = {projeto, src, new Arquivo("README.md", 1_024)};
        for (ItemSistemaArquivos item : itens) {
            System.out.println(item.getNome() + " -> "
                    + Tamanho.formatar(item.getTamanhoEmBytes())
                    + ", " + item.contarArquivos() + " arquivo(s)");
        }
    }
}