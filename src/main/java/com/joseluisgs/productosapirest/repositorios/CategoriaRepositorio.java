package com.joseluisgs.productosapirest.repositorios;

import com.joseluisgs.productosapirest.modelos.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {

    // Buscamos categoria por nombre
    Page<Categoria> findByNombreContainsIgnoreCase(String txt, Pageable pageable);

}
