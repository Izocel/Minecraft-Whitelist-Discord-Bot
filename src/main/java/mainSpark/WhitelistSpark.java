package mainSpark;

import static spark.Spark.*;

public class WhitelistSpark {
    public static void main(String[] args) {
        setPaths();
    }

    public static void setPaths() {
        get("/hello", (req, res) -> "Hello World");
    }
}