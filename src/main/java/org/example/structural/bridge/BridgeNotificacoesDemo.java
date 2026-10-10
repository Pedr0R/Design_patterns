package org.example.structural.bridge;// Exemplo realista de Bridge: sistema de notificações.
// Duas dimensões independentes:
//   - O QUE notificar (alerta de segurança, confirmação de pedido, lembrete)
//   - POR ONDE enviar (e-mail, SMS, push)

// ===== Implementação (Implementor): canais de envio =====
interface CanalEnvio {
    void enviar(String destinatario, String titulo, String corpo);
}

class EmailCanal implements CanalEnvio {
    @Override
    public void enviar(String destinatario, String titulo, String corpo) {
        System.out.println("[EMAIL] Para: " + destinatario);
        System.out.println("        Assunto: " + titulo);
        System.out.println("        Mensagem: " + corpo);
    }
}

class SmsCanal implements CanalEnvio {
    private static final int LIMITE = 160;

    @Override
    public void enviar(String destinatario, String titulo, String corpo) {
        // SMS não tem assunto e tem limite de caracteres: o canal se adapta
        String texto = titulo + ": " + corpo;
        if (texto.length() > LIMITE) {
            texto = texto.substring(0, LIMITE - 3) + "...";
        }
        System.out.println("[SMS] Para: " + destinatario + " | " + texto);
    }
}

class PushCanal implements CanalEnvio {
    @Override
    public void enviar(String destinatario, String titulo, String corpo) {
        System.out.println("[PUSH] Dispositivo: " + destinatario
                + " | " + titulo + " - " + corpo);
    }
}

// ===== Abstração (Abstraction): tipos de notificação =====
abstract class Notificacao {
    protected CanalEnvio canal; // a ponte

    protected Notificacao(CanalEnvio canal) {
        this.canal = canal;
    }

    // Permite trocar o canal em tempo de execução
    void setCanal(CanalEnvio canal) {
        this.canal = canal;
    }

    abstract void enviar(String destinatario);
}

// ===== Abstrações refinadas: cada uma define o CONTEÚDO =====
class AlertaSeguranca extends Notificacao {
    private final String localAcesso;

    AlertaSeguranca(CanalEnvio canal, String localAcesso) {
        super(canal);
        this.localAcesso = localAcesso;
    }

    @Override
    void enviar(String destinatario) {
        canal.enviar(destinatario,
                "Alerta de segurança",
                "Detectamos um novo acesso à sua conta em " + localAcesso
                        + ". Se não foi você, altere sua senha imediatamente.");
    }
}

class ConfirmacaoPedido extends Notificacao {
    private final int numeroPedido;
    private final double valor;

    ConfirmacaoPedido(CanalEnvio canal, int numeroPedido, double valor) {
        super(canal);
        this.numeroPedido = numeroPedido;
        this.valor = valor;
    }

    @Override
    void enviar(String destinatario) {
        canal.enviar(destinatario,
                "Pedido #" + numeroPedido + " confirmado",
                String.format("Recebemos seu pedido no valor de R$ %.2f. "
                        + "Você será avisado quando ele for enviado.", valor));
    }
}

class LembreteVencimento extends Notificacao {
    private final String descricao;
    private final String dataVencimento;

    LembreteVencimento(CanalEnvio canal, String descricao, String dataVencimento) {
        super(canal);
        this.descricao = descricao;
        this.dataVencimento = dataVencimento;
    }

    @Override
    void enviar(String destinatario) {
        canal.enviar(destinatario,
                "Lembrete de vencimento",
                descricao + " vence em " + dataVencimento + ".");
    }
}

public class BridgeNotificacoesDemo {
    public static void main(String[] args) {
        CanalEnvio email = new EmailCanal();
        CanalEnvio sms = new SmsCanal();
        CanalEnvio push = new PushCanal();

        // Qualquer tipo de notificação combina com qualquer canal
        new AlertaSeguranca(email, "Rio de Janeiro, BR").enviar("ana@exemplo.com");
        System.out.println();

        new ConfirmacaoPedido(sms, 4821, 249.90).enviar("+55 21 99999-0000");
        System.out.println();

        new LembreteVencimento(push, "Sua fatura do cartão", "15/11").enviar("device-7f3a");
        System.out.println();

        // Troca de canal em tempo de execução (ex.: preferência do usuário mudou)
        Notificacao pedido = new ConfirmacaoPedido(email, 4822, 89.00);
        pedido.enviar("ana@exemplo.com");
        pedido.setCanal(push);
        pedido.enviar("device-7f3a");

        // Adicionar um canal novo (ex.: Slack, WhatsApp) = 1 classe.
        // Adicionar um tipo novo de notificação = 1 classe.
        // Sem Bridge: 3 tipos x 3 canais = 9 classes, e cada novidade multiplicaria.
    }
}