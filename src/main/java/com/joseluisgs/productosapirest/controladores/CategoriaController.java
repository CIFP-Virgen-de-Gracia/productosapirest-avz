package com.joseluisgs.productosapirest.controladores;

import com.joseluisgs.productosapirest.error.ApiError;
import com.joseluisgs.productosapirest.error.SearchCategoriaNoResultException;
import com.joseluisgs.productosapirest.modelos.Categoria;
import com.joseluisgs.productosapirest.servicios.CategoriaServicio;
import com.joseluisgs.productosapirest.utils.pagination.PaginationLinksUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
// Indicamos que es un controlador de tipo Rest

@RequestMapping("/api") // Esta va a ser la raiz de donde escuchemos es decir http://localhost/api

@RequiredArgsConstructor
// Si ponemos esta anotación no es necesario el @Autowired, si lo ponemos no pasa nada,
public class CategoriaController {

    private final CategoriaServicio categoriaServicio; // No es necesario el @Autowired por la notacion, pero pon el final
    private final PaginationLinksUtils paginationLinksUtils;

    /**
     * Lista todas las categorias
     *
     * @return 404 si no hay categorías, 200 y lista de de categorías
     */
    @GetMapping("/categorias")
    public ResponseEntity<?> obetenerTodss() {
        List<Categoria> result = categoriaServicio.findAll();
        if (result.isEmpty()) {
            // Con response Status
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay categorías registradas");
        } else {
            return ResponseEntity.ok(result);
        }

    }

    /**
     * Lista todos las categorias acotados por una busqueda de nombre
     * Es exactamente igual al anterior, por eso se puede fusionar
     *
     * @return 404 si no hay categorias, 200 y lista de categorias si hay una o más
     */
    @ApiOperation(value = "Obtiene una lista de categorias basados en un nombre", notes = "Obtiene una lista de categorias")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Categoria.class),
            @ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)
    })

    @GetMapping(value = "/categorias", params = "nombre")
    public ResponseEntity<?> buscarCategoriasPorNombre(
            @RequestParam("nombre") String txt,
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            HttpServletRequest request) {

        Page<Categoria> result = categoriaServicio.findByNombre(txt, pageable);

        if (result.isEmpty()) {
            throw new SearchCategoriaNoResultException(txt);
        } else {

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

            return ResponseEntity.ok().header("link", paginationLinksUtils.createLinkHeader(result, uriBuilder))
                    .body(result);

        }

    }


    /**
     * Obtiene una categoría con in específico
     *
     * @param id id de la categoría
     * @return 404 si no encuentra la categoría, 200 y la categoría si la encuetra
     */
    @GetMapping("/categorias/{id}")
    public Categoria obtenerCategoría(@PathVariable Long id) {
        // Excepciones con ResponseStatus
        return categoriaServicio.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay categorías registradas con id: " + id));
    }

    /**
     * Insertamos una nueva categoría
     *
     * @param nuevo nueva categoria
     * @return 201 y la categoría
     */
    @PostMapping("/categorias")
    public ResponseEntity<?> nuevoCategoria(@RequestBody Categoria nuevo) {
        if (nuevo.getNombre().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre no puede ser vacío");

        else {
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaServicio.save(nuevo));
        }
    }

    /**
     * Editamos una categoría
     *
     * @param editar categoria a editar
     * @param id     id de la categoria a editar
     * @return 200 Ok si la edición tiene éxito, 404 si no se encuentra la categoria
     */
    @PutMapping("/categorias/{id}")
    public Categoria editarCategoria(@RequestBody Categoria editar, @PathVariable Long id) {

        if (editar.getNombre().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre no puede ser vacío");
        else {
            // Se puede hacer con su asignaciones normales sin usar map, mira nuevo
            return categoriaServicio.findById(id).map(p -> {
                p.setNombre(editar.getNombre());
                return categoriaServicio.save(p);
            }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay categorías registradas con id: " + id));
        }

    }

    /**
     * Borra una categoría con un id espcífico
     *
     * @param id id de la categoría
     * @return Código 204 sin contenido
     */
    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<?> borrarCategoria(@PathVariable Long id) {
        // Con manejo de excepciones
        Categoria categoria = categoriaServicio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay categorías registradas con id: " + id));

        // Debemos tener en cueta que categoría es calve externa, y no podemos borrarla si hay productos enlazados
        try {
            categoriaServicio.delete(categoria);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar categoria al existir productos enlzados a ella");
        }
    }


}
