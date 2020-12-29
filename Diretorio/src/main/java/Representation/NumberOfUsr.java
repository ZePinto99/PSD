package Representation;

import com.fasterxml.jackson.annotation.*;

public class NumberOfUsr {
    public final int id;
    public final String distrito;
    public final int numero;

    @JsonCreator
    public NumberOfUsr(@JsonProperty("Status") int id, @JsonProperty("Distrito") String Distrito, @JsonProperty("Número de utilizadores") int numero) {
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

    @JsonProperty("Número de utilizadores")
    public int getNumero() {
        return this.numero;
    }
}

