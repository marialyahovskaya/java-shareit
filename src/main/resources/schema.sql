DROP TABLE if EXISTS users, items;
CREATE TABLE if NOT EXISTS public.users
(
    id
    int8
    GENERATED
    ALWAYS AS
    IDENTITY
    PRIMARY
    KEY,
    name
    varchar
    NOT
    NULL,
    email
    varchar
    NOT
    NULL
    UNIQUE
);

CREATE TABLE if NOT EXISTS public.items
(
    id
    int8
    GENERATED
    ALWAYS AS
    IDENTITY
    PRIMARY
    KEY,
    "name"
    varchar
    NOT
    NULL,
    description
    varchar
    NOT
    NULL,
    is_available
    boolean
    NOT
    NULL,
    owner_id
    bigint
    NOT
    NULL
);


