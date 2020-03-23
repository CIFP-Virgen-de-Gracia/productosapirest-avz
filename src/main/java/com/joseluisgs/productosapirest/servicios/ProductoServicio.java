package com.joseluisgs.productosapirest.servicios;

import com.joseluisgs.productosapirest.controladores.FicherosController;
import com.joseluisgs.productosapirest.dto.CreateProductoDTO;
import com.joseluisgs.productosapirest.modelos.Producto;
import com.joseluisgs.productosapirest.repositorios.ProductoRepositorio;
import com.joseluisgs.productosapirest.upload.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Service
@RequiredArgsConstructor

// Un servicio encapsula operaciones con repositorios y algo más, ofrce métodos al Controlador entre otra scosas
public class ProductoServicio extends BaseService<Producto, Long, ProductoRepositorio> {

    private final CategoriaServicio categoriaServicio;
    private final StorageService storageService;


    public Producto nuevoProducto(CreateProductoDTO nuevo, MultipartFile file) {
        String urlImagen = null;


        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            urlImagen = MvcUriComponentsBuilder
                    .fromMethodName(FicherosController.class, "serveFile", imagen, null)
                    .build().toUriString();
        }


        // En ocasiones, no necesitamos el uso de ModelMapper si la conversión que vamos a hacer
        // es muy sencilla. Con el uso de @Builder sobre la clase en cuestión, podemos realizar
        // una transformación rápida como esta.

        Producto nuevoProducto = Producto.builder()
                .nombre(nuevo.getNombre())
                .precio(nuevo.getPrecio())
                .imagen(urlImagen)
                .categoria(categoriaServicio.findById(nuevo.getCategoriaId()).orElse(null))
                .build();

        return this.save(nuevoProducto);

    }

    // Método del servicio para encontrar producto respecto al nombre
    // De esta manera mapeamos el metodos del repositorio
    public Page<Producto> findByNombre(String txt, Pageable pageable) {
        return this.repositorio.findByNombreContainsIgnoreCase(txt, pageable);
    }

    // Busqueda global basado en argumentos globales,
    // Según esos argumentos así hará la búsqueda
    public Page<Producto> findByArgs(final Optional<String> nombre, final Optional<Float> precio, Pageable pageable) {
        // Ahora creamos las especificaciones como clases anonimas dentro del método.
        // Specificacion para buscar por nombre creando el criterio de búsqueda en base del predicado
        Specification<Producto> specNombreProducto = new Specification<Producto>() {
            @Override
            public Predicate toPredicate(Root<Producto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // Si el nombre estápresente
                if (nombre.isPresent()) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombre.get() + "%");
                } else {
                    return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Es decir, que no filtramos nada
                }
            }

        };
        // Specificacion para buscar por precio menor que...
        Specification<Producto> precioMenorQue = new Specification<Producto>() {
            @Override
            public Predicate toPredicate(Root<Producto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // Si existe precio
                if (precio.isPresent()) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("precio"), precio.get());
                } else {
                    return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Es decir, que no filtramos nada
                }
            }
        };

        // Si llega dos parámetros concatemaos los dos predicados de las consultas por ese orden
        Specification<Producto> ambas = specNombreProducto.and(precioMenorQue);
        return this.repositorio.findAll(ambas, pageable);
    }

    public Optional<Producto> findByIdConLotes(Long id) {
        return repositorio.findByIdJoinFetch(id);
    }


}
