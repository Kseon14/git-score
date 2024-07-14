package com.git.polling.model;

import lombok.Getter;

@Getter
public enum Ordering {
    DESC("desc"),
    ASC("asc");

    private final String keyWord;

    Ordering(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public String toString() {
        return keyWord;
    }
}
