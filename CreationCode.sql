create database FoodHub;
USE FoodHub;
create table Customers (
	ID int auto_increment primary key,
    name varchar(50) not null ,
    Address varchar(100) not null ,
    phone varchar(20) not null
);
create table  Orders (
	ID varchar(20) not null primary KEY,
    order_placed_at datetime default current_timestamp ,
    status ENUM('pending', 'preparing', 'ready', 'delivered', 'cancelled') NOT NULL default 'pending',
    completed_at datetime ,
    customer_id int not null,
    subtotal decimal(8,2),
    constraint cust_order_id foreign key(customer_id) references Customers(ID)
    ON delete restrict
    ON update cascade
    );
create table Categories(
	ID int auto_increment primary key,
    name varchar(50) not null
);
create table Meals (
	ID varchar(15) primary key,
    name varchar(70) not null,
    price decimal(8,2) not null ,
    description varchar(200),
    category_id int not null,
    constraint meals_cats_FK foreign key (category_id) references Categories (ID)
);
create table Order_Items (
	order_id varchar(20) not null ,
    meal_id varchar(15) not null ,
    quantity int not null default 1 ,
    price_at_time decimal(8,2) not null,
    constraint primary key(order_id ,meal_id ),
    constraint items_order_FK foreign key( order_id ) references Orders (ID),
    constraint items_meal_FK foreign key( meal_id ) references Meals (ID)
);
create table Invoice (
	ID varchar(20) primary key ,
    created_at datetime default current_timestamp ,
    fees decimal(8,2) not null,
    total_price decimal(8,2) not null ,
    payment_date datetime ,
    status ENUM('unpaid','partially_paid','paid','cancelled','refunded') NOT NULL DEFAULT 'unpaid',
    order_id varchar(20) not null,
    constraint invoice_order_FK foreign key(order_id) references Orders (ID)
)

/*Triggers*/
DELIMITER //
CREATE TRIGGER set_completed_at
BEFORE UPDATE ON Orders
FOR EACH ROW
BEGIN
    IF NEW.status = 'delivered' AND OLD.status != 'delivered' THEN
        SET NEW.completed_at = CURRENT_TIMESTAMP;
    END IF;
END;
//
DELIMITER ;
DELIMITER $$

CREATE TRIGGER set_payment_date
BEFORE UPDATE ON Invoice
FOR EACH ROW
BEGIN
    IF NEW.status = 'paid' AND OLD.status <> 'paid' THEN
        SET NEW.payment_date = CURRENT_TIMESTAMP;
    END IF;
END$$

DELIMITER ;        