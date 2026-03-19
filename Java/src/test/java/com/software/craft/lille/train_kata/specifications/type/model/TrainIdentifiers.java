package com.software.craft.lille.train_kata.specifications.type.model;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public record TrainIdentifiers(Collection<TrainIdentifier> elements) {
    public TrainIdentifiers {
        elements = Optional.ofNullable(elements)
                .map(Set::copyOf)
                .orElse(Set.of());
    }

    public boolean isEmpty() {
        return elements().isEmpty();
    }
}
