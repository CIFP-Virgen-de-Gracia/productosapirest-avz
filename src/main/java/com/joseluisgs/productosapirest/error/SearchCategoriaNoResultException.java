package com.joseluisgs.productosapirest.error;

public class SearchCategoriaNoResultException extends RuntimeException {
    private static final long serialVersionUID = -889312292404205516L;


    public SearchCategoriaNoResultException() {
        super("La búsqueda de categorias no produjo resultados");
    }

    public SearchCategoriaNoResultException(String txt) {
        super(String.format("El término de búsqueda %s no produjo resultados", txt));
    }


}