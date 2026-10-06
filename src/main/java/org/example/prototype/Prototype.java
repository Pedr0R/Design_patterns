package org.example.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Prototype: cria novos objetos COPIANDO um objeto existente (o protótipo),
// em vez de instanciar do zero.

// Interface do protótipo
interface Prototipo<T> {
    T clonar();
}

// Protótipo concreto
class Personagem implements Prototipo<Personagem> {
    private String nome;
    private int vida;
    private final List<String> habilidades; // objeto mutável: exige cópia profunda

    Personagem(String nome, int vida, List<String> habilidades) {
        this.nome = nome;
        this.vida = vida;
        this.habilidades = new ArrayList<>(habilidades);
    }

    // Construtor de cópia: faz a cópia PROFUNDA da lista
    private Personagem(Personagem outro) {
        this.nome = outro.nome;
        this.vida = outro.vida;
        this.habilidades = new ArrayList<>(outro.habilidades);
    }

    @Override
    public Personagem clonar() {
        return new Personagem(this);
    }

    void setNome(String nome) {
        this.nome = nome;
    }

    void adicionarHabilidade(String habilidade) {
        this.habilidades.add(habilidade);
    }

    @Override
    public String toString() {
        return nome + " (vida=" + vida + ", habilidades=" + habilidades + ")";
    }
}

// Registro de protótipos: guarda modelos prontos e entrega clones deles
class RegistroDePersonagens {
    private final Map<String, Personagem> modelos = new HashMap<>();

    void registrar(String chave, Personagem modelo) {
        modelos.put(chave, modelo);
    }

    Personagem criar(String chave) {
        Personagem modelo = modelos.get(chave);
        if (modelo == null) {
            throw new IllegalArgumentException("Modelo não registrado: " + chave);
        }
        return modelo.clonar();
    }
}

public class Prototype {
    public static void main(String[] args) {
        // 1) Monta um protótipo caro/complexo UMA vez e registra
        Personagem guerreiroBase = new Personagem(
                "Guerreiro", 100, List.of("Golpe de espada", "Escudo"));

        RegistroDePersonagens registro = new RegistroDePersonagens();
        registro.registrar("guerreiro", guerreiroBase);

        // 2) Cria novos personagens clonando o modelo
        Personagem p1 = registro.criar("guerreiro");
        p1.setNome("Thor");
        p1.adicionarHabilidade("Grito de guerra");

        Personagem p2 = registro.criar("guerreiro");
        p2.setNome("Brienne");

        System.out.println("Modelo : " + guerreiroBase);
        System.out.println("Clone 1: " + p1);
        System.out.println("Clone 2: " + p2);

        // 3) Prova de que a cópia é profunda: alterar um clone não afeta o modelo
        System.out.println();
        System.out.println("O modelo continua intacto? "
                + !guerreiroBase.toString().contains("Grito de guerra"));
    }
}