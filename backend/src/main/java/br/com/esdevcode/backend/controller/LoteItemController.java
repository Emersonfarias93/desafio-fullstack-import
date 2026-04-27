package br.com.esdevcode.backend.controller;

import br.com.esdevcode.backend.model.dto.LoteItemResponseDTO;
import br.com.esdevcode.backend.model.dto.LoteItemUpdateDTO;
import br.com.esdevcode.backend.model.entities.LoteItem;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;
import br.com.esdevcode.backend.service.LoteItemService;
import br.com.esdevcode.backend.service.LoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lotes/{loteId}/itens")
public class LoteItemController {

    private final LoteItemService loteItemService;
    private final LoteService loteService;

    public LoteItemController(LoteItemService loteItemService, LoteService loteService) {
        this.loteItemService = loteItemService;
        this.loteService = loteService;
    }

    @GetMapping
    public Page<LoteItemResponseDTO> listarItens(
            @PathVariable UUID loteId,
            @RequestParam(required = false) LoteItemStatus status,
            Pageable pageable
    ) {
        if (status != null) {
            return loteItemService.findByLoteIdAndStatus(loteId, status, pageable)
                    .map(LoteItemResponseDTO::fromEntity);
        }

        return loteItemService.findByLoteId(loteId, pageable)
                .map(LoteItemResponseDTO::fromEntity);
    }

    @PatchMapping("/{itemId}")
    public LoteItemResponseDTO atualizarItem(
            @PathVariable UUID loteId,
            @PathVariable UUID itemId,
            @RequestBody LoteItemUpdateDTO dto
    ) {
        LoteItem item = loteItemService.atualizar(loteId, itemId, dto);
        loteService.recalcularTotais(loteId);
        return LoteItemResponseDTO.fromEntity(item);
    }
}
