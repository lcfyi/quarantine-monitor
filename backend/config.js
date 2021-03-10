const dotenv = require("dotenv");
const result = dotenv.config();

let { parsed: envs } = result;

envs = {
    ...process.env, // Let dotenv take precedence
    ...envs,
}

module.exports = envs;
