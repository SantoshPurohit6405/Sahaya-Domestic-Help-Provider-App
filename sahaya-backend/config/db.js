require('dotenv').config();
const mysql = require('mysql');

const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: SantoshP6405,
    database: 'sahaya_db'
});

db.connect(err => {
    if (err) throw err;
    console.log('MySQL Database Connected.');
});

module.exports = db;

