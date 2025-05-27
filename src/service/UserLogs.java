package service;

import model.LogEntry;
import model.Operation;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UserLogs {

    private final Map<String, BigDecimal> balance = new HashMap<>();
    private final Map<String, List<LogEntry>> lines = new HashMap<>();

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //принимает одну операцию
    public void accept(String tsStr, String actor, Operation op) {
        LocalDateTime ts = LocalDateTime.parse(tsStr, F);

        switch (op.type) {
            case BALANCE_INQUIRY -> {
                balance.put(actor, op.amount);
                add(actor, ts, "[" + tsStr + "] " + actor + " balance inquiry " + op.amount);
            }
            case WITHDRAWAL -> {
                balance.merge(actor, op.amount.negate(), BigDecimal::add);
                add(actor, ts, "[" + tsStr + "] " + actor + " withdrew " + op.amount);
            }
            case TRANSFER_OUTGOING -> {
                balance.merge(actor, op.amount.negate(), BigDecimal::add);
                balance.merge(op.counterparty, op.amount, BigDecimal::add);

                add(actor, ts, "[" + tsStr + "] " + actor +
                        " transferred " + op.amount + " to " + op.counterparty);
                add(op.counterparty, ts, "[" + tsStr + "] " + op.counterparty +
                        " received " + op.amount + " from " + actor);
            }
        }
    }

    // запись user файлов в дир
    public void save(Path outDir) throws IOException {
        Files.createDirectories(outDir);
        String now = LocalDateTime.now().format(F);

        for (String user : lines.keySet()) {
            List<LogEntry> userLines = lines.get(user);
            Collections.sort(userLines);

            BigDecimal bal = balance.getOrDefault(user, BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

            userLines.add(new LogEntry(LocalDateTime.now(),
                    "[" + now + "] " + user + " final balance " + bal));

            Path file = outDir.resolve(user + ".log");
            List<String> raw = new ArrayList<>(userLines.size());
            for (LogEntry le : userLines) {
                raw.add(le.line);
            }

            Files.write(file, raw, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }
    }


    private void add(String user, LocalDateTime ts, String line) {
        List<LogEntry> list = lines.get(user);
        if (list == null) {
            list = new ArrayList<>();
            lines.put(user, list);
        }
        list.add(new LogEntry(ts, line));
    }

}
