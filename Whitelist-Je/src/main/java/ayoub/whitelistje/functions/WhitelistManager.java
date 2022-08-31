package ayoub.whitelistje.functions;

import ayoub.whitelistje.WhitelistJe;

import java.util.ArrayList;
import java.util.List;

public class WhitelistManager {
    private WhitelistJe main;
    public WhitelistManager(WhitelistJe main) {
        this.main = main;
    }

    // Tableau contenant la liste des joueurs autorisés sur la whitelist

    private List playersAllowed = new ArrayList<>();
    public List getPlayersAllowed() {
        return playersAllowed;
    }
}