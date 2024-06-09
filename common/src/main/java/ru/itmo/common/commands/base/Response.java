package ru.itmo.common.commands.base;

import lombok.ToString;
import ru.itmo.common.utility.ByteActions;

import java.io.Serializable;
import java.nio.ByteBuffer;

@ToString
public class Response extends Sendable {


    public Response(boolean success, String message, Object data) {
        super(success, message, data);
    }

    public Response(boolean success, String message) {
        super(success, message, null);
    }

    public Response(boolean success) {
        super(success, null, null);
    }

    @Override
    public String toString() {
        return ((message != null) ? message : "") + (data != null ? ((message != null) ? '\n' + data.toString() : data.toString()) : "");
    }
}
