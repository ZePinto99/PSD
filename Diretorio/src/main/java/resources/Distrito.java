package resources;

import io.dropwizard.logback.shaded.checkerframework.checker.nullness.qual.NonNull;

public enum Distrito {
    LISBOA("Lisboa"),
    PORTO("Porto"),
    BRAGA("Braga"),
    SETUBAL( "Setubal"),
    AVEIRO("Aveiro"),
    FARO("Faro"),
    LEIRIA("Leiria"),
    COIMBRA("Coimbra"),
    SANTAREM("Santaréqm"),
    VISEU("Viseu"),
    MADEIRA("Madeira"),
    ACORES("Acores"),
    VIANADOCASTELO("Viana Do Castelo"),
    VILAREAL("Vila Real"),
    CASTELOBRANCO("Castelo Branco"),
    EVORA("Évora"),
    GUARDA("Guarda"),
    BEJA("Beja"),
    BRAGANCA("Bragança"),
    PORTALEGRE("Portalegre");


    private String curr;

    Distrito(String distrito) {
        this.curr = distrito;
    }

    @NonNull
    @Override
    public String toString() {
        return this.curr;
    }

    public static Distrito findDistrict(String i) {
        Distrito[] testEnums = Distrito.values();
        for (Distrito testEnum : testEnums) {
            if (testEnum.curr.equals(i)) {
                return testEnum;
            }
        }
        return null;
    }


}
