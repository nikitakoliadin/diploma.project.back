package com.qthegamep.diploma.project.back.controllers;

import com.qthegamep.diploma.project.back.dto.SignRequestDTO;
import com.qthegamep.diploma.project.back.dto.SignResponseDTO;
import com.qthegamep.diploma.project.back.service.SignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

@Path("/make")
public class SignController {
    private static final Logger LOG = LoggerFactory.getLogger(SignController.class.getName());
    private SignService signService;

    @Inject
    public SignController(SignService signService) {
        this.signService = signService;
    }

    @Context
    private HttpHeaders httpHeaders;

    @POST
    @Path("/sign")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response catchRequestToSign(SignRequestDTO signRequestDTO) {
        LOG.info("Time getting request: {}", LocalDateTime.now());
        LOG.info("Request: {}", signRequestDTO.getData());
        String requestId = httpHeaders.getHeaderString("requestId");
        SignResponseDTO responseDTO = signService.sign(signRequestDTO, requestId);
        return Response.status(Response.Status.OK)
                .entity(responseDTO)
                .build();
    }
}
