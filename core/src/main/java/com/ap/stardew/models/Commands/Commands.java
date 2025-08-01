package com.ap.stardew.models.Commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Commands {
    String getPattern();

    default Matcher getMatcher(String input) {
        Matcher matcher = Pattern.compile(getPattern()).matcher(input);

        if (matcher.matches()) return matcher;
        return null;
    }

}
