package com.joseluisgs.productosapirest.servicios;

import com.joseluisgs.productosapirest.modelos.Categoria;
import com.joseluisgs.productosapirest.repositorios.CategoriaRepositorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class CategoriaServicio extends BaseService<Categoria, Long, CategoriaRepositorio> {

    // De esta manera mapeamos el metodos del repositorio
    public Page<Categoria> findByNombre(String txt, Pageable pageable) {
        return this.repositorio.findByNombreContainsIgnoreCase(txt, pageable);
    }

}
