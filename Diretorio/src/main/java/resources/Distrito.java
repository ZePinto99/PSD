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
            //System.out.println(testEnum.toString());
            if (testEnum.curr.equals(i)) {
                return testEnum;
            }
        }
        return null;
    }

    public static int findDistrictPosition(String i) {
        Distrito[] testEnums = Distrito.values();
        int count =0;
        for (Distrito testEnum : testEnums) {
            //System.out.println(testEnum.toString());
            if (testEnum.curr.equals(i)) {
                return count;
            }
            count++;
        }
        return count;
    }

    public static String findDistrictOfIndex(int i) {
        Distrito[] testEnums = Distrito.values();
        int count =0;
        for (Distrito testEnum : testEnums) {

            if (count == i) {
                return testEnum.curr;
            }
            count++;
        }
        return null;
    }


}
