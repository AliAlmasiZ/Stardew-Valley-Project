package views.inGame;

import java.io.Serializable;
import java.util.Arrays;

public class CharacterTexture implements Serializable {
    int width, height;
    public StyledCharacter[][] data;

    public CharacterTexture(int width, int height) {
        this.data = new StyledCharacter[height][width];
        this.width = width;
        this.height = height;

    }

    public void updateSize(int width, int height) {
        this.data = new StyledCharacter[height][width];
        this.width = width;
        this.height = height;
        this.reset();
    }

    public void clear() {
        this.reset();
    }

    public void reset() {
        for (StyledCharacter[] styledCharacters : data) {
            Arrays.fill(styledCharacters, null);
        }
    }

    public void setCharacter(int x, int y, char character, Color color) {
        this.data[y][x] = new StyledCharacter(character, color);
    }
}
