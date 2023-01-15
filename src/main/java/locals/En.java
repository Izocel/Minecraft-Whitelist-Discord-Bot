package locals;

public enum En {
    RAINY("rainy"),
    SUNNY("sunny"),
    NAME("Name %s")

    ;

    final String trans;

    En(String trans) {
        this.trans = trans;
    }

}