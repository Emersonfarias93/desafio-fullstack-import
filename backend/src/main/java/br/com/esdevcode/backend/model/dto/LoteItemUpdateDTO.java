package br.com.esdevcode.backend.model.dto;

public record LoteItemUpdateDTO(
        String nome,
        String email,
        String telefone,
        String origem,
        String dataCadastro,
        Boolean ignorar
) {
}
