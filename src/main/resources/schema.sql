CREATE TABLE IF NOT EXISTS users
(
    id        BIGINT GENERATED ALWAYS as IDENTITY PRIMARY KEY,
    password  VARCHAR(255),
    firstname VARCHAR(255),
    lastname  VARCHAR(255),
    username  VARCHAR(255),
    birthday  DATE,
    phone     VARCHAR(255),
    email     VARCHAR(255),
    role      varchar(255),
    CONSTRAINT uq_email UNIQUE (email),
    CONSTRAINT uq_phone UNIQUE (phone)
);

CREATE TABLE IF NOT EXISTS accounts
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT,
    balance BIGINT,
    count BIGINT,
    CONSTRAINT user_qu UNIQUE (user_id),
    CONSTRAINT no_null CHECK ( balance > 0 ),
    CONSTRAINT max_percent CHECK ( count < 16 )
);

CREATE OR REPLACE PROCEDURE public.transfer_money(
    IN id_from bigint,
    IN id_to bigint,
    IN amount_transfer bigint)
    LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
    amount_from record;
    amount_to record;

BEGIN

    LOCK TABLE accounts IN SHARE MODE;
    select balance into amount_from from accounts where user_id = id_from;
    select balance into amount_to from accounts where user_id = id_to;


    IF amount_from.balance < amount_transfer THEN
        RAISE EXCEPTION 'amount_from меньше amount_to';
    else
        amount_from.balance := amount_from.balance - amount_transfer;
        amount_to.balance := amount_to.balance + amount_transfer;
    END IF;

    update accounts
    set balance = amount_from.balance
    where user_id = id_from;

    update accounts
    set balance = amount_to.balance
    where user_id = id_to;


END;
$BODY$;

CREATE OR REPLACE PROCEDURE public.add_percent(
)
    LANGUAGE 'plpgsql'
AS
$BODY$
DECLARE
    amount_record RECORD;

BEGIN
    -- Ваш код здесь
    -- Например, выполнение каких-либо операций с параметром
    LOCK TABLE accounts IN SHARE MODE;
    FOR amount_record IN SELECT balance, count, id FROM accounts
        LOOP
            if amount_record.count >= 15 then
                RAISE NOTICE 'максимальный процент достигнут';
            else
                amount_record.balance := amount_record.balance * 1.05;
                amount_record.count := amount_record.count + 1;
            end if;
            update accounts
            set balance = amount_record.balance
            where id = amount_record.id;

            update accounts
            set count = amount_record.count
            where id = amount_record.id;

        END LOOP;

END;
$BODY$;