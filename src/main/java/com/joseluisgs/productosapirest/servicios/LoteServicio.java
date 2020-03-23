package com.joseluisgs.productosapirest.servicios;

import com.joseluisgs.productosapirest.dto.CreateLoteDTO;
import com.joseluisgs.productosapirest.error.LoteCreateException;
import com.joseluisgs.productosapirest.modelos.Lote;
import com.joseluisgs.productosapirest.repositorios.LoteRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoteServicio extends BaseService<Lote, Long, LoteRepositorio> {

    private final ProductoServicio productoServicio;

    @Override
    public Optional<Lote> findById(Long id) {
        return repositorio.findByIdJoinFetch(id);
    }

    public Lote nuevoLote(CreateLoteDTO nuevoLote) {

        Lote l = Lote.builder()
                .nombre(nuevoLote.getNombre())
                .build();

        nuevoLote.getProductos().stream()
                .map(id -> {
                    return productoServicio.findByIdConLotes(id).orElseThrow(() -> new LoteCreateException());
                })
                .forEach(l::addProducto);

        return save(l);

    }


}
