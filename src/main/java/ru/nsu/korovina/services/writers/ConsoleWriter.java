package ru.nsu.korovina.services.writers;

import ru.nsu.korovina.models.UserAndKey;

import java.util.Map;

public class ConsoleWriter implements Writer {

    @Override
    public void write(UserAndKey attributes) {
        printMap("The number of edits made by each user", attributes.getUsersCount());
        printMap("The number of unique key names with the number of node tags tagged with them",
                attributes.getKeysCount());
    }

    private void printMap(String title, Map<String, Integer> map) {
        System.out.println(title);
        map.entrySet().stream()
                .sorted((lhs, rhs) -> -(lhs.getValue() - rhs.getValue()))
                .forEach(it -> System.out.printf("%s: %d\n", it.getKey(), it.getValue()));
    }

}
