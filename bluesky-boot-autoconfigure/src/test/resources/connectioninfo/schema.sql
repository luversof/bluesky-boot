CREATE TABLE `ConnectionInfo` (
	`connection` VARCHAR(50) NOT NULL,
	`url` VARCHAR(255) NOT NULL,
	`username` VARCHAR(50) NOT NULL,
	`password` VARCHAR(256) NOT NULL,
	`extradata` VARCHAR(1024)
);