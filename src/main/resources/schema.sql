DROP TABLE if EXISTS users, items, bookings, comments;
CREATE TABLE if NOT EXISTS public.users
(
    id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL,
    email varchar NOT NULL UNIQUE
);

CREATE TABLE if NOT EXISTS public.items
(
    id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL,
    description varchar NOT NULL,
    is_available boolean NOT NULL,
    owner_id bigint NOT NULL
);
CREATE TABLE if NOT EXISTS public.bookings
(
    id         int8    NOT NULL GENERATED ALWAYS AS IDENTITY,
    start_date timestamp without time zone NOT NULL,
    end_date   timestamp without time zone NOT NULL,
    item_id    int8    NOT NULL,
    booker_id  int8    NOT NULL,
    status     varchar NOT NULL,
    CONSTRAINT start_before_end CHECK (start_date < end_date)
);

CREATE TABLE if NOT EXISTS public.comments (
                                   id int8 NOT NULL GENERATED ALWAYS AS IDENTITY,
                                   text varchar NOT NULL,
                                   item_id int8 NOT NULL,
                                   author_id int8 NOT NULL,
                                   created timestamp without time zone
);



