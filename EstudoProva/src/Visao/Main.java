package Visao;

import Controle.ArquivoController;
import Model.Produto;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // 1. Instancia o Controller (O Cérebro)
        ArquivoController controller = new ArquivoController();

        // Lista Temporária (RAM). Os dados vivem aqui enquanto o programa roda.
        List<Produto> listaMemoria = new ArrayList<>();

        String[] menu = {"1. Cadastrar", "2. Salvar", "3. Conversões", "4. Sair"};

        while (true) {
            int op = JOptionPane.showOptionDialog(null, "Sistema", "Prova",
                    0, 3, null, menu, menu[0]);

            if (op == 3 || op == -1) break;

            // [TEORIA] Try-Catch Geral
            // Qualquer erro que acontecer dentro desse bloco (seja validação,
            // erro de arquivo, ou digitação errada) cai nos 'catch' lá embaixo.
            try {
                switch (op) {
                    case 0: // CADASTRAR
                        // showInputDialog retorna String. Parse converte pra número.
                        int id = Integer.parseInt(JOptionPane.showInputDialog("ID:"));
                        String nome = JOptionPane.showInputDialog("Nome:");
                        double preco = Double.parseDouble(JOptionPane.showInputDialog("Preço:"));

                        // [MOMENTO DA VALIDAÇÃO]
                        // Ao dar 'new', o Modelo verifica se ID<=0 ou Preço<0.
                        // Se estiver errado, ele lança IllegalArgumentException AQUI.
                        Produto p = new Produto(id, nome, preco);

                        listaMemoria.add(p);
                        JOptionPane.showMessageDialog(null, "Cadastrado!");
                        break;

                    case 1: // SALVAR
                        if (listaMemoria.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Nada para salvar!");
                            break;
                        }

                        // JFileChooser: A janela de selecionar arquivos
                        JFileChooser ch = new JFileChooser();

                        // showSaveDialog: Pausa o código até o usuário escolher.
                        if (ch.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File arquivo = ch.getSelectedFile();

                            // Pergunta formato
                            String[] tipos = {"TXT", "BIN", "OBJ"};
                            int tipo = JOptionPane.showOptionDialog(null, "Qual formato?", "Tipo",
                                    0, 3, null, tipos, tipos[0]);

                            // Chamada Polimórfica (quase):
                            // Dependendo da escolha, chamamos um método diferente do Controller.
                            if (tipo == 0) {
                                arquivo = garantirExtensao(arquivo, ".txt");
                                controller.salvarTexto(arquivo, listaMemoria);
                            } else if (tipo == 1) {
                                arquivo = garantirExtensao(arquivo, ".dat");
                                controller.salvarBinario(arquivo, listaMemoria);
                            } else if (tipo == 2) {
                                arquivo = garantirExtensao(arquivo, ".obj");
                                controller.salvarObjeto(arquivo, listaMemoria);
                            }
                            JOptionPane.showMessageDialog(null, "Arquivo Salvo!");
                        }
                        break;

                    case 2: // CONVERSÕES
                        String[] convs = {"OBJ -> TXT", "TXT -> OBJ"};
                        int tipoConv = JOptionPane.showOptionDialog(null, "Qual?", "Conv",
                                0, 3, null, convs, convs[0]);

                        // Selecionar Origem
                        JFileChooser chIn = new JFileChooser();
                        chIn.setDialogTitle("Selecione ORIGEM");
                        if (chIn.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File origem = chIn.getSelectedFile();

                            // Selecionar Destino
                            JFileChooser chOut = new JFileChooser();
                            chOut.setDialogTitle("Selecione DESTINO");
                            if (chOut.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                                File destino = chOut.getSelectedFile();

                                if (tipoConv == 0) {
                                    destino = garantirExtensao(destino, ".txt");
                                    controller.converterObjParaTxt(origem, destino);
                                } else {
                                    destino = garantirExtensao(destino, ".obj");
                                    controller.converterTxtParaObj(origem, destino);
                                }
                                JOptionPane.showMessageDialog(null, "Convertido!");
                            }
                        }
                        break;
                }

                // --- TRATAMENTO DE ERROS (Diferencial na Prova) ---

            } catch (NumberFormatException e) {
                // Cai aqui se digitar letras no campo de preço/id
                JOptionPane.showMessageDialog(null, "Erro: Digite apenas números válidos!");

            } catch (IllegalArgumentException e) {
                // Cai aqui se o Modelo reclamar (Preço negativo, nome vazio)
                JOptionPane.showMessageDialog(null, "Validação: " + e.getMessage());

            } catch (Exception e) {
                // Cai aqui se der erro de arquivo (Disco cheio, sem permissão, etc)
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro Técnico: " + e.getMessage());
            }
        }
    }

    // Método auxiliar para garantir que o arquivo tenha o final correto (.txt, .obj)
    private static File garantirExtensao(File f, String ext) {
        if (!f.getName().toLowerCase().endsWith(ext)) {
            return new File(f.getAbsolutePath() + ext);
        }
        return f;
    }
}