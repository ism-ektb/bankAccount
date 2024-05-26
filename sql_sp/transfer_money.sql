
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
