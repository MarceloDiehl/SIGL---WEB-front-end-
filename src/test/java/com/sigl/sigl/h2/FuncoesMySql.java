package com.sigl.sigl.h2;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Funções do MySQL usadas pelas migrações e que o H2 não tem, registradas no banco de teste
 * por db/h2/beforeMigrate.sql. Só existem nos testes; em produção o banco é MySQL.
 */
public final class FuncoesMySql {

    private FuncoesMySql() {
    }

    /** TIMESTAMP(data) do MySQL: a data à meia-noite (usado na V2). */
    public static Timestamp timestamp(Date data) {
        return data == null ? null : new Timestamp(data.getTime());
    }
}
