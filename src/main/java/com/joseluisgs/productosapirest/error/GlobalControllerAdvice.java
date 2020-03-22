package com.joseluisgs.productosapirest.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    // Producto no encotrado
    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ApiError> handleProductoNoEncontrado(ProductoNotFoundException ex) {
        // Aplicamos el nuevo constructor indicado an APIREST
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    // Lista de productos no encontradas
    @ExceptionHandler(ProductosNotFoundException.class)
    public ResponseEntity<ApiError> handleProductosNoEncontrado(ProductosNotFoundException ex) {
        // Aplicamos el nuevo constructor indicado an APIREST
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }


    // Categoría no encotrada
    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ApiError> handleCategoriaNoEncontrado(CategoriaNotFoundException ex) {
        // Aplicamos el nuevo constructor indicado an APIREST
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(ProductoBadRequestException.class)
    public ResponseEntity<ApiError> handleProductoPeticionIncorrecta(ProductoBadRequestException ex) {
        // Aplicamos el nuevo constructor indicado an APIREST
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }


    // Clase para controlar errores inesperados que no estamos teniendo en cuenta
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(status, ex.getMessage());
        return ResponseEntity.status(status).headers(headers).body(apiError);
    }

    @ExceptionHandler(SearchProductoNoResultException.class)
    public ResponseEntity<ApiError> handleBusquedaSinResultado(SearchProductoNoResultException ex) {
        // Aplicamos el nuevo constructor indicado an APIREST
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

}
