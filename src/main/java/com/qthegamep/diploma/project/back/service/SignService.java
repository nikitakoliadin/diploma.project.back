package com.qthegamep.diploma.project.back.service;

import com.qthegamep.diploma.project.back.dto.SignRequestDTO;
import com.qthegamep.diploma.project.back.dto.SignResponseDTO;

@FunctionalInterface
public interface SignService {
    SignResponseDTO sign(SignRequestDTO requestDTO, String requestId);
}
