package com.git.polling.model;

import lombok.Getter;

@Getter
public enum Sorting {
    START("stars"),
    FORK("forks"),
    UPDATE("updated");

    private final String keyWord;

    Sorting(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public String toString() {
        return keyWord;
    }
}
