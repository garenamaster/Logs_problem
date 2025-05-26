package parser;

import model.Operation;
import model.OperationType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    // [2025-05-10 09:05:44] user001 transferred 100.00 to user002
    private static final Pattern LINE = Pattern.compile("^\\[(.+?)]\\s+(\\w+)\\s+(.+)$");
    public static Object[] parse(String raw) {
        Matcher m = LINE.matcher(raw);
        if (!m.find()) return null;

        String dataTime = m.group(1);
        String actor = m.group(2);
        String tail = m.group(3);

        Operation op = null;

        String[] parts = tail.split("\\s+"); // общее для всех случаев
        String verb = parts[0];               // transferred / withdrew / balance
        BigDecimal amount;
        try{
            switch (verb) {
                case "balance":
                    amount = new BigDecimal(parts[2]); // inquiry <amount>
                    op = new Operation(OperationType.BALANCE_INQUIRY, amount, null);
                    break;

                case "withdrew":
                    amount = new BigDecimal(parts[1]); // withdrew <amount>
                    op = new Operation(OperationType.WITHDRAWAL, amount, null);
                    break;

                case "transferred":
                    amount = new BigDecimal(parts[1]); // transferred <amount> to <dest>
                    String dest = parts[3];
                    op = new Operation(OperationType.TRANSFER_OUTGOING, amount, dest);
                    break;

                default:
                    return null; // неизвестный формат
            }
        } catch (Exception e) {
            System.err.println("Ошибка парсинга строки: " + raw);
        }
        return new Object[] {dataTime, actor, op};
    }
}
