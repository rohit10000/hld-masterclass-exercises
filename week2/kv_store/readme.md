Preparation:
1. CREATE TABLE store(
key VARCHAR(60) PRIMARY KEY,
value VARCHAR(60),
expired_at TIMESTAMP
);


2. INSERT INTO store (key, value, expired_at)
VALUES
('Rohit', 'System Design', now()),
('Shweta', 'Serial', date_add(now, interval 1 hour));

   
kv_store api:

PUT(k, v, ttl):
    insert into store(key, value, expired_at) values('Rohit', 'Java', date_add(now, interval 1 day)) 
        on duplicate key update value = values(value), expired_at = values(expired_at);

GET(k):
    select * from store where key = k and expired_at > now();

DEL(k):
    update store set expired_at = '2000-01-01 00:00:01' where key = k and expired_at > now();


Note: replace key with `key` as key is reserved keyword