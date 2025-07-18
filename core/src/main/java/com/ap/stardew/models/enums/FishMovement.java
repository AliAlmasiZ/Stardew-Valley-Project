package com.ap.stardew.models.enums;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

public enum FishMovement {
    MIXED {
        @Override
        public int moveFish(Actor actor, float min, float max, int lastMove) {
            float movement = (max - min) / 10;
            int random = (int) (Math.random() * 3);

            switch (random) {
                case 0:
                    movement = -movement;
                    break;
                case 1:
                    movement = 0;
                    break;
            }

            actor.setY(MathUtils.clamp(actor.getY() + movement, min, max));
            return 0;
        }
    },
    SMOOTH {
        @Override
        public int moveFish(Actor actor, float min, float max, int lastMove) {
            float movement = (max - min) / 10 * lastMove;
            int random = (int) (Math.random() * 4);
            switch (random) {
                case 0:
                    movement = -movement;
                    actor.setY(MathUtils.clamp(actor.getY() + movement, min, max));
                    return -lastMove;
                case 1:
                    movement = 0;
                    actor.setY(MathUtils.clamp(actor.getY() + movement, min, max));
                    return lastMove;
            }

            actor.setY(MathUtils.clamp(actor.getY() + movement, min, max));
            return lastMove;

        }
    },
    SINKER {
        @Override
        public int moveFish(Actor actor, float min, float max, int lastMove) {
            float movement = (max - min) / 10;
            int random = (int) (Math.random() * 3);

            switch (random) {
                case 0:
                    movement = -movement;
                    if (lastMove == 0)
                        actor.setY(MathUtils.clamp(actor.getY() + movement * 2.5f, min, max));
                    else actor.setY(MathUtils.clamp(actor.getY() + movement, min, max));
                    return -1;
                case 1:
                    return 0;
            }

            actor.setY(MathUtils.clamp(actor.getY() + movement, min, max));
            return 1;

        }
    },
    FLOATER {
        @Override
        public int moveFish(Actor actor, float min, float max, int lastMove) {
            float movement = (max - min) / 10;
            movement = -movement;
            int random = (int) (Math.random() * 3);

            switch (random) {
                case 0:
                    movement = -movement;
                    if (lastMove == 0)
                        actor.setY(MathUtils.clamp(actor.getY() + movement * 2.5f, min, max));
                    else actor.setY(MathUtils.clamp(actor.getY() + movement, min, max));
                    return 1;
                case 1:
                    movement = 0;
                    return 0;
            }

            actor.setY(MathUtils.clamp(actor.getY() + movement, min, max));
            return -1;
        }
    },
    DART {
        @Override
        public int moveFish(Actor actor, float min, float max, int lastMove) {

            float movement = (max - min) / 6;
            int random = (int) (Math.random() * 3);

            switch (random) {
                case 0:
                    movement = -movement;
                    break;
                case 1:
                    movement = 0;
                    break;
            }

            actor.setY(MathUtils.clamp(actor.getY() + movement, min, max));
            return 0;

        }
    };

    public abstract int moveFish(Actor actor, float min, float max, int lastMove);

    public static FishMovement getRandomFishMovement() {
        int random = (int) (Math.random() * 5);
        switch (random) {
            case 0:
                return FishMovement.MIXED;
            case 1:
                return FishMovement.SINKER;
            case 2:
                return FishMovement.FLOATER;
            case 3:
                return FishMovement.DART;
            case 4:
                return FishMovement.SMOOTH;
        }
        return null;
    }
}
