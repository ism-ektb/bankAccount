CREATE OR REPLACE PROCEDURE public.add_percent(
)
    LANGUAGE 'plpgsql'
AS
$BODY$
DECLARE
    amount_record RECORD;

BEGIN
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
