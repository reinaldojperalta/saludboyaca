package sena.adso.modules.auth.model;

import sena.adso.core.model.Identifiable;

/**
 * Clase base abstracta para todas las personas del sistema.
 * 
 * Define los atributos comunes de identificación personal y el patrón
 * Builder recursivo que permiten a las subclases extenderlo type-safe.
 * 
 * @param <T> Tipo del Builder concreto (patrón self-referential generics)
 */
public abstract class Persona implements Identifiable<Integer> {

    protected Integer id;
    protected final String nombres;
    protected final String apellidos;
    protected final String documento;

    /**
     * Constructor protegido que recibe el Builder.
     * Las subclases deben llamar a super(builder) en su constructor.
     */
    protected <T extends Builder<T>> Persona(Builder<T> builder) {
        this.id = builder.id;
        this.nombres = builder.nombres;
        this.apellidos = builder.apellidos;
        this.documento = builder.documento;
    }

    // ============================================================
    // GETTERS (sin setters — objetos inmutables después de construidos)
    // ============================================================

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDocumento() {
        return documento;
    }

    /**
     * Nombre completo formateado.
     */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    // ============================================================
    // BUILDER BASE (Recursive Type Bounding)
    // ============================================================

    /**
     * Builder base con tipo recursivo.
     * 
     * El parámetro genérico <T> permite que el Builder de Usuario
     * retorne Usuario.Builder en sus métodos, no Persona.Builder.
     * Esto permite encadenar: new
     * Usuario.Builder().nombres("X").username("Y").build()
     */
    public abstract static class Builder<T extends Builder<T>> {

        protected Integer id;
        protected String nombres;
        protected String apellidos;
        protected String documento;

        /**
         * Método mágico: retorna "this" casteado al tipo concreto T.
         * Cada subclase implementa esto retornando this.
         */
        protected abstract T self();

        public T id(Integer id) {
            this.id = id;
            return self();
        }

        public T nombres(String nombres) {
            this.nombres = nombres;
            return self();
        }

        public T apellidos(String apellidos) {
            this.apellidos = apellidos;
            return self();
        }

        public T documento(String documento) {
            this.documento = documento;
            return self();
        }

        /**
         * Construye la instancia concreta.
         * Cada subclase implementa esto retornando su tipo específico.
         */
        public abstract Persona build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", documento='" + documento + '\'' +
                '}';
    }
}