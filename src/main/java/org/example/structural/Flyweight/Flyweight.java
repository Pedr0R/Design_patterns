package org.example.structural.Flyweight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Flyweight: economiza memória compartilhando as partes comuns (estado intrínseco)
// entre muitos objetos, em vez de duplicá-las em cada um.
//
//   Estado INTRÍNSECO: igual para muitos objetos, imutável, fica no flyweight (compartilhado).
//   Estado EXTRÍNSECO: específico de cada objeto, fica fora do flyweight (posição, idade...).

// Flyweight: guarda o estado intrínseco, que é pesado e se repete
class TipoArvore {
    private static final int TAMANHO_TEXTURA_BYTES = 500_000; // simula uma textura pesada (~500 KB)

    private final String nome;
    private final String cor;
    private final byte[] textura;

    TipoArvore(String nome, String cor) {
        this.nome = nome;
        this.cor = cor;
        this.textura = new byte[TAMANHO_TEXTURA_BYTES];
    }

    static int tamanhoTexturaEmBytes() {
        return TAMANHO_TEXTURA_BYTES;
    }

    // O estado extrínseco (posição e idade) chega como parâmetro
    void desenhar(int x, int y, int idade) {
        System.out.println("Desenhando " + nome + " (" + cor + ") em (" + x + ", " + y
                + "), " + idade + " anos");
    }
}

// Fábrica de flyweights: garante que cada tipo seja criado uma única vez
class FabricaTipoArvore {
    private static final Map<String, TipoArvore> cache = new HashMap<>();

    static TipoArvore obter(String nome, String cor) {
        String chave = nome + "|" + cor;
        return cache.computeIfAbsent(chave, k -> new TipoArvore(nome, cor));
    }

    static int totalDeTipos() {
        return cache.size();
    }
}

// Contexto: cada árvore guarda só o que é próprio dela + uma referência ao tipo compartilhado
class Arvore {
    private final int x;
    private final int y;
    private final int idade;
    private final TipoArvore tipo;

    Arvore(int x, int y, int idade, TipoArvore tipo) {
        this.x = x;
        this.y = y;
        this.idade = idade;
        this.tipo = tipo;
    }

    TipoArvore getTipo() {
        return tipo;
    }

    void desenhar() {
        tipo.desenhar(x, y, idade);
    }
}

// Cliente: a floresta usa a fábrica para obter os tipos compartilhados
class Floresta {
    private final List<Arvore> arvores = new ArrayList<>();

    void plantar(int x, int y, int idade, String nome, String cor) {
        TipoArvore tipo = FabricaTipoArvore.obter(nome, cor);
        arvores.add(new Arvore(x, y, idade, tipo));
    }

    Arvore get(int indice) {
        return arvores.get(indice);
    }

    int total() {
        return arvores.size();
    }
}

public class Flyweight {
    public static void main(String[] args) {
        String[][] especies = {
                {"Carvalho", "verde-escuro"},
                {"Pinheiro", "verde-claro"},
                {"Bétula", "branco-e-verde"}
        };

        Floresta floresta = new Floresta();
        Random random = new Random(42);

        // Planta 100 mil árvores, mas só existem 3 espécies
        for (int i = 0; i < 100_000; i++) {
            String[] especie = especies[random.nextInt(especies.length)];
            floresta.plantar(random.nextInt(5000), random.nextInt(5000),
                    random.nextInt(100), especie[0], especie[1]);
        }

        // Desenha apenas as 3 primeiras para não poluir a saída
        for (int i = 0; i < 3; i++) {
            floresta.get(i).desenhar();
        }

        // Evidência do compartilhamento: árvores da mesma espécie usam o MESMO objeto
        boolean mesmoObjeto = false;
        TipoArvore primeiro = floresta.get(0).getTipo();
        for (int i = 1; i < floresta.total(); i++) {
            if (floresta.get(i).getTipo() == primeiro) {
                mesmoObjeto = true;
                break;
            }
        }
        System.out.println("\nÁrvores da mesma espécie compartilham o mesmo TipoArvore? " + mesmoObjeto);

        // Estimativa (apenas das texturas, que dominam o consumo; não é uma medição)
        long semFlyweight = (long) floresta.total() * TipoArvore.tamanhoTexturaEmBytes();
        long comFlyweight = (long) FabricaTipoArvore.totalDeTipos() * TipoArvore.tamanhoTexturaEmBytes();

        System.out.println("Árvores: " + floresta.total());
        System.out.println("Tipos criados (flyweights): " + FabricaTipoArvore.totalDeTipos());
        System.out.printf("Texturas sem Flyweight: ~%.0f MB%n", semFlyweight / (1024.0 * 1024));
        System.out.printf("Texturas com Flyweight: ~%.1f MB%n", comFlyweight / (1024.0 * 1024));
    }
}