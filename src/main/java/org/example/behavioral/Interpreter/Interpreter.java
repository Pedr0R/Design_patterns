package org.example.behavioral.Interpreter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Interpreter: dada uma linguagem (uma gramática), representa cada regra dela como
// uma classe e monta uma árvore de objetos que sabe se "interpretar".
//
// Gramática da mini-linguagem de regras deste exemplo:
//
//   regra      := e ( "OR" e )*
//   e          := nao ( "AND" nao )*
//   nao        := "NOT" nao | primario
//   primario   := "(" regra ")" | comparacao
//   comparacao := VARIAVEL OPERADOR VALOR       (> >= < <= == !=)
//   VALOR      := número | 'texto' | true | false
//
// Exemplo: idade >= 18 AND (pais == 'BR' OR vip == true)

// Expressão abstrata: toda parte da gramática sabe se interpretar dado um contexto
interface Expressao {
    boolean interpretar(Map<String, Object> contexto);
}

// Expressão terminal: a menor peça da gramática, não contém outras expressões
class Comparacao implements Expressao {
    private final String variavel;
    private final String operador;
    private final Object valor;

    Comparacao(String variavel, String operador, Object valor) {
        this.variavel = variavel;
        this.operador = operador;
        this.valor = valor;
    }

    @Override
    public boolean interpretar(Map<String, Object> contexto) {
        Object atual = contexto.get(variavel);
        if (atual == null) {
            throw new IllegalStateException("Variável não definida: " + variavel);
        }

        // Comparação numérica
        if (valor instanceof Double && atual instanceof Number) {
            double a = ((Number) atual).doubleValue();
            double b = (Double) valor;
            switch (operador) {
                case ">":  return a > b;
                case ">=": return a >= b;
                case "<":  return a < b;
                case "<=": return a <= b;
                case "==": return a == b;
                case "!=": return a != b;
                default:   throw new IllegalArgumentException("Operador desconhecido: " + operador);
            }
        }

        // Texto e booleano: só igualdade e diferença fazem sentido
        boolean igual = atual.equals(valor);
        switch (operador) {
            case "==": return igual;
            case "!=": return !igual;
            default:
                throw new IllegalArgumentException(
                        "Operador '" + operador + "' não se aplica à variável " + variavel);
        }
    }

    @Override
    public String toString() {
        return variavel + " " + operador + " " + valor;
    }
}

// Expressões não terminais: combinam outras expressões (formam a árvore)
class E implements Expressao {
    private final Expressao esquerda;
    private final Expressao direita;

    E(Expressao esquerda, Expressao direita) {
        this.esquerda = esquerda;
        this.direita = direita;
    }

    @Override
    public boolean interpretar(Map<String, Object> contexto) {
        return esquerda.interpretar(contexto) && direita.interpretar(contexto);
    }

    @Override
    public String toString() {
        return "(" + esquerda + " AND " + direita + ")";
    }
}

class Ou implements Expressao {
    private final Expressao esquerda;
    private final Expressao direita;

    Ou(Expressao esquerda, Expressao direita) {
        this.esquerda = esquerda;
        this.direita = direita;
    }

    @Override
    public boolean interpretar(Map<String, Object> contexto) {
        return esquerda.interpretar(contexto) || direita.interpretar(contexto);
    }

    @Override
    public String toString() {
        return "(" + esquerda + " OR " + direita + ")";
    }
}

class Nao implements Expressao {
    private final Expressao interna;

    Nao(Expressao interna) {
        this.interna = interna;
    }

    @Override
    public boolean interpretar(Map<String, Object> contexto) {
        return !interna.interpretar(contexto);
    }

    @Override
    public String toString() {
        return "NOT " + interna;
    }
}

// Parser: transforma o TEXTO da regra na árvore de expressões.
// Não faz parte do padrão em si, mas é ele que permite escrever regras como texto.
// Cada método abaixo corresponde a uma linha da gramática (descida recursiva).
class Parser {
    private static final Pattern TOKEN =
            Pattern.compile(">=|<=|==|!=|>|<|\\(|\\)|'[^']*'|[^\\s()<>=!']+");

    private final List<String> tokens = new ArrayList<>();
    private int posicao = 0;

    Parser(String texto) {
        Matcher m = TOKEN.matcher(texto);
        while (m.find()) {
            tokens.add(m.group());
        }
    }

    Expressao analisar() {
        Expressao resultado = analisarOu();
        if (posicao < tokens.size()) {
            throw new IllegalArgumentException("Token inesperado: " + tokens.get(posicao));
        }
        return resultado;
    }

    private Expressao analisarOu() {
        Expressao esquerda = analisarE();
        while (consumir("OR")) {
            esquerda = new Ou(esquerda, analisarE());
        }
        return esquerda;
    }

    private Expressao analisarE() {
        Expressao esquerda = analisarNao();
        while (consumir("AND")) {
            esquerda = new E(esquerda, analisarNao());
        }
        return esquerda;
    }

    private Expressao analisarNao() {
        if (consumir("NOT")) {
            return new Nao(analisarNao());
        }
        return analisarPrimario();
    }

    private Expressao analisarPrimario() {
        if (consumir("(")) {
            Expressao interna = analisarOu();
            if (!consumir(")")) {
                throw new IllegalArgumentException("Faltou fechar o parêntese");
            }
            return interna;
        }
        String variavel = proximo();
        String operador = proximo();
        String valor = proximo();
        return new Comparacao(variavel, operador, converter(valor));
    }

    private boolean consumir(String esperado) {
        if (posicao < tokens.size() && tokens.get(posicao).equalsIgnoreCase(esperado)) {
            posicao++;
            return true;
        }
        return false;
    }

    private String proximo() {
        if (posicao >= tokens.size()) {
            throw new IllegalArgumentException("A regra terminou antes do esperado");
        }
        return tokens.get(posicao++);
    }

    private Object converter(String valor) {
        if (valor.startsWith("'") && valor.endsWith("'") && valor.length() >= 2) {
            return valor.substring(1, valor.length() - 1);
        }
        if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(valor);
        }
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido: " + valor);
        }
    }
}

public class Interpreter {
    public static void main(String[] args) {
        // Contexto: os dados de cada cliente que as regras vão consultar
        Map<String, Map<String, Object>> clientes = new LinkedHashMap<>();
        clientes.put("Ana (30, BR, 12 compras)", cliente(30, "BR", 12, false));
        clientes.put("Bruno (17, BR, 2 compras)", cliente(17, "BR", 2, false));
        clientes.put("Carla (40, PT, 5 compras, vip)", cliente(40, "PT", 5, true));

        // As regras são TEXTO: poderiam vir de um banco de dados ou de uma tela de administração
        String[] regras = {
                "idade >= 18 AND (pais == 'BR' OR vip == true)",
                "compras > 10 OR vip == true",
                "NOT (pais == 'BR') AND idade >= 18"
        };

        for (String texto : regras) {
            Expressao regra = new Parser(texto).analisar(); // interpreta o texto UMA vez
            System.out.println("Regra: " + texto);
            System.out.println("Árvore: " + regra);
            for (Map.Entry<String, Map<String, Object>> c : clientes.entrySet()) {
                // e reaproveita a árvore para cada cliente
                System.out.printf("   %-32s -> %s%n", c.getKey(), regra.interpretar(c.getValue()));
            }
            System.out.println();
        }

        // Erros: a regra mal escrita é rejeitada na hora de analisar
        testarRegraInvalida("idade >=");
        testarRegraInvalida("(idade > 18");
        testarRegraInvalida("idade > dezoito");

        // Variável que não existe no contexto: erro na hora de interpretar
        try {
            new Parser("salario > 1000").analisar().interpretar(cliente(30, "BR", 1, false));
        } catch (IllegalStateException e) {
            System.out.println("Erro ao interpretar: " + e.getMessage());
        }
    }

    private static Map<String, Object> cliente(int idade, String pais, int compras, boolean vip) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("idade", idade);
        dados.put("pais", pais);
        dados.put("compras", compras);
        dados.put("vip", vip);
        return dados;
    }

    private static void testarRegraInvalida(String texto) {
        try {
            new Parser(texto).analisar();
        } catch (IllegalArgumentException e) {
            System.out.println("Regra \"" + texto + "\" rejeitada: " + e.getMessage());
        }
    }
}