package com.ap.stardew.records;

public record WalkProposal(
        boolean isAllowed,
        String message,
        double energyCost,
        int x,
        int y
) {
}
