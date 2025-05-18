package models.Commands;

public enum     ProfileMenuCommands implements Commands{
    USER_INFO         ("^user\\s+info$"),
    MENU_ENTER        ("^menu\\s+enter\\s+(?<menuName>.+)"),
    CHANGE_EMAIL      ("^change\\s+email\\s+-e\\s+(?<email>.+)$"),
    CHANGE_PASSWORD   ("^change\\s+password\\s+-p\\s+(?<newPassword>.+?)\\s+-o\\s+(?<oldPassword>.+)$"),
    CHANGE_USERNAME   ("^change\\s+username\\s+-u\\s+(?<username>.+)$"),
    CHANGE_NICKNAME   ("^change\\s+nickname\\s+-u\\s+(?<nickname>.+)$"),
    SHOW_CURRENT_MENU ("^show\\s+current\\s+menu$"),
    ;


    private final String pattern;

    ProfileMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }
}
