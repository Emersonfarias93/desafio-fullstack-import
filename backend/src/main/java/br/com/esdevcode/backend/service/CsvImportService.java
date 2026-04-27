package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.config.ImportProperties;
import br.com.esdevcode.backend.exception.BusinessException;
import br.com.esdevcode.backend.messaging.producer.LoteEventProducer;
import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.model.entities.LoteItem;
import br.com.esdevcode.backend.model.enums.DecisaoUsuario;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CsvImportService {

    private final LoteService loteService;
    private final LoteItemService loteItemService;
    private final LeadService leadService;
    private final LoteEventProducer loteEventProducer;
    private final CsvFileValidator csvFileValidator;
    private final CsvLeadParser csvLeadParser;
    private final ImportProperties importProperties;

    public CsvImportService(
            LoteService loteService,
            LoteItemService loteItemService,
            LeadService leadService,
            LoteEventProducer loteEventProducer,
            CsvFileValidator csvFileValidator,
            CsvLeadParser csvLeadParser,
            ImportProperties importProperties
    ) {
        this.loteService = loteService;
        this.loteItemService = loteItemService;
        this.leadService = leadService;
        this.loteEventProducer = loteEventProducer;
        this.csvFileValidator = csvFileValidator;
        this.csvLeadParser = csvLeadParser;
        this.importProperties = importProperties;
    }

    @Transactional
    public Lote iniciarImportacao(MultipartFile arquivo) {
        csvFileValidator.validar(arquivo);

        Path caminhoArquivo = salvarArquivo(arquivo);

        try {
            Lote lote = loteService.criarLote(arquivo.getOriginalFilename(), caminhoArquivo.toString());
            preValidar(lote, caminhoArquivo);
            loteService.recalcularTotais(lote.getId());
            return iniciarProcessamento(lote.getId());
        } catch (RuntimeException exception) {
            apagarArquivoSilenciosamente(caminhoArquivo);
            throw exception;
        }
    }

    private Lote iniciarProcessamento(UUID loteId) {
        Lote processando = loteService.iniciarProcessamento(loteId);
        publicarLoteIniciadoAposCommit(loteId);

        return processando;
    }

    private void publicarLoteIniciadoAposCommit(UUID loteId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            loteEventProducer.publicarLoteIniciado(loteId);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                loteEventProducer.publicarLoteIniciado(loteId);
            }
        });
    }

    private void preValidar(Lote lote, Path caminhoArquivo) {
        List<CsvLeadRow> linhas = csvLeadParser.parse(caminhoArquivo);

        if (linhas.isEmpty()) {
            throw new BusinessException("CSV sem linhas de dados.");
        }

        Set<String> emailsValidos = linhas.stream()
                .filter(CsvLeadRow::valido)
                .map(CsvLeadRow::emailNormalizado)
                .filter(email -> !email.isBlank())
                .collect(Collectors.toSet());

        Set<String> emailsExistentes = leadService.findExistingEmails(emailsValidos);
        Set<String> emailsVistosNoArquivo = new HashSet<>();
        List<LoteItem> itens = new ArrayList<>(linhas.size());

        for (CsvLeadRow linha : linhas) {
            LoteItem item = new LoteItem();
            item.setLote(lote);
            item.setLinhaCsv(linha.linhaCsv());
            item.setNome(linha.nome());
            item.setEmail(linha.emailNormalizado());
            item.setTelefone(linha.telefone());
            item.setOrigem(linha.origem());
            item.setDataCadastro(linha.dataCadastro());

            if (!linha.valido()) {
                item.setStatus(LoteItemStatus.INVALIDO);
                item.setDecisaoUsuario(DecisaoUsuario.PENDENTE);
                item.setMotivo(linha.erro());
            } else if (emailsExistentes.contains(linha.emailNormalizado())) {
                item.setStatus(LoteItemStatus.DUPLICADO_EXATO);
                item.setDecisaoUsuario(DecisaoUsuario.IGNORAR);
                item.setMotivo("Email ja existe no banco.");
            } else if (!emailsVistosNoArquivo.add(linha.emailNormalizado())) {
                item.setStatus(LoteItemStatus.DUPLICADO_EXATO);
                item.setDecisaoUsuario(DecisaoUsuario.IGNORAR);
                item.setMotivo("Email duplicado no CSV.");
            } else {
                item.setStatus(LoteItemStatus.VALIDO);
                item.setDecisaoUsuario(DecisaoUsuario.IMPORTAR);
            }

            itens.add(item);
        }

        loteItemService.saveAllEmLotes(itens, 1000);
    }

    private Path salvarArquivo(MultipartFile arquivo) {
        try {
            Path uploadDir = Path.of(importProperties.uploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            String nomeOriginal = arquivo.getOriginalFilename() == null ? "leads.csv" : arquivo.getOriginalFilename();
            String nomeSeguro = nomeOriginal.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path destino = uploadDir.resolve(UUID.randomUUID() + "-" + nomeSeguro);
            arquivo.transferTo(destino);
            return destino;
        } catch (IOException exception) {
            throw new BusinessException("Nao foi possivel persistir o arquivo CSV.");
        }
    }

    private void apagarArquivoSilenciosamente(Path caminhoArquivo) {
        try {
            Files.deleteIfExists(caminhoArquivo);
        } catch (IOException ignored) {
        }
    }
}
