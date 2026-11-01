package org.example.behavioral.Memento;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// Memento: captura e guarda o estado interno de um objeto, sem violar o
// encapsulamento, para que ele possa ser restaurado depois.

// Memento visto de FORA: uma caixa opaca. Quem guarda o checkpoint
// só consegue ler a descrição, nunca o estado interno.
interface Checkpoint {
    String descricao();
}

// Originator: o objeto cujo estado será salvo e restaurado
class Personagem {
    private final String nome;
    private int vida = 100;
    private int fase = 1;
    private int x = 0;
    private int y = 0;
    private final List<String> inventario = new ArrayList<>();

    Personagem(String nome) {
        this.nome = nome;
    }

    void sofrerDano(int dano) {
        vida = Math.max(0, vida - dano);
    }

    void mover(int dx, int dy) {
        x += dx;
        y += dy;
    }

    void coletar(String item) {
        inventario.add(item);
    }

    // Cria o memento com um retrato do estado atual
    Checkpoint salvar(String descricao) {
        return new Snapshot(descricao, vida, fase, x, y, inventario);
    }

    // Restaura o estado a partir de um memento
    void restaurar(Checkpoint checkpoint) {
        if (!(checkpoint instanceof Snapshot)) {
            throw new IllegalArgumentException("Checkpoint inválido");
        }
        Snapshot s = (Snapshot) checkpoint;
        this.vida = s.vida;
        this.fase = s.fase;
        this.x = s.x;
        this.y = s.y;
        this.inventario.clear();
        this.inventario.addAll(s.inventario);
    }

    @Override
    public String toString() {
        return nome + " | vida=" + vida + ", fase=" + fase
                + ", pos=(" + x + "," + y + "), inventário=" + inventario;
    }

    // Memento de verdade: classe aninhada PRIVADA. Só o Personagem enxerga
    // os campos; o resto do sistema só conhece a interface Checkpoint.
    private static class Snapshot implements Checkpoint {
        private final String descricao;
        private final int vida;
        private final int fase;
        private final int x;
        private final int y;
        private final List<String> inventario;

        Snapshot(String descricao, int vida, int fase, int x, int y, List<String> inventario) {
            this.descricao = descricao;
            this.vida = vida;
            this.fase = fase;
            this.x = x;
            this.y = y;
            this.inventario = List.copyOf(inventario); // cópia PROFUNDA: o snapshot não muda depois
        }

        @Override
        public String descricao() {
            return descricao;
        }
    }
}

// Caretaker: guarda os mementos, mas não sabe nada sobre o conteúdo deles
class HistoricoDeCheckpoints {
    private final Deque<Checkpoint> checkpoints = new ArrayDeque<>();

    void guardar(Checkpoint checkpoint) {
        checkpoints.push(checkpoint);
    }

    Checkpoint ultimo() {
        return checkpoints.peek(); // null se estiver vazio
    }

    void listar() {
        for (Checkpoint c : checkpoints) {
            System.out.println("   - " + c.descricao());
        }
    }
}

public class Memento {
    public static void main(String[] args) {
        Personagem heroi = new Personagem("Aria");
        HistoricoDeCheckpoints historico = new HistoricoDeCheckpoints();

        heroi.coletar("Espada");
        heroi.mover(5, 3);
        System.out.println("1) Antes do chefe:");
        System.out.println("   " + heroi);

        // Salva o checkpoint antes de uma luta arriscada
        historico.guardar(heroi.salvar("Antes do chefe da fase 1"));

        heroi.sofrerDano(70);
        heroi.coletar("Poção vazia");
        heroi.mover(10, 0);
        System.out.println("\n2) Durante a luta:");
        System.out.println("   " + heroi);

        heroi.sofrerDano(50);
        System.out.println("\n3) Derrota:");
        System.out.println("   " + heroi);

        // Volta ao último checkpoint: vida, posição e inventário voltam ao que eram
        heroi.restaurar(historico.ultimo());
        System.out.println("\n4) Restaurado do checkpoint:");
        System.out.println("   " + heroi);

        System.out.println("\nCheckpoints guardados:");
        historico.listar();

        // O HistoricoDeCheckpoints só enxerga a interface Checkpoint (descricao()).
        // Ele não consegue ler nem alterar vida, posição ou inventário salvos.
    }
}