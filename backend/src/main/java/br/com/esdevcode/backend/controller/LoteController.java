package br.com.esdevcode.backend.controller;

import br.com.esdevcode.backend.model.dto.LoteResponseDTO;
import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.service.CsvImportService;
import br.com.esdevcode.backend.service.LoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private final CsvImportService csvImportService;
    private final LoteService loteService;

    public LoteController(CsvImportService csvImportService, LoteService loteService) {
        this.csvImportService = csvImportService;
        this.loteService = loteService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public LoteResponseDTO upload(@RequestParam("arquivo") MultipartFile arquivo) {
        Lote lote = csvImportService.iniciarImportacao(arquivo);
        return LoteResponseDTO.fromEntity(lote);
    }

    @GetMapping
    public Page<LoteResponseDTO> listar(Pageable pageable) {
        return loteService.listar(pageable)
                .map(LoteResponseDTO::fromEntity);
    }

    @GetMapping("/{id}/status")
    public LoteResponseDTO buscarStatus(@PathVariable UUID id) {
        Lote lote = loteService.buscarPorId(id);
        return LoteResponseDTO.fromEntity(lote);
    }
}
