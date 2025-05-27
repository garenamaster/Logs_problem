package app;

import model.Operation;
import parser.LogParser;
import service.UserLogs;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public final class Main {

    public static void main(String[] args) throws Exception {
        Path root;
        if (args.length == 0) {
            root = Paths.get("logs").toAbsolutePath();
        } else {
            root = Paths.get(args[0]).toAbsolutePath();
        }

        if (!Files.isDirectory(root)) {
            System.out.println("Каталог не найден: " + root);
            return;
        }

        Path outDir = root.resolve("transactions_by_users");

        UserLogs userLogs = new UserLogs();

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(root, "*.log")) {
            for (Path file : ds) {
                if (file.getParent().equals(outDir)) {
                    continue;
                }
                List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                for (String raw : lines) {
                    Object[] parsed = LogParser.parse(raw);
                    if (parsed == null) {
                        continue;
                    }
                    userLogs.accept(
                            (String)   parsed[0], // timestamp
                            (String)   parsed[1], // actor
                            (Operation) parsed[2]); // операция
                }
            }
        }

        userLogs.save(outDir);
        System.out.println("Файлы созданы в " + outDir);
    }
}
