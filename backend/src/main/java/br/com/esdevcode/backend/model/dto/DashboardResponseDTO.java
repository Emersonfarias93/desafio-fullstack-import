package br.com.esdevcode.backend.model.dto;

public record DashboardResponseDTO(
        long totalLeads,
        long totalLotes,
        long lotesProcessando,
        long lotesFinalizados,
        long totalLinhas,
        long totalErros,
        double taxaErro
) {
}
