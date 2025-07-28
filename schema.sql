-- Создание таблицы для хранения цен товаров от разных производителей
CREATE TABLE IF NOT EXISTS item_prices (
                                           item_id VARCHAR(255) NOT NULL,
                                           manufacturer VARCHAR(255) NOT NULL,
                                           price DECIMAL(10,2) NOT NULL,
                                           PRIMARY KEY (item_id, manufacturer)
);