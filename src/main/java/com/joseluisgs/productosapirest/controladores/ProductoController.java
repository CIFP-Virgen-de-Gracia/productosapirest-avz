package com.joseluisgs.productosapirest.controladores;


import com.joseluisgs.productosapirest.dto.CreateProductoDTO;
import com.joseluisgs.productosapirest.dto.ProductoDTO;
import com.joseluisgs.productosapirest.dto.coverter.ProductoDTOConverter;
import com.joseluisgs.productosapirest.error.ApiError;
import com.joseluisgs.productosapirest.error.ProductoBadRequestException;
import com.joseluisgs.productosapirest.error.ProductoNotFoundException;
import com.joseluisgs.productosapirest.modelos.Producto;
import com.joseluisgs.productosapirest.servicios.CategoriaServicio;
import com.joseluisgs.productosapirest.servicios.ProductoServicio;
import com.joseluisgs.productosapirest.upload.StorageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
// Indicamos que es un controlador de tipo Rest

@RequestMapping("/api") // Esta va a ser la raiz de donde escuchemos es decir http://localhost/api

@RequiredArgsConstructor
// Si ponemos esta anotación no es necesario el @Autowired, si lo ponemos no pasa nada,
public class ProductoController {


    //@Autowired // No es necesario el @Autowired por la notacion @RequiredArgsConstructor, pero pon el final
    private final CategoriaServicio categoriaServicio;
    private final ProductoServicio productoServicio;
    private final ProductoDTOConverter productoDTOConverter;
    private final StorageService storageService;

    /**
     * Lista todos los productos
     *
     * @return 404 si no hay productos, 200 y lista de productos si hay uno o más
     */

    //@CrossOrigin(origins = "http://localhost:8888") // No es necesario porque ya tenemos las conf globales MyConfig
    // Indicamos sobre que puerto u orignes dejamos que actue (navegador) En nuestro caso no habría problemas
    // Pero es bueno tenerlo en cuenta si tenemos en otro serviror una app en angular o similar
    // Pero es inviable para API consumida por muchos terceros. // Debes probar con un cliente desde ese puerto
    // Mejor hacer un filtro, ver MyConfig.java

    @ApiOperation(value = "Obtiene una lista de productos", notes = "Obtiene una lista de productos")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Producto.class),
            @ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)
    })
    @GetMapping("/productos")
    public ResponseEntity<?> obetenerTodos() {
        List<Producto> result = productoServicio.findAll();
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay productos registrados");
        } else {
            List<ProductoDTO> dtoList = result.stream().map(productoDTOConverter::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        }
    }


    /**
     * Obtiene un producto con un id específico
     *
     * @param id id del producto
     * @return 404 si no encuentra el producto, 200 y el producto si lo encuentra
     */
    @ApiOperation(value = "Obtener un producto por su ID", notes = "Provee un mecanismo para obtener todos los datos de un producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Producto.class),
            @ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)
    })
    @GetMapping("/productos/{id}")
    public Producto obtenerProducto(@ApiParam(value = "ID del producto", required = true, type = "long") @PathVariable Long id) {
        // Excepciones con ResponseStatus
        try {
            return productoServicio.findById(id)
                    .orElseThrow(() -> new ProductoNotFoundException(id));
        } catch (ProductoNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }


    /**
     * Insertamos un nuevo producto
     *
     * @param nuevo nuevo producto a insertar
     * @return 201 y el producto insertado
     */
    @ApiOperation(value = "Crear un nuevo Producto", notes = "Provee la operación para crear un nuevo Producto a partir de un CreateProductoDto y devuelve el objeto creado")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OK", response = Producto.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)
    })
    @PostMapping(value = "/productos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> nuevoProducto(
            @ApiParam(value = "Datos del nuevo producto", type = "CreateProductoDTO.class")
            @RequestPart("nuevo") CreateProductoDTO nuevo,
            @ApiParam(value = "imagen para el nuevo producto", type = "application/octet-stream")
            @RequestPart("file") MultipartFile file) {

        try {

            // Comprobaciones
            if (nuevo.getNombre().isEmpty())
                throw new ProductoBadRequestException("Nombre", "Nombre vacío");
            if (nuevo.getPrecio() < 0)
                throw new ProductoBadRequestException("Precio", "Precio no puede ser negativo");

            // Con esto obligamos que todos producto pertenezca a una categoría si no quitar
            if (!categoriaServicio.findById(nuevo.getCategoriaId()).isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe o es vacía la categoría con ID: " + nuevo.getCategoriaId());


            return ResponseEntity.status(HttpStatus.CREATED).body(productoServicio.nuevoProducto(nuevo, file));

        } catch (ProductoNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

    }


    /**
     * Editamos un producto
     *
     * @param editar producto a editar
     * @param id     id del producto a editar
     * @return 200 Ok si la edición tiene éxito, 404 si no se encuentra el producto
     */
    @ApiOperation(value = "Edita un Producto", notes = "Edita un producto en base a su ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Producto.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)
    })
    @PutMapping("/productos/{id}")
    public Producto editarProducto(@RequestBody Producto editar, @PathVariable Long id) {

        // Comprobamos que los campos no sean vacios antes o el precio negativo
        if (editar.getNombre().isEmpty())
            throw new ProductoBadRequestException("Nombre", "Nombre vacío");
        else if (editar.getPrecio() <= 0)
            throw new ProductoBadRequestException("Precio", "Precio no puede ser negativo");
        else {
            // Se puede hacer con su asignaciones normales sin usar map, mira nuevo
            return productoServicio.findById(id).map(p -> {
                p.setNombre(editar.getNombre());
                p.setPrecio(editar.getPrecio());
                return productoServicio.save(p);
            }).orElseThrow(() -> new ProductoNotFoundException(id));
        }

    }

    /**
     * Borra un producto con un id espcífico
     *
     * @param id id del producto a borrar
     * @return Código 204 sin contenido
     */
    @ApiOperation(value = "Elimina un Producto", notes = "Elimina un producto en base a su ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "OK", response = Producto.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)
    })
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> borrarProducto(@PathVariable Long id) {

        // Con manejo de excepciones
        Producto producto = productoServicio.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        productoServicio.delete(producto);
        return ResponseEntity.noContent().build();
    }
}
