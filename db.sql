USE vradio;
CREATE TABLE accounts (id UUID not null, username varchar(255), email varchar(255), hashed_password varchar(255), verified bool, primary key (id));