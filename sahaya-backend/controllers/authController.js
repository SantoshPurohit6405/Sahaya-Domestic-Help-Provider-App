require('dotenv').config();
const db = require('../config/db');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const SECRET_KEY = S@ntos#Puro#it6405 || 'your_strong_secret_key';

// Signup Controller
exports.signup = (req, res) => {
    const { name, phone_number, email, password, role } = req.body;

    // 🔎 Input Validation
    if (!name || !phone_number || !password || !role) {
        return res.status(400).json({ error: "All required fields must be filled." });
    }

    // 🔎 Role Validation
    const validRoles = ['customer', 'service_provider', 'admin'];
    if (!validRoles.includes(role.toLowerCase())) {
        return res.status(400).json({ error: "Invalid role. Allowed roles are: customer, service_provider, admin." });
    }

    // 🔎 Phone Number Validation
    const phoneRegex = /^[6-9]\d{9}$/;
    if (!phoneRegex.test(phone_number)) {
        return res.status(400).json({ error: "Invalid phone number format. Use a 10-digit number starting with 6-9." });
    }

    // 🔎 Email Validation (if provided)
    if (email && !/^\S+@\S+\.\S+$/.test(email)) {
        return res.status(400).json({ error: "Invalid email format." });
    }

    // 🔎 Check if User Already Exists
    const checkUserQuery = 'SELECT * FROM users WHERE phone_number = ?';
    db.query(checkUserQuery, [phone_number], (err, results) => {
        if (err) {
            return res.status(500).json({ error: "Database error." });
        }
        if (results.length > 0) {
            return res.status(409).json({ error: "User already exists with this phone number." });
        }

        // 🔐 Hash Password for Security
        bcrypt.hash(password, 10, (err, hashedPassword) => {
            if (err) {
                return res.status(500).json({ error: "Error hashing password." });
            }

            // 🔹 Insert User in Database
            const insertUserQuery = 'INSERT INTO users (name, phone_number, email, password, role) VALUES (?, ?, ?, ?, ?)';
            db.query(insertUserQuery, [name, phone_number, email, hashedPassword, role], (err, result) => {
                if (err) {
                    return res.status(500).json({ error: "Failed to register user." });
                }

                // 🔒 Generate JWT Token
                const token = jwt.sign(
                    { userId: result.insertId, role: role },
                    SECRET_KEY,
                    { expiresIn: '7d' } // Token valid for 7 days
                );

                // 🟢 Success Response
                res.status(201).json({
                    message: "User registered successfully!",
                    token: token
                });
            });
        });
    });
};

