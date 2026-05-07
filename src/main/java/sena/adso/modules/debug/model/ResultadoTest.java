package sena.adso.modules.debug.model;

/**
 * DTO para resultados de tests funcionales en la debug suite.
 */
public class ResultadoTest {
    
    private String nombre;
    private boolean exito;
    private String queryEjecutada;
    private String resultadoObtenido;
    private String error;
    private long tiempoMs;

    public ResultadoTest() {}

    public ResultadoTest(String nombre) {
        this.nombre = nombre;
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }
    
    public String getQueryEjecutada() { return queryEjecutada; }
    public void setQueryEjecutada(String queryEjecutada) { this.queryEjecutada = queryEjecutada; }
    
    public String getResultadoObtenido() { return resultadoObtenido; }
    public void setResultadoObtenido(String resultadoObtenido) { this.resultadoObtenido = resultadoObtenido; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public long getTiempoMs() { return tiempoMs; }
    public void setTiempoMs(long tiempoMs) { this.tiempoMs = tiempoMs; }

    /**
     * Helper para marcar éxito rápidamente.
     */
    public void marcarExito(String resultado, String query) {
        this.exito = true;
        this.resultadoObtenido = resultado;
        this.queryEjecutada = query;
    }

    /**
     * Helper para marcar fallo rápidamente.
     */
    public void marcarFallo(String error, String query) {
        this.exito = false;
        this.error = error;
        this.queryEjecutada = query;
    }
}