package com.qthegamep.diploma.project.back.service;

import com.qthegamep.diploma.project.back.dto.GenerationRequestDTO;
import com.qthegamep.diploma.project.back.dto.GenerationResponseDTO;

@FunctionalInterface
public interface GenerationService {
    GenerationResponseDTO generate(GenerationRequestDTO generationRequestDTO, String requestId);
}
