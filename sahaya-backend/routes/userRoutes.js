const express = require('express');
const router = express.Router();
const db = require('../config/db'); // Assuming you have db config set up

// Get User Role API
router.get('/getUserRole/:userId', (req, res) => {
    const { userId } = req.params;

    const query = 'SELECT role FROM users WHERE id = ?';
    
    db.query(query, [userId], (err, result) => {
        if (err) {
            return res.status(500).json({ error: 'Database error', details: err });
        }
        if (result.length === 0) {
            return res.status(404).json({ error: 'User not found' });
        }
        res.status(200).json({ role: result[0].role });
    });

    router.get('/profile', verifyToken, (req, res) => {
        res.json({ message: `Welcome ${req.user.userId}!` });
    });
});

module.exports = router;
