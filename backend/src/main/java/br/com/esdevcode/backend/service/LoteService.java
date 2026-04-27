package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.exception.ResourceNotFoundException;
import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;
import br.com.esdevcode.backend.model.enums.LoteStatus;
import br.com.esdevcode.backend.repository.LoteItemRepository;
import br.com.esdevcode.backend.repository.LoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final LoteItemRepository loteItemRepository;

    public LoteService(LoteRepository loteRepository, LoteItemRepository loteItemRepository) {
        this.loteRepository = loteRepository;
        this.loteItemRepository = loteItemRepository;
    }

    @Transactional
    public Lote criarLote(String nomeArquivo, String caminhoArquivo) {
        Lote lote = new Lote();
        lote.setNomeArquivo(nomeArquivo);
        lote.setCaminhoArquivo(caminhoArquivo);
        lote.setStatus(LoteStatus.RECEBIDO);

        return loteRepository.save(lote);
    }

    @Transactional
    public Lote iniciarProcessamento(UUID loteId) {
        Lote lote = buscarPorId(loteId);
        lote.setStatus(LoteStatus.PROCESSANDO);
        lote.setIniciadoEm(LocalDateTime.now());

        return loteRepository.save(lote);
    }

    @Transactional
    public Lote finalizar(UUID loteId) {
        Lote lote = recalcularTotais(loteId);
        lote.setStatus(LoteStatus.FINALIZADO);
        lote.setFinalizadoEm(LocalDateTime.now());

        return loteRepository.save(lote);
    }

    @Transactional
    public Lote finalizarComErro(UUID loteId, String mensagemErro) {
        Lote lote = recalcularTotais(loteId);
        lote.setStatus(LoteStatus.FINALIZADO_COM_ERRO);
        lote.setMensagemErro(mensagemErro);
        lote.setFinalizadoEm(LocalDateTime.now());

        return loteRepository.save(lote);
    }

    @Transactional
    public Lote recalcularTotais(UUID loteId) {
        Lote lote = buscarPorId(loteId);

        long totalLinhas = loteItemRepository.countByLoteId(loteId);
        long totalInvalidas = loteItemRepository.countByLoteIdAndStatus(loteId, LoteItemStatus.INVALIDO);
        long totalDuplicadas = loteItemRepository.countByLoteIdAndStatus(loteId, LoteItemStatus.DUPLICADO_EXATO);
        long totalPossiveisDuplicadas = loteItemRepository.countByLoteIdAndStatusIn(
                loteId,
                EnumSet.of(LoteItemStatus.POSSIVEL_DUPLICADO, LoteItemStatus.AGUARDANDO_DECISAO)
        );
        long totalImportadas = loteItemRepository.countByLoteIdAndStatus(loteId, LoteItemStatus.IMPORTADO);
        long totalIgnoradas = loteItemRepository.countByLoteIdAndStatus(loteId, LoteItemStatus.IGNORADO);
        long totalErros = loteItemRepository.countByLoteIdAndStatus(loteId, LoteItemStatus.ERRO);
        long totalValidas = loteItemRepository.countByLoteIdAndStatusIn(
                loteId,
                EnumSet.of(LoteItemStatus.VALIDO, LoteItemStatus.APROVADO, LoteItemStatus.IMPORTADO)
        );

        lote.setTotalLinhas(toInt(totalLinhas));
        lote.setTotalInvalidas(toInt(totalInvalidas));
        lote.setTotalDuplicadas(toInt(totalDuplicadas));
        lote.setTotalPossiveisDuplicadas(toInt(totalPossiveisDuplicadas));
        lote.setTotalImportadas(toInt(totalImportadas));
        lote.setTotalIgnoradas(toInt(totalIgnoradas));
        lote.setTotalErros(toInt(totalErros));
        lote.setTotalValidas(toInt(totalValidas));
        lote.setTotalNovas(toInt(totalValidas));

        return loteRepository.save(lote);
    }

    @Transactional(readOnly = true)
    public Page<Lote> listar(Pageable pageable) {
        return loteRepository.findAllByOrderByCriadoEmDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Lote buscarPorId(UUID loteId) {
        return loteRepository.findById(loteId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote nao encontrado: " + loteId));
    }

    private int toInt(long valor) {
        return valor > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) valor;
    }
}
