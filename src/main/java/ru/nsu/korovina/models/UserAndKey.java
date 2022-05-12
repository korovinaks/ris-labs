package ru.nsu.korovina.models;

import java.util.Map;

public class UserAndKey {
    private final Map<String, Integer> usersCount;
    private final Map<String, Integer> keysCount;

    public UserAndKey(Map<String, Integer> userEditedCount, Map<String, Integer> keyEditedCount) {
        this.usersCount = userEditedCount;
        this.keysCount = keyEditedCount;
    }

    public Map<String, Integer> getUsersCount() {
        return usersCount;
    }

    public Map<String, Integer> getKeysCount() {
        return keysCount;
    }

}
