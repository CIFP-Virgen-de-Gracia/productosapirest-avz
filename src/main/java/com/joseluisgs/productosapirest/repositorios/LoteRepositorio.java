package com.joseluisgs.productosapirest.repositorios;

import com.joseluisgs.productosapirest.modelos.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoteRepositorio extends JpaRepository<Lote, Long> {

    // Creamos nuestra Query
    @Query("select l from Lote l JOIN FETCH l.productos WHERE l.id = :id")
    public Optional<Lote> findByIdJoinFetch(Long id);

}
