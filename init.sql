-- Создание таблицы actor
CREATE TABLE actor (
    Id SERIAL PRIMARY KEY,
    first_name VARCHAR(20) NOT NULL,
    second_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    CONSTRAINT uniq_actor_full_name UNIQUE (first_name, second_name, last_name)
);

-- Вставка данных в actor
INSERT INTO actor (first_name, second_name, last_name) VALUES
    ('Ewan', 'Gordon', 'McGregor'),
    ('Michael', 'John', 'Douglas'),
    ('Robert', 'John', 'Downey'),
    ('Sylvia', 'Sidney', ''),
    ('ывм', 'ывмыв', 'ымв'),
    ('ывм', 'ымв', 'ывм');

-- Создание таблицы director
CREATE TABLE director (
    Id SERIAL PRIMARY KEY,
    first_name VARCHAR(20) NOT NULL,
    second_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    CONSTRAINT uniq_director_full_name UNIQUE (first_name, second_name, last_name)
);

-- Вставка данных в director
INSERT INTO director (first_name, second_name, last_name) VALUES
    ('Cristopher', 'Advard', 'Nolan'),
    ('David', 'Andrew', 'Fincher'),
    ('sdv', 'svd', 'svd'),
    ('Timothy', 'Walter', 'Burton');

-- Создание таблицы film
CREATE TABLE film (
    id SERIAL PRIMARY KEY,
    director_id INT,
    title VARCHAR(255) NOT NULL,
    year INT,
    link VARCHAR(255),
    CONSTRAINT uniq_film UNIQUE (title, year, director_id),
    CONSTRAINT film_director_id_fk FOREIGN KEY (director_id) REFERENCES director(Id) ON DELETE SET NULL
);

-- Вставка данных в film
INSERT INTO film (director_id, title, year, link) VALUES
    (3, 'Pinocio', 2022, 'C:ilm'),
    (3, 'Beetlejuice', 1988, 'C:ilm'),
    (2, 'вслотвылсорвыосвыис', 2020, NULL);

-- Создание таблицы film_actors
CREATE TABLE film_actors (
    films_id INT,
    actors_id INT,
    CONSTRAINT fk_film_actors_actor FOREIGN KEY (actors_id) REFERENCES actor(Id) ON DELETE CASCADE,
    CONSTRAINT fk_film_actors_film FOREIGN KEY (films_id) REFERENCES film(id) ON DELETE CASCADE
);

-- Вставка данных в film_actors
INSERT INTO film_actors (films_id, actors_id) VALUES
    (2, 2),
    (2, 4),
    (1, 1),
    (1, 2),
    (1, 4),
    (1, 5),
    (3, 3),
    (3, 1),
    (3, 2),
    (3, 4),
    (3, 5),
    (3, 6);