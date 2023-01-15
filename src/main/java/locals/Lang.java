package locals;

public enum Lang {
    FR("FR"),
    EN("EN"),
    EN_FR("EN_FR"),
    FR_EN("FR_EN"),

    ;

    public final String value;

    Lang(String lang) {
        this.value = lang;
    }
}
