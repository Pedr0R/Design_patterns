package org.example.creational.builder;

// Produto imutável
class Computador {
    // obrigatorios
    private final String cpu;
    private final int ramGb;

    // Opcionais
    private final int armazenamentoGb;
    private final String placaDeVideo;
    private final boolean wifi;

    private Computador(Builder b) {
        this.cpu = b.cpu;
        this.ramGb = b.ramGb;
        this.armazenamentoGb = b.armazenamentoGb;
        this.placaDeVideo = b.placaDeVideo;
        this.wifi = b.wifi;
    }

    @Override
    public String toString() {
        return "Computador{cpu=" + cpu
                + ", ram=" + ramGb + "GB"
                + ", armazenamento=" + armazenamentoGb + "GB"
                + ", gpu=" + (placaDeVideo == null ? "integrada" : placaDeVideo)
                + ", wifi=" + wifi + "}";
    }

    // Builder como classe interna estática
    static class Builder {
        // Obrigatórios (vêm no construtor do Builder)
        final String cpu;
        final int ramGb;

        // Opcionais com valores padrão
        int armazenamentoGb = 256;
        String placaDeVideo = null;
        boolean wifi = true;

        Builder(String cpu, int ramGb) {
            this.cpu = cpu;
            this.ramGb = ramGb;
        }

        // Cada método devolve "this" para permitir encadeamento (interface fluente)
        Builder armazenamento(int gb) {
            this.armazenamentoGb = gb;
            return this;
        }

        Builder placaDeVideo(String modelo) {
            this.placaDeVideo = modelo;
            return this;
        }

        Builder wifi(boolean ativo) {
            this.wifi = ativo;
            return this;
        }

        // Validação centralizada antes de criar o objeto
        Computador build() {
            if (cpu == null || cpu.isBlank()) {
                throw new IllegalStateException("CPU é obrigatória");
            }
            if (ramGb < 4) {
                throw new IllegalStateException("RAM mínima é 4GB");
            }
            return new Computador(this);
        }
    }
}

// Director (opcional, versão GoF): encapsula receitas prontas de construção
class MontadoraDeComputador {
    static Computador escritorio() {
        return new Computador.Builder("Ryzen 5", 8)
                .armazenamento(512)
                .build();
    }

    static Computador gamer() {
        return new Computador.Builder("Core i9", 32)
                .armazenamento(2000)
                .placaDeVideo("RTX 4080")
                .build();
    }
}


// Classe pública precisa corresponder ao nome do arquivo
public class Builder {
    public static void main(String[] args) {
        // Uso direto do Builder: legível, só configura o que importa
        Computador pc = new Computador.Builder("Core i5", 16)
                .armazenamento(1000)
                .wifi(false)
                .build();
        System.out.println(pc);

        // Usando as receitas do Director
        System.out.println(MontadoraDeComputador.escritorio());
        System.out.println(MontadoraDeComputador.gamer());

        // Validação em ação
        try {
            new Computador.Builder("Core i3", 2).build();
        } catch (IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}