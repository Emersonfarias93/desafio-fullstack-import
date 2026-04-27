package br.com.esdevcode.backend.controller;

import br.com.esdevcode.backend.model.dto.LeadResponseDTO;
import br.com.esdevcode.backend.service.LeadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public Page<LeadResponseDTO> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String origem,
            Pageable pageable
    ) {
        return leadService.findByFilters(nome, email, origem, pageable)
                .map(LeadResponseDTO::fromEntity);
    }
}