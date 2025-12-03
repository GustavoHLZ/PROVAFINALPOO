package Controle;

import Model.Produto;
import java.io.*; // Importa tudo de arquivo
import java.util.ArrayList;
import java.util.List;

public class ArquivoController {

    // ========================================================================
    // TIPO 1: ARQUIVO TEXTO (.txt)
    // Teoria: Usa classes "Writer". Converte dados em caracteres legíveis.
    // ========================================================================
    public void salvarTexto(File arquivo, List<Produto> lista) throws IOException {

        // [TEORIA] Try-with-resources:
        // Tudo dentro do parênteses do try (...) será fechado (.close()) automaticamente.
        // FileWriter: Conecta com o arquivo no disco. 'false' = sobrescreve tudo.
        // PrintWriter: Classe "decoradora". Adiciona métodos fáceis como 'println'.
        try (FileWriter fw = new FileWriter(arquivo, false);
             PrintWriter pw = new PrintWriter(fw)) {

            for (Produto p : lista) {
                // Escreve "1;Mouse;20.0" e pula linha (\n)
                pw.println(p.paraTexto());
            }
        }
    }

    // ========================================================================
    // TIPO 2: ARQUIVO BINÁRIO PURO (.dat)
    // Teoria: Usa classes "Stream". Grava bytes brutos.
    // DataOutputStream: Sabe transformar int em 4 bytes, double em 8 bytes, etc.
    // ========================================================================
    public void salvarBinario(File arquivo, List<Produto> lista) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(arquivo);
             DataOutputStream dos = new DataOutputStream(fos)) {

            // [DICA] Gravamos o tamanho da lista primeiro (int).
            // Ajuda na leitura futura (saber quantas vezes rodar o loop).
            dos.writeInt(lista.size());

            for (Produto p : lista) {
                // Grava cada campo separadamente.
                dos.writeInt(p.getId());
                dos.writeUTF(p.getNome()); // UTF = String em formato binário java
                dos.writeDouble(p.getPreco());
            }
        }
    }

    // ========================================================================
    // TIPO 3: ARQUIVO OBJETO (.obj) - A SERIALIZAÇÃO
    // Teoria: ObjectOutputStream pega o objeto inteiro da memória e "congela".
    // ========================================================================
    public void salvarObjeto(File arquivo, List<Produto> lista) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(arquivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            // [MÁGICA] Grava a lista toda de uma vez.
            // O Java percorre a lista e serializa item por item automaticamente.
            oos.writeObject(lista);
        }
    }

    // LEITURA DE OBJETO (Para carregar na RAM)
    public List<Produto> lerObjeto(File arquivo) throws IOException, ClassNotFoundException {

        try (FileInputStream fis = new FileInputStream(arquivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            // [TEORIA] Casting (List<Produto>):
            // readObject retorna um "Object" genérico. Nós garantimos ao Java:
            // "Pode confiar, isso aqui é uma Lista de Produtos".
            return (List<Produto>) ois.readObject();
        }
    }

    // ========================================================================
    // CONVERSÃO 1: OBJETO -> TEXTO
    // Teoria: A memória RAM é a ponte. Carrega do .obj -> RAM -> Salva .txt
    // ========================================================================
    public void converterObjParaTxt(File origemObj, File destinoTxt) throws Exception {
        // 1. Usa o método de leitura que já criamos
        List<Produto> lista = lerObjeto(origemObj);

        // 2. Usa o método de escrita texto que já criamos
        salvarTexto(destinoTxt, lista);
    }

    // ========================================================================
    // CONVERSÃO 2: TEXTO -> OBJETO (Parsing)
    // Teoria: Ler Texto -> Quebrar String (Split) -> Converter (Parse) -> Criar Objeto
    // ========================================================================
    public void converterTxtParaObj(File origemTxt, File destinoObj) throws IOException {
        List<Produto> listaTemp = new ArrayList<>();

        // BufferedReader: Lê grandes blocos de texto de forma eficiente.
        // FileReader: Lê caractere por caractere. O BufferedReader agrupa em linhas.
        try (FileReader fr = new FileReader(origemTxt);
             BufferedReader br = new BufferedReader(fr)) {

            String linha;
            // Loop: Lê linha a linha até acabar (null)
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue; // Pula linha vazia

                // [TEORIA] Split: Corta a string nos ";" criando um vetor.
                String[] dados = linha.split(";");

                if (dados.length < 3) continue; // Ignora linha incompleta

                // [TEORIA] Parsing: O arquivo texto é "burro", tudo é String.
                // Integer.parseInt: Transforma texto "10" em número 10.
                int id = Integer.parseInt(dados[0].trim());
                String nome = dados[1].trim();
                double preco = Double.parseDouble(dados[2].trim());

                // Cria o objeto (Passa pela validação do Modelo aqui!)
                listaTemp.add(new Produto(id, nome, preco));
            }
        }

        if (!listaTemp.isEmpty()) {
            salvarObjeto(destinoObj, listaTemp);
        } else {
            throw new IOException("Arquivo texto vazio ou inválido!");
        }
    }
}