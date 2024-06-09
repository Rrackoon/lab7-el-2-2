package ru.itmo.common.commands.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The {@code Sendable} class represents an object that can be sent over the network.
 * It encapsulates information about the success status, message, data payload, and optionally user credentials.
 * This class serves as an abstract base class for specific types of sendable objects.
 *
 * @author zevtos
 */
@Getter
@Setter
public abstract class Sendable implements Serializable {
    protected final boolean success;

    protected final String message;

    protected final Object data;

    protected String login;

    protected String password;


    protected Integer userId;


    public Sendable(final boolean success, String message, final Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}