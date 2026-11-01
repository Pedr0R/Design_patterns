package org.example.behavioral.Mediator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// org.example.behavioral.Mediator.Mediator: centraliza a comunicação entre vários objetos. Em vez de se
// falarem diretamente, eles falam apenas com o mediador, que coordena tudo.

// Interface do mediador
interface Mediador {
    void registrar(Usuario usuario);

    void remover(Usuario usuario);

    void enviar(Usuario remetente, String texto);

    void enviarPrivado(Usuario remetente, Usuario destinatario, String texto);
}

// Colega (Colleague): conhece só o mediador, nunca os outros usuários
class Usuario {
    private final String nome;
    private Mediador mediador;

    Usuario(String nome) {
        this.nome = nome;
    }

    String getNome() {
        return nome;
    }

    void setMediador(Mediador mediador) {
        this.mediador = mediador;
    }

    void enviar(String texto) {
        mediador.enviar(this, texto);
    }

    void enviarPara(Usuario destinatario, String texto) {
        mediador.enviarPrivado(this, destinatario, texto);
    }

    void receber(String de, String texto) {
        System.out.println("   [" + nome + " recebeu] " + de + ": " + texto);
    }
}

// org.example.behavioral.Mediator.Mediador concreto: concentra as regras de quem fala com quem
class SalaDeChat implements Mediador {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final Set<Usuario> silenciados = new HashSet<>();

    @Override
    public void registrar(Usuario usuario) {
        usuarios.add(usuario);
        usuario.setMediador(this);
        avisarOutros(usuario, usuario.getNome() + " entrou na sala");
    }

    @Override
    public void remover(Usuario usuario) {
        usuarios.remove(usuario);
        usuario.setMediador(null);
        avisarOutros(usuario, usuario.getNome() + " saiu da sala");
    }

    // Regra exclusiva da sala: administradores podem silenciar alguém
    void silenciar(Usuario usuario) {
        silenciados.add(usuario);
    }

    @Override
    public void enviar(Usuario remetente, String texto) {
        if (silenciados.contains(remetente)) {
            remetente.receber("Sistema", "Você está silenciado e não pode enviar mensagens.");
            return;
        }
        for (Usuario u : usuarios) {
            if (u != remetente) {
                u.receber(remetente.getNome(), texto);
            }
        }
    }

    @Override
    public void enviarPrivado(Usuario remetente, Usuario destinatario, String texto) {
        if (!usuarios.contains(destinatario)) {
            remetente.receber("Sistema", destinatario.getNome() + " não está na sala.");
            return;
        }
        destinatario.receber(remetente.getNome() + " (privado)", texto);
    }

    private void avisarOutros(Usuario origem, String aviso) {
        for (Usuario u : usuarios) {
            if (u != origem) {
                u.receber("Sistema", aviso);
            }
        }
    }
}

public class Mediator {
    public static void main(String[] args) {
        SalaDeChat sala = new SalaDeChat();

        Usuario ana = new Usuario("Ana");
        Usuario bruno = new Usuario("Bruno");
        Usuario carla = new Usuario("Carla");

        System.out.println("1) Entrada na sala:");
        sala.registrar(ana);
        sala.registrar(bruno);
        sala.registrar(carla);

        System.out.println("\n2) Mensagem para todos:");
        ana.enviar("Bom dia, pessoal!");

        System.out.println("\n3) Mensagem privada:");
        bruno.enviarPara(carla, "Você viu o relatório?");

        System.out.println("\n4) Usuária silenciada tenta falar:");
        sala.silenciar(carla);
        carla.enviar("Alguém me ouve?");

        System.out.println("\n5) Mensagem para quem já saiu:");
        sala.remover(bruno);
        ana.enviarPara(bruno, "Oi, Bruno!");

        // Os usuários nunca guardam referência uns aos outros. Para mudar uma regra
        // (limite de mensagens, palavras proibidas, histórico), só a org.example.behavioral.Mediator.SalaDeChat muda.
    }
}