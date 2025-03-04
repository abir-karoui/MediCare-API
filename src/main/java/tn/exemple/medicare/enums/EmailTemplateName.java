package tn.exemple.medicare.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account");
    private  final  String name;

    public String getName() {
        return name;
    }

    EmailTemplateName(String name) {
        this.name = name;
    }
}
