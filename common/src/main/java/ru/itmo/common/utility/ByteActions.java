package ru.itmo.common.utility;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteActions {

    // Метод для объединения строк в один ByteBuffer
    public static ByteBuffer joinStrings(String[] response) {
        // Вычисление общей длины всех строк
        int totalLength = 0;
        for (String str : response) {
            if (!str.isEmpty()) {
                totalLength += str.getBytes(StandardCharsets.UTF_8).length + 1; // +1 для символа новой строки
            }
        }

        // Создание буфера для хранения объединённых строк
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);

        // Сборка всех строк в единый буфер
        StringBuilder combinedResponse = new StringBuilder();
        for (String str : response) {
            if (!str.isEmpty()) {
                buffer.put(str.getBytes(StandardCharsets.UTF_8));
                buffer.put((byte) '\n'); // Добавление символа новой строки
                combinedResponse.append(str).append("\n");
            }
        }

        // Установка позиции буфера в начало для последующего чтения
        buffer.flip();

        // Вывод объединённых строк на экран для отладки
        System.out.println("Ответ: " + combinedResponse.toString());

        return buffer;
    }

    // Метод для разделения ByteBuffer на несколько буферов заданного размера
    public static ByteBuffer[] split(ByteBuffer src, int unitSize) {
        int limit = src.limit();
        if (unitSize >= limit) {
            return new ByteBuffer[]{src};
        }

        // Рассчитываем количество получаемых буферов
        int numBuffers = (int) Math.ceil((double) limit / unitSize);
        ByteBuffer[] buffers = new ByteBuffer[numBuffers];
        int srcIndex = 0;

        // Разделяем исходный буфер на буферы с заданным размером
        for (int i = 0; i < numBuffers; i++) {
            int bufferSize = unitSize;
            if (i == numBuffers - 1) {
                bufferSize = limit % unitSize;
            }

            byte[] dest = new byte[bufferSize];
            src.get(dest, 0, bufferSize);
            buffers[i] = ByteBuffer.wrap(dest);
        }

        return buffers;
    }
}
