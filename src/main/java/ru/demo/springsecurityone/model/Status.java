package ru.demo.springsecurityone.model;

/**
 * Статус активности аккаунта.
 * Используем в User SS при создании из User который у нас в БД
 * */
public enum Status {
    ACTIVE, BANNED
}