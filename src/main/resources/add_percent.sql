--PROCEDURE: public.add_percent()

--DROP PROCEDURE IF EXISTS public.add_percent();

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
ALTER PROCEDURE public.add_percent()
    OWNER TO postgres;
