package Whitelist_Je.functions;

public class Alphanumeric {
    public boolean isAlphanumeric(String string) {

        // Interdire les caarctères spéciaux non autorisés par mojang pour les pseudos

        if (string.matches("^[a-zA-Z0-9_-]+$")) {
            return true;
        } else {
            return false;
        }
    }
}
