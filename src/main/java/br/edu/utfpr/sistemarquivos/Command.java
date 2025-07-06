package br.edu.utfpr.sistemarquivos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

public enum Command {

    LIST() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("LIST") || commands[0].startsWith("list");
        }

        @Override
        Path execute(Path path) throws IOException {

            try(var stream = Files.list(path)) {
                stream.forEach(p -> System.out.println(p.getFileName()));
            }

            return path;
        }
    },
    SHOW() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("SHOW") || commands[0].startsWith("show");
        }

        @Override
        Path execute(Path path) {

            if(parameters.length < 2 || parameters[1].isBlank()){
                throw new UnsupportedOperationException("Erro: nenhum arquivo informado.");
            }

            Path file = path.resolve(parameters[1]);

            new FileReader().read(file);

            return path;
        }
    },
    BACK() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("BACK") || commands[0].startsWith("back");
        }

        @Override
        Path execute(Path path) {

            Path rootPath = Paths.get(Application.ROOT);

            if(path.equals(rootPath)){
                throw new UnsupportedOperationException("Você já está no diretório raiz");
            }

            return path.getParent();
        }
    },
    OPEN() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("OPEN") || commands[0].startsWith("open");
        }

        @Override
        Path execute(Path path) {

            if(parameters.length < 2 || parameters[1].isBlank()){
                throw new UnsupportedOperationException("Necessário informar o nome do diretório");
            }

            Path newPath = path.resolve(parameters[1]);

            if(!Files.isDirectory(newPath)){
                throw new UnsupportedOperationException("Diretório não encontrado: " + parameters[1]);
            }

            return newPath;
        }
    },
    DETAIL() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("DETAIL") || commands[0].startsWith("detail");
        }

        @Override
        Path execute(Path path) {

            if(parameters.length < 2 || parameters[1].isBlank()){
                throw new UnsupportedOperationException("Erro: arquivo ou diretório não informado.");
            }

            Path file = path.resolve(parameters[1]);

            if(!Files.exists(file)){
                throw new UnsupportedOperationException("Erro: arquivo ou diretório não informado.");
            }

            try{
                BasicFileAttributeView view = Files.getFileAttributeView(file, BasicFileAttributeView.class);
                BasicFileAttributes attrbts = view.readAttributes();

                System.out.println("É diretório: " + attrbts.isDirectory());
                System.out.println("Tamanho: " + "[" + attrbts.size() + "]");
                System.out.println("Criação: " + attrbts.creationTime());
                System.out.println("Ultimo acesso: " + attrbts.lastAccessTime());
                System.out.println("Ultima modificação: " + attrbts.lastModifiedTime());
            }
            catch (IOException e){
                throw new UnsupportedOperationException("Erro ao ler detalhes do arquivo: " + e.getMessage());
            }

            return path;
        }
    },
    EXIT() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("EXIT") || commands[0].startsWith("exit");
        }

        @Override
        Path execute(Path path) {
            System.out.print("Saindo...");
            return path;
        }

        @Override
        boolean shouldStop() {
            return true;
        }
    };

    abstract Path execute(Path path) throws IOException;

    abstract boolean accept(String command);

    void setParameters(String[] parameters) {
    }

    boolean shouldStop() {
        return false;
    }

    public static Command parseCommand(String commandToParse) {

        if (commandToParse.isBlank()) {
            throw new UnsupportedOperationException("Type something...");
        }

        final var possibleCommands = values();

        for (Command possibleCommand : possibleCommands) {
            if (possibleCommand.accept(commandToParse)) {
                possibleCommand.setParameters(commandToParse.split(" "));
                return possibleCommand;
            }
        }

        throw new UnsupportedOperationException("Can't parse command [%s]".formatted(commandToParse));
    }
}
