package org.example.behavioral.Visitor;
import java.util.List;

// Visitor: permite adicionar NOVAS OPERAÇÕES a uma hierarquia de classes sem
// alterar essas classes. A operação vira um objeto (o visitante) que "visita"
// cada elemento, e a chamada certa é escolhida por despacho duplo.

// Elemento: cada item sabe apenas "aceitar" um visitante
interface Item {
    void aceitar(VisitanteDeItem visitante);
}

// Interface do visitante: um método visitar() para CADA tipo de elemento
interface VisitanteDeItem {
    void visitar(Livro livro);

    void visitar(Eletronico eletronico);

    void visitar(Alimento alimento);
}

// Elementos concretos: só dados e o método aceitar(), nenhuma regra de negócio das operações
class Livro implements Item {
    final String titulo;
    final double preco;

    Livro(String titulo, double preco) {
        this.titulo = titulo;
        this.preco = preco;
    }

    @Override
    public void aceitar(VisitanteDeItem visitante) {
        visitante.visitar(this); // despacho duplo: o item informa o PRÓPRIO tipo
    }
}

class Eletronico implements Item {
    final String nome;
    final double preco;
    final int garantiaMeses;

    Eletronico(String nome, double preco, int garantiaMeses) {
        this.nome = nome;
        this.preco = preco;
        this.garantiaMeses = garantiaMeses;
    }

    @Override
    public void aceitar(VisitanteDeItem visitante) {
        visitante.visitar(this);
    }
}

class Alimento implements Item {
    final String nome;
    final double preco;
    final int diasDeValidade;

    Alimento(String nome, double preco, int diasDeValidade) {
        this.nome = nome;
        this.preco = preco;
        this.diasDeValidade = diasDeValidade;
    }

    @Override
    public void aceitar(VisitanteDeItem visitante) {
        visitante.visitar(this);
    }
}

// Visitante 1: calcula o imposto, com uma alíquota diferente para cada tipo
class CalculadoraDeImposto implements VisitanteDeItem {
    private double total = 0;

    @Override
    public void visitar(Livro livro) {
        registrar(livro.titulo, livro.preco, 0.00); // livros: imunes
    }

    @Override
    public void visitar(Eletronico eletronico) {
        registrar(eletronico.nome, eletronico.preco, 0.18);
    }

    @Override
    public void visitar(Alimento alimento) {
        registrar(alimento.nome, alimento.preco, 0.07);
    }

    private void registrar(String nome, double preco, double aliquota) {
        double imposto = preco * aliquota;
        total += imposto;
        System.out.printf("   %-20s R$ %7.2f x %4.1f%% = R$ %6.2f%n",
                nome, preco, aliquota * 100, imposto);
    }

    double getTotal() {
        return total;
    }
}

// Visitante 2: gera uma linha de resumo, mostrando o dado específico de cada tipo
class GeradorDeResumo implements VisitanteDeItem {
    @Override
    public void visitar(Livro livro) {
        System.out.printf("   Livro      : %s - R$ %.2f%n", livro.titulo, livro.preco);
    }

    @Override
    public void visitar(Eletronico eletronico) {
        System.out.printf("   Eletrônico : %s - R$ %.2f (garantia de %d meses)%n",
                eletronico.nome, eletronico.preco, eletronico.garantiaMeses);
    }

    @Override
    public void visitar(Alimento alimento) {
        System.out.printf("   Alimento   : %s - R$ %.2f (validade: %d dias)%n",
                alimento.nome, alimento.preco, alimento.diasDeValidade);
    }
}

// Visitante 3: conta itens por tipo. Foi acrescentado sem tocar em Livro, Eletronico ou Alimento.
class ContadorPorTipo implements VisitanteDeItem {
    private int livros;
    private int eletronicos;
    private int alimentos;

    @Override
    public void visitar(Livro livro) {
        livros++;
    }

    @Override
    public void visitar(Eletronico eletronico) {
        eletronicos++;
    }

    @Override
    public void visitar(Alimento alimento) {
        alimentos++;
    }

    @Override
    public String toString() {
        return livros + " livro(s), " + eletronicos + " eletrônico(s), " + alimentos + " alimento(s)";
    }
}

public class Visitor {
    public static void main(String[] args) {
        List<Item> carrinho = List.of(
                new Livro("Padrões de Projeto", 89.90),
                new Eletronico("Fone Bluetooth", 250.00, 12),
                new Alimento("Café especial", 42.00, 180),
                new Livro("Clean Code", 110.00),
                new Eletronico("Carregador", 80.00, 6));

        System.out.println("1) Resumo do carrinho:");
        GeradorDeResumo resumo = new GeradorDeResumo();
        for (Item item : carrinho) {
            item.aceitar(resumo);
        }

        System.out.println("\n2) Impostos:");
        CalculadoraDeImposto imposto = new CalculadoraDeImposto();
        for (Item item : carrinho) {
            item.aceitar(imposto);
        }
        System.out.printf("   Total de impostos: R$ %.2f%n", imposto.getTotal());

        System.out.println("\n3) Contagem por tipo:");
        ContadorPorTipo contador = new ContadorPorTipo();
        for (Item item : carrinho) {
            item.aceitar(contador);
        }
        System.out.println("   " + contador);

        // Uma operação nova (desconto, exportação para JSON, cálculo de frete...)
        // é um visitante novo. As classes de item não mudam.
    }
}