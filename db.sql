--пользователь
CREATE TABLE users (
	user_id INTEGER PRIMARY KEY,
	login VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	phone VARCHAR(255),
	full_name VARCHAR(255),
	email VARCHAR(255),
	role VARCHAR(255)
);

--избранное
CREATE TABLE favorites (
	favorite_id INTEGER PRIMARY KEY,
	room_id INTEGER NOT NULL,
	user_id INTEGER NOT NULL,
	FOREIGN KEY (room_id) REFERENCES rooms(room_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id)
);

--бронь
CREATE TABLE bookings (
	booking_id INTEGER PRIMARY KEY,
	user_id INTEGER NOT NULL,
	room_id INTEGER NOT NULL,
	check_in_date DATE,
	check_out_date DATE,
	status VARCHAR(255),
	booking_date TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users(user_id),
	FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

--платёж
CREATE TABLE payments (
	payment_id INTEGER PRIMARY KEY,
	booking_id INTEGER NOT NULL,
	amount INTEGER,
	payment_date TIMESTAMP,
	status VARCHAR(255),
	type VARCHAR(255),
	FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

--номер
CREATE TABLE rooms (
	room_id INTEGER PRIMARY KEY,
	capacity INTEGER,
	comfort_level VARCHAR(255),
	price_per_night DECIMAL
);

--отзыв
CREATE TABLE reviews (
	review_id INTEGER PRIMARY KEY,
	user_id INTEGER NOT NULL,
	room_id INTEGER NOT NULL,
	review_date TIMESTAMP,
	content TEXT,
	rating INTEGER,
	FOREIGN KEY (user_id) REFERENCES users(user_id),
	FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);
