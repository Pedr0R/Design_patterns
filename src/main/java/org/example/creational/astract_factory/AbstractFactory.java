package org.example.creational.astract_factory;

// Abstract Factory: Cria famílias de produtos compatíveis entre si

// Produtos abstratos
interface Botao {
    void desenhar();
}

interface Checkbox {
    void desenhar();
}

// Família Windows
class BotaoWindows implements Botao {
    public void desenhar() {
        System.out.println("Desenhando botão estilo Windows");
    }
}

class CheckboxWindows implements Checkbox {
    public void desenhar() {
        System.out.println("Desenhando checkbox estilo Windows");
    }
}

// Família Mac
class BotaoMac implements Botao {
    public void desenhar() {
        System.out.println("Desenhando botão estilo Mac");
    }
}

class CheckboxMac implements Checkbox {
    public void desenhar() {
        System.out.println("Desenhando checkbox estilo Mac");
    }
}

// Família Linux
class BotaoLinux implements Botao {
    public void desenhar() {
        System.out.println("Desenhando botão estilo Linux");
    }
}

class CheckboxLinux implements Checkbox {
    public void desenhar() {
        System.out.println("Desenhando checkbox estilo Linux");
    }
}


//Abstract Factory: interface com os metodos de criação para cada produto da familia
interface GUIFactory {
    Botao criarBotao();
    Checkbox criarCheckbox();
}

// Fabricas concretas: Cada uma produz a familia completa
class WindowsFactory implements GUIFactory {
    public Botao criarBotao() {
        return new BotaoWindows();
    }

    public Checkbox criarCheckbox() {
        return new CheckboxWindows();
    }
}

class MacFactory implements GUIFactory {
    public Botao criarBotao() {
        return new BotaoMac();
    }

    public Checkbox criarCheckbox() {
        return new CheckboxMac();
    }
}

class LinuxFactory implements GUIFactory {
    public Botao criarBotao() {
        return new BotaoLinux();
    }

    public Checkbox criarCheckbox() {
        return new CheckboxLinux();
    }
}

// Cliente: recebe a fábrica (composição) e não conhece as classes concretas
class Aplicacao {
    private final Botao botao;
    private final Checkbox checkbox;

    Aplicacao(GUIFactory factory) {
        this.botao = factory.criarBotao();
        this.checkbox = factory.criarCheckbox();
    }

    void renderizar() {
        botao.desenhar();
        checkbox.desenhar();
    }
}

public class AbstractFactory {
    static void main(String[] args) {
        String so = System.getProperty("os.name").toLowerCase();

        GUIFactory factory = so.contains("mac") ? new MacFactory() : new WindowsFactory();

        new Aplicacao(factory).renderizar();

        System.out.println("--- Trocando a família inteira ---");
        new Aplicacao(new MacFactory()).renderizar();

        System.out.println("--- Trocando a família inteira ---");
        new Aplicacao(new LinuxFactory()).renderizar();
    }
}
