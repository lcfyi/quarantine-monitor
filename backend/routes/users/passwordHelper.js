const crypto = require("crypto");

/*
 *	The following functions are used to encrypt/decrypt the password
 */
var genRandomString = function (length) {
	return crypto.randomBytes(Math.ceil(length / 2))
		.toString("hex") //converts it to hex format
		.slice(0, length);
};

// Uses SHA(Secure hashing algorithm) to encrypt 64-bit words
var sha512 = function (password, salt) {
	var hash = crypto.createHmac("sha512", salt);
	hash.update(password);
	var value = hash.digest("hex");
	return {
		salt,
		passwordHash: value
	};
};

function saltHashPassword(userPassword) {
	var salt = genRandomString(16); //Randomly generates 16 characters
	var passwordData = sha512(userPassword, salt);
	return passwordData;
}

function checkHashPassword(userPassword, salt) {
	var passwordData = sha512(userPassword, salt);
	return passwordData;
}

module.exports = {
    saltHashPassword,
    checkHashPassword
};
