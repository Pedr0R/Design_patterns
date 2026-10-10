package org.example.bridge;
// Bridge: separa uma abstração da sua implementação, para que as duas
// possam variar de forma independente (evita a explosão de subclasses).

// Implementação (Implementor): a dimensão "cor"
interface Cor {
    String aplicar();
}

class Vermelho implements Cor {
    @Override
    public String aplicar() {
        return "vermelho";
    }
}

class Azul implements Cor {
    @Override
    public String aplicar() {
        return "azul";
    }
}

class Rosa implements Cor {
    @Override
    public String aplicar() {
        return "rosa";
    }
}

// Abstração (Abstraction): a dimensão "forma".
// O campo "cor" é a PONTE entre as duas hierarquias.
abstract class Forma {
    protected final Cor cor;

    protected Forma(Cor cor) {
        this.cor = cor;
    }

    abstract void desenhar();
}

// Abstrações refinadas (RefinedAbstraction)
class Circulo extends Forma {
    Circulo(Cor cor) {
        super(cor);
    }

    @Override
    void desenhar() {
        System.out.println("Desenhando um círculo " + cor.aplicar());
    }
}

class Quadrado extends Forma {
    Quadrado(Cor cor) {
        super(cor);
    }

    @Override
    void desenhar() {
        System.out.println("Desenhando um quadrado " + cor.aplicar());
    }
}

class Triangulo extends Forma {
    Triangulo(Cor cor) {
        super(cor);
    }

    @Override
    void desenhar() {
        System.out.println("Desenhando um triangulo " + cor.aplicar());
    }
}

public class Bridge {
    public static void main(String[] args) {
        // Qualquer forma combina com qualquer cor, sem classes extras
        Forma[] formas = {
                new Circulo(new Vermelho()),
                new Circulo(new Azul()),
                new Quadrado(new Vermelho()),
                new Quadrado(new Azul()),
                new Triangulo(new Rosa())
        };

        for (Forma f : formas) {
            f.desenhar();
        }

        // Adicionar um Triangulo exige UMA classe nova.
        // Adicionar uma cor Verde exige UMA classe nova.
        // Sem Bridge, cada novo item geraria uma classe por combinação.
    }
}