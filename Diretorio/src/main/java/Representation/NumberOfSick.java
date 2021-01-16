package Representation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NumberOfSick {
    public final int id;
    public final int numero;

    @JsonCreator
    public NumberOfSick(@JsonProperty("Status") int id,
                        @JsonProperty("Número médio de utilizadores que se cruzaram com utilizadores declarados doentes") int numero) {
        this.id = id;
        this.numero = numero;
    }

    @JsonProperty("Status")
    public int getId() {
        return id;
    }


    @JsonProperty("Número médio de utilizadores que se cruzaram com utilizadores declarados doentes")
    public int getNumero() {
        return this.numero;
    }
}
