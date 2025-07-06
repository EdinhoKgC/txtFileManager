package br.edu.utfpr.sistemarquivos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    public void read(Path path) {
        try {
            if (Files.isDirectory(path)){
                throw new UnsupportedOperationException("Erro: Show não pode ser usado para abrir diretórios");
            }

            if (!path.toString().toLowerCase().endsWith(".txt")) {
                throw new UnsupportedOperationException("Erro: Só é possível mostrar arquivos .txt");
            }

            try(var linhas = Files.lines(path)) {
                linhas.forEach(System.out::println);
            }
        }
        catch (IOException e){
            throw new UnsupportedOperationException("Erro ao ler arquivo: " + e.getMessage());
        }
    }
}
