package locals;

public enum Lang {
    FR("FR"),
    EN("EN"),
    ES("ES"),
    FR_EN("FR_EN"),
    FR_ES("FR_ES"),
    EN_FR("EN_FR"),
    ES_FR("ES_FR"),
    ES_EN("ES_EN");

    public final String value;

    Lang(String lang) {
        this.value = lang;
    }
}
