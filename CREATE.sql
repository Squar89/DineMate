CREATE TABLE IF NOT EXISTS users
(
  user_id INTEGER PRIMARY KEY,
  login VARCHAR(30) UNIQUE NOT NULL,
  password_hash VARCHAR(30) NOT NULL,
  date_of_registration TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS loggings
(
  user_id INTEGER REFERENCES users,
  date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS dishes
(
  dish_id INTEGER PRIMARY KEY,
  description TEXT
);

CREATE TABLE IF NOT EXISTS ratings
(
  user_id INTEGER REFERENCES users,
  dish_id INTEGER REFERENCES dishes,
  rate NUMERIC CHECK (rate > 0 AND rate < 6), --TODO
  date TIMESTAMP NOT NULL,
  PRIMARY KEY (user_id, dish_id)
);
