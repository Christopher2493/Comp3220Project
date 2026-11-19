module groupII {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens group2 to javafx.fxml;
    requires java.sql;
    exports group2;
    
}
