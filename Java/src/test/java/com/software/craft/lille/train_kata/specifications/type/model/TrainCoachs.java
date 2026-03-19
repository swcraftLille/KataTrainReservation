package com.software.craft.lille.train_kata.specifications.type.model;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record TrainCoachs(Collection<TrainCoach> elements) {
    public TrainCoachs {
        elements = Optional.ofNullable(elements)
                .map(Set::copyOf)
                .orElseGet(Set::of);
    }

    public int size() {
        return elements().size();
    }

    public Collection<String> seats() {
        return elements()
                .stream()
                .map(TrainCoach::toSeats)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
