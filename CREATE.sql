create table users
(
  user_id              integer     not null
    constraint users_pkey
    primary key,
  login                varchar(30) not null
    constraint users_login_key
    unique,
  password_hash        varchar(30) not null,
  date_of_registration timestamp   not null
);

create table loggings
(
  user_id integer
    constraint loggings_user_id_fkey
    references users,
  date    timestamp not null
);

create table dishes
(
  dish_id     integer not null
    constraint dishes_pkey
    primary key,
  name        text,
  ingredients text,
  directions  text,
  image_url   text
);

create table ratings
(
  user_id integer   not null
    constraint ratings_user_id_fkey
    references users,
  dish_id integer   not null
    constraint ratings_dish_id_fkey
    references dishes,
  rate    numeric
    constraint ratings_rate_check
    check ((rate > (0) :: numeric) AND (rate < (6) :: numeric)),
  date    timestamp not null,
  constraint ratings_pkey
  primary key (user_id, dish_id)
);

