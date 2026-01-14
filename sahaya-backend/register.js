const express = require('express');
const router = express.Router();
const db = require('../database'); // Assuming you have a database connection

router.post('/register', async (req, res) => {
    const { username, phone, password } = req.body;

    if (!username || !phone || !password) {
        return res.status(400).json({ status: 'error', message: 'All fields are required.' });
    }

    try {
        const query = `INSERT INTO users (username, phone, password) VALUES (?, ?, ?)`;
        await db.query(query, [username, phone, password]);

        res.status(200).json({ status: 'success', message: 'User registered successfully.' });
    } catch (error) {
        res.status(500).json({ status: 'error', message: 'Database error occurred.' });
    }
});

module.exports = router;
