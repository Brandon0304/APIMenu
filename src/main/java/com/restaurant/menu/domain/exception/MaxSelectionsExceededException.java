package com.restaurant.menu.domain.exception;

public class MaxSelectionsExceededException extends DomainException {

    public MaxSelectionsExceededException(int maxSelections, int optionCount) {
        super("MAX_SELECTIONS_EXCEEDED",
            "Maximum selections (" + maxSelections + ") exceeded. Only " + optionCount + " options available",
            422);
    }
}
