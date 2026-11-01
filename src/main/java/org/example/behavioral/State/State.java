package org.example.behavioral.State;

// State: permite que um objeto altere seu comportamento quando seu estado interno
// muda. Cada estado vira uma classe, e o objeto parece mudar de classe.

// Interface do estado: todas as ações que o pedido pode receber
interface EstadoPedido {
    String nome();

    void pagar(Pedido pedido);

    void enviar(Pedido pedido);

    void entregar(Pedido pedido);

    void cancelar(Pedido pedido);
}

// Base com o comportamento padrão: recusar a operação.
// Cada estado concreto sobrescreve apenas as transições que PERMITE.
abstract class EstadoBase implements EstadoPedido {
    @Override
    public void pagar(Pedido pedido) {
        recusar("pagar");
    }

    @Override
    public void enviar(Pedido pedido) {
        recusar("enviar");
    }

    @Override
    public void entregar(Pedido pedido) {
        recusar("entregar");
    }

    @Override
    public void cancelar(Pedido pedido) {
        recusar("cancelar");
    }

    protected void recusar(String operacao) {
        System.out.println("   Operação '" + operacao + "' não permitida no estado " + nome());
    }
}

// Estados concretos

class Novo extends EstadoBase {
    @Override
    public String nome() {
        return "NOVO";
    }

    @Override
    public void pagar(Pedido pedido) {
        System.out.println("   Pagamento confirmado.");
        pedido.mudarPara(new Pago());
    }

    @Override
    public void cancelar(Pedido pedido) {
        System.out.println("   Pedido cancelado antes do pagamento.");
        pedido.mudarPara(new Cancelado());
    }
}

class Pago extends EstadoBase {
    @Override
    public String nome() {
        return "PAGO";
    }

    @Override
    public void enviar(Pedido pedido) {
        System.out.println("   Pedido despachado para a transportadora.");
        pedido.mudarPara(new Enviado());
    }

    @Override
    public void cancelar(Pedido pedido) {
        System.out.println("   Pedido cancelado. Iniciando reembolso...");
        pedido.mudarPara(new Cancelado());
    }
}

class Enviado extends EstadoBase {
    @Override
    public String nome() {
        return "ENVIADO";
    }

    @Override
    public void entregar(Pedido pedido) {
        System.out.println("   Entrega confirmada pelo cliente.");
        pedido.mudarPara(new Entregue());
    }
    // pagar, enviar e cancelar ficam recusados pelo comportamento padrão
}

class Entregue extends EstadoBase {
    @Override
    public String nome() {
        return "ENTREGUE";
    }
    // Estado final: todas as operações são recusadas
}

class Cancelado extends EstadoBase {
    @Override
    public String nome() {
        return "CANCELADO";
    }
    // Estado final: todas as operações são recusadas
}

// Contexto: delega TODAS as ações ao estado atual, sem nenhum if/switch
class Pedido {
    private final int numero;
    private EstadoPedido estado = new Novo();

    Pedido(int numero) {
        this.numero = numero;
    }

    void mudarPara(EstadoPedido novoEstado) {
        System.out.println("   [Pedido #" + numero + "] " + estado.nome() + " -> " + novoEstado.nome());
        this.estado = novoEstado;
    }

    void pagar() {
        estado.pagar(this);
    }

    void enviar() {
        estado.enviar(this);
    }

    void entregar() {
        estado.entregar(this);
    }

    void cancelar() {
        estado.cancelar(this);
    }
}

public class State {
    public static void main(String[] args) {
        System.out.println("Pedido 1001: fluxo completo, com tentativas inválidas");
        Pedido pedido = new Pedido(1001);

        System.out.println("-> enviar (antes de pagar)");
        pedido.enviar();

        System.out.println("-> pagar");
        pedido.pagar();

        System.out.println("-> pagar de novo");
        pedido.pagar();

        System.out.println("-> enviar");
        pedido.enviar();

        System.out.println("-> cancelar (já em transporte)");
        pedido.cancelar();

        System.out.println("-> entregar");
        pedido.entregar();

        System.out.println("-> cancelar (já entregue)");
        pedido.cancelar();

        System.out.println("\nPedido 1002: cancelado depois de pago");
        Pedido outro = new Pedido(1002);

        System.out.println("-> pagar");
        outro.pagar();

        System.out.println("-> cancelar");
        outro.cancelar();

        System.out.println("-> enviar (já cancelado)");
        outro.enviar();
    }
}