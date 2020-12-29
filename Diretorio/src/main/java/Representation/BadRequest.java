package Representation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BadRequest {

    public final int id;
    public final String distrito;
    public final int numero;

    @JsonCreator
    public BadRequest(@JsonProperty("Status") int id, @JsonProperty("") String Distrito, @JsonProperty("Número de infectados") int numero) {
        this.id = id;
        this.distrito = Distrito;
        this.numero = numero;
    }

    @JsonProperty("Status")
    public int getId() {
        return id;
    }

    @JsonProperty("Distrito")
    public String getContent() {
        return distrito;
    }

    @JsonProperty("Número de infectados")
    public int getNumero() {
        return this.numero;
    }

}
