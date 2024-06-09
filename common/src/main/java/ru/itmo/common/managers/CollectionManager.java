package ru.itmo.common.managers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class CollectionManager<T extends Comparable<T>> {
    private final LocalDateTime createdAt = LocalDateTime.now();
    private List<T> collection;

    public CollectionManager() {
        this.collection = Collections.synchronizedList(new LinkedList<>());
    }

    public List<T> getCollection() {
        return collection;
    }

    public void setCollection(List<T> collection) {
        this.collection = Collections.synchronizedList(collection);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int push(T element) throws Exception {
        int id=saveToDatabase(element);
        if ( id >0 ) {
            collection.add(element);
        }
        return id;
    }

    public void remove(long id, String login) throws Exception {
        T element = get(id);
        if (element != null && checkOwnerShip(id, login) && removeFromDatabase(id)) {
            collection.remove(element);
        } else {
            throw new Exception("Error! Element with ID " + id + " not found");
        }
    }

    public void update(long id, String login, T newElement) throws Exception {
        T existingElement = get(id);
        if (existingElement != null && checkOwnerShip(id, login) && updateInDatabase(id, newElement)) {
            updateElement(existingElement, newElement);
        } else {
            throw new Exception("Error! Element with ID " + id + " not found");
        }
    }

    public void removeById(long id, String login) throws Exception {
        T element = get(id);
        if (element != null && checkOwnerShip(id, login) && removeFromDatabase(id)) {
            collection.remove(element);
        } else {
            throw new Exception("Error! Element with ID " + id + " not found");
        }
    }


    public void clear(String login) throws Exception {
        if (login == null) {
            throw new Exception("Error! You are not logged in");
        }
        if (clearDatabase()) {
            collection.clear();
        }
    }

    public T get(long id) {
        return collection.stream()
                .filter(element -> getElementId(element) == id)
                .findFirst()
                .orElse(null);
    }

    public String description() {
        return String.format("Type: %s\nInitialization date: %s\nNumber of elements: %d",
                collection.getClass().getName(), createdAt, collection.size());
    }

    public T min() {
        return collection.stream()
                .min(T::compareTo)
                .orElse(null);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (T element : collection) {
            builder.append(element);
            builder.append("\n");
        }
        return builder.toString();
    }

    // Вспомогательные методы для работы с ID элементов и обновления элементов
    protected abstract long getElementId(T element);
    protected abstract int saveToDatabase(T element) throws Exception;
    protected abstract void updateElement(T existingElement, T newElement);

    // Абстрактные методы для взаимодействия с базой данных

    protected abstract boolean removeFromDatabase(long id) throws Exception;

    protected abstract boolean updateInDatabase(long id, T newElement) throws Exception;

    protected abstract boolean clearDatabase() throws Exception;

    protected abstract boolean checkOwnerShip(long id, String login);
}
