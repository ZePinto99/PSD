package Representation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class NumberOfInfected {

    public final int id;
    public final String distrito;
    public final int numero;

    @JsonCreator
    public NumberOfInfected(@JsonProperty("Status") int id, @JsonProperty("Distrito") String Distrito, @JsonProperty("Número de infectados") int numero) {
        this.id = id;
        this.distrito = Distrito;
        this.numero = numero;
    }

    @JsonProperty("Status")
    public int getId() {
        return id;
    }

    @JsonProperty("Distrito")
    public String getDistrito() {
        return distrito;
    }

    @JsonProperty("Número de infectados")
    public int getNumero() {
        return this.numero;
    }
}
