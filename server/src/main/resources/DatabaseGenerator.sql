BEGIN;

DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS STUDYGROUP CASCADE;
DROP TYPE IF EXISTS COLOR CASCADE;
DROP TYPE IF EXISTS FORMOFEDUCATION CASCADE;

CREATE TYPE COLOR AS ENUM ('RED','BLACK','BLUE', 'BROWN');
CREATE TYPE FORMOFEDUCATION AS ENUM ('DISTANCE_EDUCATION', 'FULL_TIME_EDUCATION', 'EVENING_CLASSES');

CREATE TABLE IF NOT EXISTS USERS
(
    name
    TEXT
    NOT
    NULL
    PRIMARY
    KEY,
    password
    TEXT
    NOT
    NULL,
    repeats
    INT
    NOT
    NULL
    DEFAULT
    1
);

CREATE TABLE IF NOT EXISTS STUDYGROUP
(
    id
    BIGSERIAL
    UNIQUE
    PRIMARY
    KEY,
    name
    TEXT
    NOT
    NULL
    CHECK
(
    name
    !=
    ''
),
    x INTEGER NOT NULL CHECK
(
    x >
    -
    151
),
    y BIGINT NOT NULL CHECK
(
    y >
    -
    436
),
    creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    studentsCount INTEGER CHECK
(
    studentsCount >
    0
),
    expelledStudents INTEGER CHECK
(
    expelledStudents >
    0
),
    shouldBeExpelled INTEGER CHECK
(
    shouldBeExpelled >
    0
),
    formOfEducation FORMOFEDUCATION,
    nameP TEXT NOT NULL CHECK
(
    name != ''
),
    passportID TEXT NOT NULL CHECK
(
    name != ''
),
    color COLOR,
    xP INTEGER NOT NULL CHECK
(
    x >
    -
    32
),
    yP BIGINT NOT NULL,
    nameloc TEXT NOT NULL CHECK
(
    name != ''
),
    login TEXT NOT NULL
    );

ALTER TABLE STUDYGROUP
ALTER
COLUMN creationDate TYPE TIMESTAMP(0);
COMMIT;