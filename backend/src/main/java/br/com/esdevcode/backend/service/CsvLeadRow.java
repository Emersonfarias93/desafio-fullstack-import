package br.com.esdevcode.backend.service;

import java.time.LocalDateTime;

public record CsvLeadRow(
        int linhaCsv,
        String nome,
        String email,
        String telefone,
        String origem,
        LocalDateTime dataCadastro,
        String erro
) {
    public boolean valido() {
        return erro == null || erro.isBlank();
    }

    public String emailNormalizado() {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
