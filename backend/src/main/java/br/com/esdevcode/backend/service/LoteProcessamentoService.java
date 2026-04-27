package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.model.entities.LoteProcessamento;
import br.com.esdevcode.backend.model.enums.ProcessamentoStatus;
import br.com.esdevcode.backend.repository.LoteProcessamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoteProcessamentoService {

    private final LoteProcessamentoRepository repository;

    public LoteProcessamentoService(LoteProcessamentoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LoteProcessamento criarProcessamento(Lote lote, Integer numeroChunk, Integer totalLinhas) {
        LoteProcessamento processamento = new LoteProcessamento();
        processamento.setLote(lote);
        processamento.setNumeroChunk(numeroChunk);
        processamento.setTotalLinhas(totalLinhas);
        processamento.setStatus(ProcessamentoStatus.PENDENTE);

        return repository.save(processamento);
    }

    @Transactional
    public LoteProcessamento iniciar(UUID processamentoId) {
        LoteProcessamento processamento = findById(processamentoId);
        processamento.setStatus(ProcessamentoStatus.PROCESSANDO);
        processamento.setIniciadoEm(LocalDateTime.now());

        return repository.save(processamento);
    }

    @Transactional
    public LoteProcessamento concluir(UUID processamentoId, Integer totalSucesso, Integer totalErros, Long tempoMs) {
        LoteProcessamento processamento = findById(processamentoId);
        processamento.setStatus(ProcessamentoStatus.CONCLUIDO);
        processamento.setTotalSucesso(totalSucesso);
        processamento.setTotalErros(totalErros);
        processamento.setTempoMs(tempoMs);
        processamento.setFinalizadoEm(LocalDateTime.now());

        return repository.save(processamento);
    }

    @Transactional
    public LoteProcessamento finalizarComErro(UUID processamentoId, String mensagemErro, Long tempoMs) {
        LoteProcessamento processamento = findById(processamentoId);
        processamento.setStatus(ProcessamentoStatus.ERRO);
        processamento.setMensagemErro(mensagemErro);
        processamento.setTempoMs(tempoMs);
        processamento.setFinalizadoEm(LocalDateTime.now());

        return repository.save(processamento);
    }

    @Transactional(readOnly = true)
    public LoteProcessamento findById(UUID processamentoId) {
        return repository.findById(processamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Processamento não encontrado: " + processamentoId));
    }

    @Transactional(readOnly = true)
    public List<LoteProcessamento> findByLoteId(UUID loteId) {
        return repository.findByLoteIdOrderByNumeroChunkAsc(loteId);
    }
}