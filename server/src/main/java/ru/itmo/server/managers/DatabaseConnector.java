package ru.itmo.server.managers;

import ru.itmo.common.interfaces.Accessible;
import ru.itmo.common.models.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseConnector implements Accessible {
    // Константы для SQL-запросов
    private static final String GET_ALL_STUDY_GROUPS_QUERY = "SELECT * FROM STUDYGROUP;";
    private static final String CLEAR_STUDY_GROUPS_QUERY = "DELETE FROM STUDYGROUP;";
    private static final String INSERT_OR_UPDATE_STUDY_GROUP_QUERY =
            "INSERT INTO STUDYGROUP(id, name, x, y, creationDate, studentsCount, expelledStudents, shouldBeExpelled, formOfEducation, nameP, passportID, color, xP, yP, nameloc, login) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, x = EXCLUDED.x, y = EXCLUDED.y, studentsCount = EXCLUDED.studentsCount, expelledStudents = EXCLUDED.expelledStudents, " +
                    "shouldBeExpelled = EXCLUDED.shouldBeExpelled, formOfEducation = EXCLUDED.formOfEducation, nameP = EXCLUDED.nameP, passportID = EXCLUDED.passportID, color = EXCLUDED.color, " +
                    "xP = EXCLUDED.xP, yP = EXCLUDED.yP, nameloc = EXCLUDED.nameloc, login = EXCLUDED.login;";
    private static final String SIGN_IN_QUERY = "SELECT * FROM USERS WHERE name = ?";
    private static final String REGISTER_QUERY = "INSERT INTO USERS(name, password) VALUES (?, ?);";
    private static final String SAVE_SINGLE_QUERY =
            "INSERT INTO STUDYGROUP(name, x, y, creationDate, studentsCount, expelledStudents, shouldBeExpelled, formOfEducation, nameP, passportID, color, xP, yP, nameloc, login) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "RETURNING id;";
    private static final String REMOVE_QUERY = "DELETE FROM STUDYGROUP WHERE id = ?";
    private static final String UPDATE_QUERY =
            "UPDATE STUDYGROUP SET name = ?, x = ?, y = ?, creationDate = ?, studentsCount = ?, expelledStudents = ?, " +
                    "shouldBeExpelled = ?, formOfEducation = ?, nameP = ?, passportID = ?, color = ?, xP = ?, yP = ?, nameloc = ?, login = ? WHERE id = ?";
    private static final String CHECK_OWNERSHIP_QUERY = "SELECT 1 FROM STUDYGROUP WHERE id = ? AND login = ?;";
    private static Connection connection;

    public DatabaseConnector(String host, String name, String password) throws SQLException, SQLTimeoutException {
        connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":5432/studs", name, password);
    }

    public DatabaseConnector(String name, String password) throws SQLException, SQLTimeoutException {
        this("pg", name, password);
    }

    public DatabaseConnector() throws SQLException, SQLTimeoutException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs","postgres","postgres");
    }

    public static void clear() throws SQLException {
        Statement state = connection.createStatement();
        state.executeUpdate(CLEAR_STUDY_GROUPS_QUERY);
    }

    public static int saveSingle(StudyGroup studyGroup) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SAVE_SINGLE_QUERY);

        ps.setString(1, studyGroup.getName());
        ps.setInt(2, studyGroup.getCoordinates().getX());
        ps.setLong(3, studyGroup.getCoordinates().getY());
        ps.setTimestamp(4, Timestamp.valueOf(studyGroup.getCreationDate()));
        ps.setInt(5, studyGroup.getStudentsCount());
        ps.setInt(6, studyGroup.getExpelledStudents());
        ps.setInt(7, studyGroup.getShouldBeExpelled());
        ps.setObject(8, (studyGroup.getFormOfEducation() != null ? studyGroup.getFormOfEducation() : "NULL"), Types.OTHER);

        Person groupAdmin = studyGroup.getGroupAdmin();
        ps.setString(9, (groupAdmin.getName() != null ? groupAdmin.getName() : "NULL"));
        ps.setObject(10, (groupAdmin.getPassportID() != null ? groupAdmin.getPassportID() : "NULL"), Types.OTHER);
        ps.setObject(11, (groupAdmin.getHairColor() != null ? groupAdmin.getHairColor() : "NULL"), Types.OTHER);

        ps.setInt(12, groupAdmin.getLocation().getX());
        ps.setLong(13, groupAdmin.getLocation().getY());
        ps.setString(14, groupAdmin.getLocation().getName());
        ps.setString(15, studyGroup.getLogin());

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1); // Возвращаем сгенерированный id
        } else {
            throw new SQLException("ID генерироваться не удалось");
        }
    }

    public void remove(long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_QUERY)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public void update(long id, StudyGroup newElement) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {
            fillPreparedStatementForStudyGroup(preparedStatement, newElement);
            preparedStatement.setLong(16, id);
            preparedStatement.executeUpdate();
        }
    }

    private void fillPreparedStatementForStudyGroup(PreparedStatement preparedStatement, StudyGroup studyGroup) throws SQLException {
        preparedStatement.setString(1, studyGroup.getName());
        preparedStatement.setInt(2, studyGroup.getCoordinates().getX());
        preparedStatement.setLong(3, studyGroup.getCoordinates().getY());
        preparedStatement.setTimestamp(4, Timestamp.valueOf(studyGroup.getCreationDate()));
        preparedStatement.setInt(5, studyGroup.getStudentsCount());
        preparedStatement.setInt(6, studyGroup.getExpelledStudents());
        preparedStatement.setInt(7, studyGroup.getShouldBeExpelled());
        preparedStatement.setString(8, studyGroup.getFormOfEducation().toString());
        preparedStatement.setString(9, studyGroup.getGroupAdmin().getName());
        preparedStatement.setString(10, studyGroup.getGroupAdmin().getPassportID());
        preparedStatement.setString(11, studyGroup.getGroupAdmin().getHairColor().toString());
        preparedStatement.setInt(12, studyGroup.getGroupAdmin().getLocation().getX());
        preparedStatement.setInt(13, studyGroup.getGroupAdmin().getLocation().getY());
        preparedStatement.setString(14, studyGroup.getGroupAdmin().getLocation().getName());
        preparedStatement.setString(15, studyGroup.getLogin());
    }

    public void save(List<StudyGroup> studyGroups) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_OR_UPDATE_STUDY_GROUP_QUERY)) {
            for (StudyGroup studyGroup : studyGroups) {
                fillPreparedStatementForStudyGroup(preparedStatement, studyGroup);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    public LinkedList<StudyGroup> getStudyGroup() throws SQLException {
        Statement state = connection.createStatement();
        ResultSet rs = state.executeQuery(GET_ALL_STUDY_GROUPS_QUERY);
        LinkedList<StudyGroup> studyGroups = new LinkedList<>();
        while (rs.next()) {
            StudyGroup studyGroup = StudyGroup.builder()
                    .id(rs.getLong(1))
                    .name(rs.getString(2))
                    .coordinates(new Coordinates(rs.getInt(3), rs.getLong(4)))
                    .creationDate(rs.getTimestamp(5).toLocalDateTime())
                    .studentsCount(rs.getInt(6))
                    .expelledStudents(rs.getInt(7))
                    .shouldBeExpelled(rs.getInt(8))
                    .formOfEducation(FormOfEducation.valueOf(rs.getString(9)))
                    .groupAdmin(Person.builder()
                            .name(rs.getString(10))
                            .passportID(rs.getString(11))
                            .hairColor(Color.valueOf(rs.getString(12)))
                            .location(new Location(rs.getInt(13), rs.getInt(14), rs.getString(15)))
                            .build())
                    .login(rs.getString(16))
                    .build();
            studyGroups.addLast(studyGroup);
        }
        return studyGroups;
    }

    public String signIn(String login, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement(SIGN_IN_QUERY);
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            int cnt = 0;
            boolean ok = false;
            while (rs.next()) {
                ++cnt;
                String pword = rs.getString("password");
                if (hashSHA1(password).equals(pword)) {
                    ok = true;
                }
            }
            if (cnt < 1) {
                return "Пользователь с таким логином не найден! Попробуйте снова";
            } else if (!ok) {
                return "Неправильный пароль! Попробуйте снова";
            }
            return "Вы успешно зашли в систему";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Ошибка! Попробуйте связаться с администратором или зайти в систему позже";
        }
    }

    public boolean isSignIn(String login, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement(SIGN_IN_QUERY);
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            int cnt = 0;
            boolean ok = false;
            while (rs.next()) {
                ++cnt;
                String pword = rs.getString("password");
                if (hashSHA1(password).equals(pword)) {
                    ok = true;
                }
            }
            if (cnt < 1) {
                return false;
            } else if (!ok) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String register(String login, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement(SIGN_IN_QUERY);
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            int cnt = 0;
            while (rs.next()) {
                ++cnt;
            }
            if (cnt > 0) {
                return "Пользователь с таким логином уже существует! Придумайте новый";
            }
            String codedpassword = hashSHA1(password);
            PreparedStatement ps = connection.prepareStatement(REGISTER_QUERY);
            ps.setString(1, login);
            ps.setString(2, codedpassword);
            ps.executeUpdate();
            return "Вы успешно зарегистрированы";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Ошибка! Попробуйте связаться с администратором или зайти в систему позже";
        }
    }

    private String hashSHA1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkOwnership(long id, String login) {
        try (PreparedStatement ps = connection.prepareStatement(CHECK_OWNERSHIP_QUERY)) {
            ps.setLong(1, id);
            ps.setString(2, login);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
