package com.ap.stardew.models.enums;

import com.ap.stardew.models.entities.components.Growable;

public enum FertilizerFunction {
    Deluxe_Retaining_Soil("Deluxe Retaining Soil"){
        @Override
        public void fertilize(Growable growable) {
            growable.addWateredFertilizedDays((int) (((Math.random() * 0.5) + 0.5) * 14));
        }
    },
    Basic_Retaining_Soil("Basic Retaining Soil"){
        @Override
        protected void fertilize(Growable growable) {
            growable.addWateredFertilizedDays((int) (((Math.random() * 0.5) + 0.5) * 4));
        }
    },
    Quality_Retaining_Soil("Quality Retaining Soil"){
        @Override
        protected void fertilize(Growable growable) {
            growable.addWateredFertilizedDays((int) (((Math.random() * 0.5) + 0.5) * 8));
        }
    },
    Speed_Gro("Speed-Gro") {
        @Override
        protected void fertilize(Growable growable) {
            growable.setDaysPastFromPlant(growable.getDaysPastFromPlant() + 2);
        }
    }

    ;

    abstract protected void fertilize(Growable growable);
    private final String name;

    FertilizerFunction(String name) {
        this.name = name;
    }

    public static void  fertilize(String fertilizerName, Growable growable) {
        for (FertilizerFunction fertilizerFunction : FertilizerFunction.values()) {
            if (fertilizerFunction.name.equalsIgnoreCase(fertilizerName)) {
                fertilizerFunction.fertilize(growable);
                return;
            }
        }
        System.out.println("RIDI: there was no fertilizer with this name");
    }
}
