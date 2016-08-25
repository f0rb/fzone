CREATE DATABASE fzone;

CREATE USER fzone_user@localhost IDENTIFIED BY 'fzone_pass';

GRANT ALL PRIVILEGES ON fzone.* TO fzone_user@localhost IDENTIFIED BY 'fzone_pass';

flush privileges;