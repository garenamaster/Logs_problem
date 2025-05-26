package model;

import java.math.BigDecimal;

public class Operation {
    public final OperationType type; // тип операции
    public final BigDecimal amount; // сумма операции
    public final String counterparty; // получатель/отправитель

    public Operation(OperationType type, BigDecimal amount, String counterparty) {
        this.type = type;
        this.amount = amount;
        this.counterparty = counterparty;
    }
}
