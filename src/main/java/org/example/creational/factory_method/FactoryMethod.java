package org.example.factory_method;

// Produto (interface)
interface Mensagem {
    void enviar(String destino, String texto);
}

// Produtos concretos
class Email implements Mensagem {
    public void enviar(String destino, String texto) {
        System.out.println("email " + texto + " " + destino);
    }
}

class SMS implements Mensagem {
    public void enviar(String destino, String texto) {
        System.out.println("SMS " + texto + " " + destino);
    }
}

// Criador -> Classe abstrata com o factory method
abstract class Notificador {
    // Factory Method
    protected abstract Mensagem criarMensagem();

    // Lógica comum que usa o produto sem saber a sua classe concreta
    public void notificar(String destino, String texto) {
        Mensagem mensagem = criarMensagem();
        mensagem.enviar(destino, texto);
    }
}

// Criadores concretos
class NotificadorEmail extends Notificador {
    @Override
    protected Mensagem criarMensagem() {
        return new Email();
    }
}

class NotificadorSMS extends Notificador {
    @Override
    protected Mensagem criarMensagem() {
        return new SMS();
    }
}

// Utilização
public class FactoryMethod {
    public static void  main( String[] args ) {
        // Instancia os creators específicos diretamente, sem saber o conteúdo da classe herdada.
        Notificador porEmail = new NotificadorEmail();
        Notificador porSMS = new NotificadorSMS();

        porEmail.notificar("lá", "Mensagem email");
        porSMS.notificar("lô", "mensagem SMS");
    }
}