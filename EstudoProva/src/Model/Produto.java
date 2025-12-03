package Model;

import java.io.File;
import java.io.Serializable; // Importa a "permissão" para salvar objetos

// [TEORIA] "implements Serializable":
// É uma Interface Marcadora (sem métodos). Diz à JVM (Java Virtual Machine):
// "Pode pegar os dados dessa classe e transformar em bytes para salvar no HD".
// Se esquecer isso, dá o erro 'NotSerializableException'.
public class Produto implements Serializable {

    // [TEORIA] serialVersionUID:
    // É o RG da classe. Se você salvar um objeto hoje (versão 1) e amanhã mudar o código
    // (adicionar um atributo), o Java compara esse número.
    // Se não tiver, o Java bloqueia a leitura para evitar dados corrompidos.
    private static final long serialVersionUID = 1L;

    // Atributos privados (Encapsulamento). Ninguém mexe neles diretamente.
    private int id;
    private String nome;
    private double preco;

    // [CONSTRUTOR]
    // Chamado quando fazemos 'new Produto()'.
    // TRUQUE DE MESTRE: Chamamos os métodos SET aqui dentro.
    // Por que? Para garantir que a validação (ex: preço negativo) funcione
    // logo na criação do objeto. Impede criar produto inválido.
    public Produto(int id, String nome, double preco) {
        this.setId(id);
        this.setNome(nome);
        this.setPreco(preco);
    }

    // --- GETTERS E SETTERS (Com Validação) ---

    public int getId() { return id; }

    public void setId(int id) {
        // [VALIDAÇÃO] Regra de Negócio: ID não pode ser zero ou negativo.
        if (id <= 0) {
            // "throw": Para a execução e lança uma bomba (Erro).
            // Quem chamou esse método (a Main) vai ter que lidar com isso no 'catch'.
            throw new IllegalArgumentException("O ID deve ser maior que zero!");
        }
        this.id = id;
    }

    public String getNome() { return nome; }

    public void setNome(String nome) {
        // [VALIDAÇÃO] .trim().isEmpty(): Verifica se é vazio ou só tem espaços em branco.
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio!");
        }
        this.nome = nome;
    }

    public double getPreco() { return preco; }

    public void setPreco(double preco) {
        // [VALIDAÇÃO] Preço não pode ser negativo.
        if (preco < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo!");
        }
        this.preco = preco;
    }

    // [AUXILIAR] Método próprio para formatar a linha do CSV (Arquivo Texto).
    // Evita ter que ficar concatenando strings lá no Controller.
    public String paraTexto() {
        return id + ";" + nome + ";" + preco;
    }

    // ========================================================================
    // NOVO: MANIPULAÇÃO DE DIRETÓRIOS (Baseado na sua foto)
    // Teoria: Usa a classe File para listar conteúdo, verificar tipos e tamanhos.
    // ========================================================================

    /**
     * Lista todo o conteúdo de uma pasta específica.
     * Mostra no console se é arquivo ou diretório e o tamanho em bytes.
     * Baseado na lógica de FileDirectoryTest.
     * * @param caminhoDiretorio O caminho da pasta para escanear.
     */
    public void listarConteudoDoDiretorio(String caminhoDiretorio) {
        File diretorio = new File(caminhoDiretorio);

        // 1. Validação básica
        if (!diretorio.exists()) {
            System.out.println("Erro: O diretório não existe: " + caminhoDiretorio);
            return;
        }
        if (!diretorio.isDirectory()) {
            System.out.println("Erro: O caminho informado não é uma pasta!");
            return;
        }

        // 2. listFiles(): Retorna um array com tudo que tem dentro da pasta
        File[] conteudo = diretorio.listFiles();

        System.out.println("--- CONTEÚDO DA PASTA: " + diretorio.getName() + " ---");

        if (conteudo != null) {
            for (File item : conteudo) {
                // 3. isDirectory(): Verifica se é pasta ou arquivo
                if (item.isDirectory()) {
                    System.out.println("[PASTA]   " + item.getName());
                } else {
                    // 4. length(): Pega o tamanho do arquivo em bytes
                    System.out.println("[ARQUIVO] " + item.getName() +
                            " | Tamanho: " + item.length() + " bytes");
                }
            }
        } else {
            System.out.println("A pasta está vazia ou erro de acesso.");
        }
        System.out.println("----------------------------------------------");
    }

    // [VISUAL] Quando o JOptionPane mostra o objeto, ele usa esse texto.
    @Override
    public String toString() {
        return "Produto [ID=" + id + ", Nome=" + nome + ", R$=" + preco + "]";
    }
}