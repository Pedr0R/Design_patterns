package org.example.behavioral.Observer;

import java.util.ArrayList;
import java.util.List;

// Observer: define uma dependência um-para-muitos. Quando o estado de um objeto
// (o sujeito) muda, todos os seus dependentes (observadores) são notificados
// automaticamente, sem que o sujeito precise conhecê-los em detalhe.

// Interface do observador. Com um único método, também pode ser uma lambda.
@FunctionalInterface
interface ObservadorDePreco {
    void precoAlterado(String produto, double precoAntigo, double precoNovo);
}

// Sujeito (Subject): mantém a lista de observadores e os notifica
class Produto {
    private final String nome;
    private double preco;
    private final List<ObservadorDePreco> observadores = new ArrayList<>();

    Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    void inscrever(ObservadorDePreco observador) {
        observadores.add(observador);
    }

    void cancelar(ObservadorDePreco observador) {
        observadores.remove(observador);
    }

    void setPreco(double novoPreco) {
        double precoAntigo = this.preco;
        this.preco = novoPreco;
        notificar(precoAntigo, novoPreco);
    }

    private void notificar(double precoAntigo, double precoNovo) {
        // Itera sobre uma CÓPIA: um observador pode se cancelar durante a notificação
        for (ObservadorDePreco o : new ArrayList<>(observadores)) {
            o.precoAlterado(nome, precoAntigo, precoNovo);
        }
    }
}

// Observador concreto 1: painel interno que mostra toda variação
class PainelAdmin implements ObservadorDePreco {
    @Override
    public void precoAlterado(String produto, double precoAntigo, double precoNovo) {
        double variacao = (precoNovo - precoAntigo) / precoAntigo * 100;
        System.out.printf("   [PAINEL] %s: R$ %.2f -> R$ %.2f (%+.1f%%)%n",
                produto, precoAntigo, precoNovo, variacao);
    }
}

// Observador concreto 2: alerta de um cliente, disparado uma única vez
class AlertaDePreco implements ObservadorDePreco {
    private final String cliente;
    private final double precoAlvo;
    private final Produto produto;

    AlertaDePreco(String cliente, double precoAlvo, Produto produto) {
        this.cliente = cliente;
        this.precoAlvo = precoAlvo;
        this.produto = produto;
    }

    @Override
    public void precoAlterado(String nomeProduto, double precoAntigo, double precoNovo) {
        if (precoNovo <= precoAlvo) {
            System.out.printf("   [ALERTA] %s, o produto %s chegou a R$ %.2f (seu alvo: R$ %.2f)%n",
                    cliente, nomeProduto, precoNovo, precoAlvo);
            produto.cancelar(this); // alerta único: o observador sai sozinho
        }
    }
}

public class Observer {
    public static void main(String[] args) {
        Produto notebook = new Produto("Notebook Pro", 5000.00);

        PainelAdmin painel = new PainelAdmin();
        notebook.inscrever(painel);
        notebook.inscrever(new AlertaDePreco("Ana", 4500.00, notebook));
        notebook.inscrever(new AlertaDePreco("Bruno", 4000.00, notebook));
        // Observador como lambda
        notebook.inscrever((produto, antigo, novo) ->
                System.out.println("   [AUDITORIA] preço de " + produto + " alterado"));

        System.out.println("1) Preço cai para 4800 (nenhum alvo atingido):");
        notebook.setPreco(4800.00);

        System.out.println("\n2) Preço cai para 4400 (alvo da Ana atingido):");
        notebook.setPreco(4400.00);

        System.out.println("\n3) Preço cai para 3900 (alvo do Bruno atingido; Ana já saiu):");
        notebook.setPreco(3900.00);

        System.out.println("\n4) Painel cancela a inscrição e o preço cai para 3800:");
        notebook.cancelar(painel);
        notebook.setPreco(3800.00);

        // O Produto não conhece PainelAdmin nem AlertaDePreco, só a interface
        // ObservadorDePreco. Novos tipos de interessado (e-mail, push, integração com
        // marketplace) entram sem alterar a classe Produto.
    }
}