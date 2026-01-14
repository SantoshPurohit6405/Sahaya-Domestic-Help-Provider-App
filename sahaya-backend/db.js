// MySQL Database Connection
const db = mysql.createConnection({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || 'SantoshP6405',  // Use environment variables securely
    database: process.env.DB_NAME || 'sahaya_db',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

db.connect((err) => {
    if (err) {
        console.error('❌ Database connection failed:', err.message);
        process.exit(1); // Stop the server if the database fails
    }
    console.log('✅ Connected to MySQL Database');
});

module.exports = db;
