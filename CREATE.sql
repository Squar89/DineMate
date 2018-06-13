CREATE TABLE IF NOT EXISTS users
(
  user_id SERIAL PRIMARY KEY,
  login VARCHAR(30) UNIQUE NOT NULL,
  password_hash VARCHAR(30) NOT NULL,
  name VARCHAR(30) NOT NULL,
  year_of_birth INTEGER NOT NULL,
  sex VARCHAR(30) NOT NULL,
  contact VARCHAR(30) NOT NULL,
  description VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS dishes
(
  dish_id VARCHAR(10) PRIMARY KEY,
  description TEXT,
  name  TEXT,
  ingredients_list TEXT,
  directions TEXT,
  image_url TEXT,
  publisher_url TEXT
);

CREATE TABLE IF NOT EXISTS ratings
(
  user_id INTEGER REFERENCES users,
  dish_id VARCHAR(10) REFERENCES dishes,
  date TIMESTAMP NOT NULL DEFAULT now(),
  rate INTEGER,
  PRIMARY KEY (user_id, dish_id)
);




CREATE OR REPLACE FUNCTION daj_ziomkow( my_id INTEGER)
  RETURNS TABLE (so_id INTEGER,nick VARCHAR(30),age INTEGER, gender VARCHAR(30)) AS
$$
BEGIN
  RETURN QUERY
  SELECT user_id,name, CAST( EXTRACT(YEAR FROM now() )-year_of_birth AS INTEGER ) AS age,sex
  FROM users RIGHT JOIN (
                          SELECT
                            CAST( SUM(2 - ABS(oceny.moje - oceny.jego) ) AS INTEGER) AS suma,
                            jego_id
                          FROM
                            (
                              SELECT
                                my.rate      AS moje,
                                they.rate    AS jego,
                                they.user_id AS jego_id
                              FROM ratings AS my LEFT JOIN
                                (SELECT *
                                 FROM ratings) AS they ON
                                                         my.dish_id = they.dish_id
                              WHERE my.user_id = my_id AND they.user_id <> my_id
                                    AND my.rate IS NOT NULL
                                    AND they.rate IS NOT NULL
                            ) AS oceny
                          GROUP BY oceny.jego_id
                          ORDER BY suma  DESC
                          LIMIT 10
                        ) AS podobni ON user_id=jego_id;
END;
$$
LANGUAGE  plpgsql;




