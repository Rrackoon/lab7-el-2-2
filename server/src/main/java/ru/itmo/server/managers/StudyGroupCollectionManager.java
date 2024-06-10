package ru.itmo.server.managers;

import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.models.StudyGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudyGroupCollectionManager extends CollectionManager<StudyGroup> {
    private final DatabaseConnector databaseConnector;

    public StudyGroupCollectionManager(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
        loadFromDatabase();
    }

    @Override
    protected long getElementId(StudyGroup element) {
        return element.getId();
    }

    @Override
    public void clear(String login) throws Exception {
        List<StudyGroup> toRemove = getCollection().stream()
                .filter(group -> group.getLogin().equals(login))
                .collect(Collectors.toList());
        for (StudyGroup group : toRemove) {
            remove(group.getId(), login);
        }
    }

    @Override
    protected void updateElement(StudyGroup existingElement, StudyGroup newElement) {
        existingElement.update(newElement);
    }

    protected int saveToDatabase(StudyGroup element) throws Exception {
        try {
            return databaseConnector.saveSingle(element);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }



    @Override
    protected boolean removeFromDatabase(long id) throws Exception {
        try {
            databaseConnector.remove(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean updateInDatabase(long id, StudyGroup newElement) throws Exception {
        try {
            databaseConnector.update(id, newElement);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean clearDatabase() throws Exception {
        try {
            databaseConnector.clear();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean checkOwnerShip(long id, String login) {
        return databaseConnector.checkOwnership(id, login);
    }

    @Override
    public void setId(StudyGroup element, int id) {
        element.setId(id);
    }
    public void saveToDatabase() {
        List<StudyGroup> collection = getCollection();
        try {
            databaseConnector.save(collection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadFromDatabase() {
        try {
            List<StudyGroup> studyGroups = databaseConnector.getStudyGroup();
            if ((studyGroups != null) && (studyGroups.size() > 0)) {
                setCollection(studyGroups);
            } else {
                setCollection(new ArrayList<StudyGroup>());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

}
