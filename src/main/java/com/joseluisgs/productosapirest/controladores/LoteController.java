package com.joseluisgs.productosapirest.controladores;

import com.joseluisgs.productosapirest.dto.CreateLoteDTO;
import com.joseluisgs.productosapirest.error.ApiError;
import com.joseluisgs.productosapirest.error.LoteCreateException;
import com.joseluisgs.productosapirest.error.LoteNotFoundException;
import com.joseluisgs.productosapirest.modelos.Lote;
import com.joseluisgs.productosapirest.servicios.LoteServicio;
import com.joseluisgs.productosapirest.utils.pagination.PaginationLinksUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoteController {

    private final LoteServicio loteServicio;
    private final PaginationLinksUtils paginationLinksUtils;


    @ApiOperation(value = "Obtiene una lista de lotes", notes = "Obtiene una lista de lotes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Lote.class),
            @ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)
    })
    @GetMapping("/lotes")
    public ResponseEntity<?> lotes(Pageable pageable, HttpServletRequest request) throws LoteNotFoundException {
        Page<Lote> result = loteServicio.findAll(pageable);


        if (result.isEmpty()) {
            throw new LoteNotFoundException();
        } else {

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

            return ResponseEntity.ok().header("link", paginationLinksUtils.createLinkHeader(result, uriBuilder))
                    .body(result);

        }
    }


    /**
     * Insertamos un nuevo lote
     *
     * @param nuevoLote nuevo lote a insertar
     * @return 201 y el lote insertado
     */
    @ApiOperation(value = "Crear un nuevo Lote", notes = "Provee la operación para crear un nuevo lote a partir de un CreateLoteDto y devuelve el objeto creado")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OK", response = Lote.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)
    })
    @PostMapping("/lotes")
    public ResponseEntity<?> nuevoLote(@RequestBody CreateLoteDTO nuevoLote) {
        try {
            Lote lote = loteServicio.nuevoLote(nuevoLote);
            return ResponseEntity.status(HttpStatus.CREATED).body(lote);
        } catch (LoteCreateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

}
