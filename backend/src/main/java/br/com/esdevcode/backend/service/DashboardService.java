package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.model.dto.DashboardResponseDTO;
import br.com.esdevcode.backend.model.enums.LoteStatus;
import br.com.esdevcode.backend.repository.LeadRepository;
import br.com.esdevcode.backend.repository.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final LeadRepository leadRepository;
    private final LoteRepository loteRepository;

    public DashboardService(LeadRepository leadRepository, LoteRepository loteRepository) {
        this.leadRepository = leadRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO buscarResumo() {
        long totalLeads = leadRepository.count();
        long totalLotes = loteRepository.count();
        long lotesProcessando = loteRepository.countByStatus(LoteStatus.PROCESSANDO);
        long lotesFinalizados = loteRepository.countByStatus(LoteStatus.FINALIZADO);
        long totalLinhas = loteRepository.sumTotalLinhas();
        long totalErros = loteRepository.sumTotalErros();
        double taxaErro = totalLinhas == 0 ? 0.0 : (totalErros * 100.0) / totalLinhas;

        return new DashboardResponseDTO(
                totalLeads,
                totalLotes,
                lotesProcessando,
                lotesFinalizados,
                totalLinhas,
                totalErros,
                taxaErro
        );
    }
}
