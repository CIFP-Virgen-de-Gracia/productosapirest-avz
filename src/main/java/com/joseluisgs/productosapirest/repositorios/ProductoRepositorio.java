package com.joseluisgs.productosapirest.repositorios;

import com.joseluisgs.productosapirest.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

// Creamos el repositorio extendéndolo de JPA, siguiendo DAO
// Con ello ya tenemos las operaciones básicas de CRUD y Paginación
// extiende de JpaSpecificationExecutor para tener las opciones de Specificación y busqueda con Criteria
public interface ProductoRepositorio extends
        JpaRepository<Producto, Long>,
        JpaSpecificationExecutor<Producto> {

    // Buscamos por el campo nombre ingnoranod mayusculas y minusculas
    // select * from producto where upper(nombre) like '%txt%'
    Page<Producto> findByNombreContainsIgnoreCase(String txt, Pageable pageable);

    @Query("select p from Producto p LEFT JOIN FETCH p.lotes WHERE p.id = :id")
    public Optional<Producto> findByIdJoinFetch(Long id);
}
