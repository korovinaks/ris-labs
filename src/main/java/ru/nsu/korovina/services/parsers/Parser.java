package ru.nsu.korovina.services.parsers;

import ru.nsu.korovina.models.UserAndKey;

public interface Parser {
    UserAndKey parse(String resources);

}
