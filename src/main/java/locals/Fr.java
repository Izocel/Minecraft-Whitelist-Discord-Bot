package locals;

public enum Fr {
    RAINY("pluvieux"),
    SUNNY("ensoleillé"),
    NAME("Nom: %s")

    ;

    final String trans;

    Fr(String trans) {
        this.trans = trans;
    }
}