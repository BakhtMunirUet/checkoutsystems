
-- seeding of items to TBL_ITEM
INSERT INTO TBL_ITEM (NAME, PRICE, IS_ACTIVE, CREATED_DATE)
VALUES
    ('Apple', 0.30, TRUE, NOW()),
    ('Banana', 0.20, TRUE, NOW()),
    ('Orange', 0.40, TRUE, NOW()),
    ('Milk', 1.20, TRUE, NOW()),
    ('Bread', 1.00, TRUE, NOW());


-- seeding for first offer on apple in TBL_OFFER
INSERT INTO TBL_OFFER
(ITEM_ID, QUANTITY, OFFER_PRICE, IS_ACTIVE, CREATED_DATE)
SELECT ID, 2, 0.45, TRUE, NOW()
FROM TBL_ITEM WHERE NAME = 'Apple';

-- seeding for second offer on banana in TBL_OFFER
INSERT INTO TBL_OFFER
(ITEM_ID, QUANTITY, OFFER_PRICE, IS_ACTIVE, CREATED_DATE)
SELECT ID, 3, 0.50, TRUE, NOW()
FROM TBL_ITEM WHERE NAME = 'Banana';