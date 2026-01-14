require('dotenv').config(); 
const express = require('express');
const mysql = require('mysql2');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// MySQL Database Connection
const db = mysql.createConnection({
    host: process.env.DB_HOST || '192.168.0.103',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || 'SantoshP6405',
    database: process.env.DB_NAME || 'sahaya_db'
});

db.connect((err) => {
    if (err) {
        console.error('❌ Database connection failed:', err.message);
        process.exit(1);
    }
    console.log('✅ Connected to MySQL Database');
});

// JWT Secret Key
const SECRET_KEY = process.env.JWT_SECRET || 'S@ntos#Puro#it6405';

// ===================== SIGNUP API (No Role Allotment) =====================
app.post('/signup', async (req, res) => {
    try {
        const { username, phone, email, password } = req.body;

        console.log(`📌 Signup Request: ${username}, ${phone}, ${email}`);

        if (!username || !phone || !password) {
            return res.status(400).json({ status: "error", error: "❌ All fields except email are required." });
        }

        // Check if user already exists
        const [existingUser] = await db.promise().query('SELECT * FROM users WHERE phone = ?', [phone]);
        if (existingUser.length > 0) {
            return res.status(400).json({ status: "error", error: "❌ User already exists." });
        }

        // Hash the password before storing it
        const hashedPassword = await bcrypt.hash(password, 10);

        // Insert new user into the database
        const [result] = await db.promise().query(
            'INSERT INTO users (username, phone, email, password) VALUES (?, ?, ?, ?)',
            [username, phone, email || null, hashedPassword]
        );

        if (result.affectedRows > 0) {
            console.log(`✅ User Registered: { username: '${username}', phone: '${phone}' }`);
            return res.status(201).json({ status: "success", message: "✅ Signup successful! Please select a role." });
        } else {
            return res.status(500).json({ status: "error", error: "❌ Signup failed." });
        }

    } catch (error) {
        console.error(`❌ Internal Server Error: ${error.message}`);
        return res.status(500).json({ status: "error", error: "❌ Internal Server Error." });
    }
});




// ===================== Role Selection API =====================
app.post('/select-role', async (req, res) => {
    try {
        const { phone, role } = req.body;

        console.log(`📌 Role Selection Request: Phone = ${phone}, Role = ${role}`);

        // ✅ Validate Input
        if (!phone || !role) {
            return res.status(400).json({ status: "error", error: "❌ Missing phone number or role." });
        }

        // ✅ Validate Role
        const validRoles = ["Customer", "Service Provider"];
        if (!validRoles.includes(role)) {
            return res.status(400).json({ status: "error", error: "❌ Invalid role selected." });
        }

        // ✅ Check if user exists
        const checkUserQuery = "SELECT * FROM users WHERE phone = ?";
        db.query(checkUserQuery, [phone], (err, userResult) => {
            if (err) {
                console.error("❌ Database Query Error:", err);
                return res.status(500).json({ status: "error", error: "❌ Database error while fetching user." });
            }

            if (userResult.length === 0) {
                return res.status(404).json({ status: "error", error: "❌ User not found." });
            }

            // ✅ Update Role in Database
            const updateRoleQuery = "UPDATE users SET role = ? WHERE phone = ?";
            db.query(updateRoleQuery, [role, phone], (updateErr, updateResult) => {
                if (updateErr) {
                    console.error("❌ Role Update Error:", updateErr);
                    return res.status(500).json({ status: "error", error: "❌ Database error while updating role." });
                }

                if (updateResult.affectedRows === 0) {
                    console.log("❌ Role update failed (no rows affected).");
                    return res.status(500).json({ status: "error", error: "❌ Role update failed." });
                }

                console.log(`✅ Role Updated Successfully: Phone = ${phone}, Role = ${role}`);
                return res.status(200).json({ status: "success", message: `✅ Role updated to ${role} successfully!` });
            });
        });

    } catch (error) {
        console.error("❌ Unexpected Error:", error);
        return res.status(500).json({ status: "error", error: "❌ Internal server error." });
    }
});



// ===================== SERVICE SELECTION API =====================
app.post('/select-services', (req, res) => {
    const { phone, services, experience, availability } = req.body;
    console.log('🔧 Received Service Selection:', req.body);

    // ✅ Validate phone
    if (!phone) {
        return res.status(400).json({ status: "error", error: "Phone number is required." });
    }

    // ✅ Validate services: must be an array with 1 or 2 items
    if (!Array.isArray(services) || services.length < 1 || services.length > 2) {
        return res.status(400).json({ status: "error", error: "You must select 1 or 2 services." });
    }

    // ✅ Validate experience (Convert "Fresher" to 0 and "Experienced" to 1)
    const experienceMapping = { "Fresher": 0, "Experienced": 1, "0": 0, "1": 1 };
    const experienceValue = experienceMapping[experience];
    if (experienceValue === undefined) {
        return res.status(400).json({ status: "error", error: "Invalid experience value!" });
    }

    // ✅ Step 1: Fetch user details from `users` table
    const getUserQuery = "SELECT id, username, email FROM users WHERE phone = ?";
    db.query(getUserQuery, [phone], (err, userResult) => {
        if (err) {
            console.error("❌ Database Query Error:", err);
            return res.status(500).json({ status: "error", error: "Database error while fetching user." });
        }
        if (userResult.length === 0) {
            return res.status(404).json({ status: "error", error: "User not found." });
        }

        const { id: userId, username: providerName, email } = userResult[0];

        // ✅ Step 2: Update role to 'Service Provider' in `users` table
        const updateRoleQuery = "UPDATE users SET role = 'Service Provider' WHERE phone = ?";
        db.query(updateRoleQuery, [phone], (updateErr) => {
            if (updateErr) {
                console.error("❌ Role Update Error:", updateErr);
                return res.status(500).json({ status: "error", error: "Database error while updating role." });
            }

            // ✅ Step 3: Insert or update provider details in `service_providers` table
            const insertProviderQuery = `
                INSERT INTO service_providers (user_id, provider_name, email, phone, experience, availability, created_at)
                VALUES (?, ?, ?, ?, ?, ?, NOW())
                ON DUPLICATE KEY UPDATE experience = VALUES(experience), availability = VALUES(availability)`;

            db.query(insertProviderQuery, [userId, providerName, email, phone, experienceValue, availability], (providerErr) => {
                if (providerErr) {
                    console.error("❌ Provider Insertion Error:", providerErr);
                    return res.status(500).json({ status: "error", error: "Database error while inserting provider details." });
                }

                console.log('✅ Service Provider Registered:', { phone, providerName, services, experience });

                // ✅ Step 4: Get provider `id` from `service_providers`
                const getProviderIdQuery = "SELECT id FROM service_providers WHERE user_id = ?";
                db.query(getProviderIdQuery, [userId], (providerIdErr, providerIdResult) => {
                    if (providerIdErr || providerIdResult.length === 0) {
                        console.error("❌ Provider ID Fetch Error:", providerIdErr);
                        return res.status(500).json({ status: "error", error: "Failed to retrieve provider ID." });
                    }

                    const providerId = providerIdResult[0].id;

                    // ✅ Step 5: Remove previous services before inserting new ones
                    const deleteServicesQuery = "DELETE FROM provider_services WHERE provider_id = ?";
                    db.query(deleteServicesQuery, [providerId], (deleteErr) => {
                        if (deleteErr) {
                            console.error("❌ Service Deletion Error:", deleteErr);
                            return res.status(500).json({ status: "error", error: "Database error while deleting previous services." });
                        }

                        // ✅ Step 6: Insert new services into `provider_services` table
                        const serviceInsertions = services.map(servicename => {
                            return new Promise((resolve, reject) => {
                                const insertServiceQuery = "INSERT INTO provider_services (provider_id, service_name) VALUES (?, ?)";
                                db.query(insertServiceQuery, [providerId, servicename], (serviceErr) => {
                                    if (serviceErr) {
                                        console.error("❌ Service Insertion Error:", serviceErr);
                                        reject(serviceErr);
                                    } else {
                                        resolve();
                                    }
                                });
                            });
                        });

                        // Execute all service insertions
                        Promise.all(serviceInsertions)
                            .then(() => {
                                console.log("✅ Services added successfully!");
                                return res.status(200).json({ status: "success", message: "✅ Services added successfully!" });
                            })
                            .catch(serviceErr => {
                                console.error("❌ Service Insertion Error:", serviceErr);
                                return res.status(500).json({ status: "error", error: "Database error while inserting services." });
                            });
                    });
                });
            });
        });
    });
});



// ✅ LOGIN API (Supports both phone number and username)
app.post('/login', (req, res) => {
    const { identifier, password } = req.body;

    // ✅ Validate Input
    if (!identifier || !password) {
        return res.status(400).json({ status: "error", error: "Phone/Username and password are required." });
    }

    // ✅ Query User by Phone OR Username
    const query = 'SELECT * FROM users WHERE phone = ? OR username = ? LIMIT 1';

    db.query(query, [identifier, identifier], async (err, rows) => {
        if (err) {
            console.error("❌ Database Query Failed:", err);
            return res.status(500).json({ status: "error", error: "Database query failed." });
        }
        if (rows.length === 0) {
            return res.status(404).json({ status: "error", error: "User not found." });
        }

        const user = rows[0];
        const storedPassword = user.password;

        try {
            // ✅ Check if password is hashed (bcrypt hashes start with $2a$, $2b$, or $2y$)
            const isMatch = storedPassword.startsWith('$2') ? 
                await bcrypt.compare(password, storedPassword) : 
                password === storedPassword;


                
            if (!isMatch) {
                return res.status(401).json({ status: "error", error: "Incorrect password." });
            }

            // ✅ Generate JWT Token
            const token = jwt.sign({ id: user.id, role: user.role }, SECRET_KEY, { expiresIn: '2h' });


             // ✅ If Admin (ID = 1), Update Last Login
             if (user.id === 1) {
                const currentTime = new Date();
                const updateQuery = 'UPDATE users SET last_login = ? WHERE id = 1';
                db.query(updateQuery, [currentTime], (updateErr) => {
                    if (updateErr) {
                        console.error("❌ Failed to update last login:", updateErr);
                    }
                });
            }

            console.log(`✅ Login successful for user: ${user.username || user.phone}`);


            // ✅ Send Response
            return res.status(200).json({
                status: "success",
                message: "Login successful",
                token,
                user_id: user.id,  // ✅ Added user ID
                role: user.role,
                username: user.username,
                phone: user.phone,
                last_login: user.id === 1 ? user.last_login : null // Return last login only for admin
            });

        } catch (error) {
            console.error("❌ Password Comparison Error:", error);
            return res.status(500).json({ status: "error", error: "Error processing login request." });
        }
    });
});



// API to fetch all customers (excluding password)
app.get('/customers', (req, res) => {  // Use GET instead of POST
    const query = `SELECT id, username, phone AS phoneNumber, email, created_at FROM users WHERE role = 'customer'`;

    db.query(query, (err, results) => {
        if (err) {
            console.error("Database query error:", err);
            return res.status(500).json({ success: false, error: "Database query failed" });
        }

        if (results.length === 0) {
            return res.status(404).json({ success: false, message: "No customers found" });
        }

        res.status(200).json({ success: true, customers: results });
    });
});

// ✅ UPDATED GET all service providers with services and user creation date
// ✅ GET all service providers with full details
app.get("/providers", (req, res) => {
    const query = `
        SELECT 
            sp.user_id,
            sp.id AS provider_id,
            sp.provider_name,
            sp.experience,
            u.created_at,
            GROUP_CONCAT(ps.service_name) AS services
        FROM service_providers sp
        JOIN users u ON sp.user_id = u.id
        LEFT JOIN provider_services ps ON ps.provider_id = sp.id
        GROUP BY sp.id
    `;

    db.query(query, (err, results) => {
        if (err) {
            console.error("❌ Database query failed:", err);
            return res.status(500).json({ success: false, error: "Database query failed", details: err.message });
        }

        res.status(200).json({
            success: true,
            providers: results.length > 0 ? results : []
        });
    });
});




// ✅ API to Fetch Admin Details (Including Last Login)
app.get('/admin', (req, res) => {
    const sql = 'SELECT id, username, phone, email, last_login FROM users WHERE id = 1 LIMIT 1';
    
    db.query(sql, (err, rows) => {
        if (err) {
            console.error("❌ Database Query Failed:", err.sqlMessage);
            return res.status(500).json({ status: "error", error: "Database query failed." });
        }
        if (rows.length === 0) {
            console.error("❌ No admin found with ID = 1");
            return res.status(404).json({ status: "error", error: "Admin not found." });
        }

        console.log("✅ Admin Data Fetched Successfully:", rows[0]);
        return res.status(200).json({ status: "success", admin: rows[0] });
    });
});



// ✅ API to fetch logged-in customer's profile
app.get("/customerprofile/:id?", (req, res) => {
    // Check if ID is from URL or Query Params
    const customerId = req.params.id || req.query.id;

    if (!customerId) {
        return res.status(400).json({ success: false, message: "Customer ID is required" });
    }

    const query = "SELECT id, username, email, phone, created_at FROM users WHERE id = ? AND role = 'customer'";

    db.query(query, [customerId], (err, results) => {
        if (err) {
            console.error("❌ Database query failed:", err);
            return res.status(500).json({ success: false, message: "Database query error", details: err.message });
        }

        if (results.length > 0) {
            res.json({ success: true, customer: results[0] }); // ✅ Return the logged-in customer's details
        } else {
            res.status(404).json({ success: false, message: "Customer not found" });
        }
    });
});

// ✅ API to fetch logged-in service provider's profile
app.get("/providerprofile/:user_id?", (req, res) => {
    // Extract User ID from URL Params OR Query Params
    const userId = req.params.user_id || req.query.user_id;

    if (!userId) {
        return res.status(400).json({ success: false, message: "User ID is required" });
    }

    const query = `
        SELECT 
            sp.id AS provider_id, 
            sp.user_id, 
            sp.provider_name, 
            sp.email, 
            sp.phone, 
            sp.experience, 
            sp.created_at,
            u.username 
        FROM service_providers sp
        JOIN users u ON sp.user_id = u.id
        WHERE sp.user_id = ? AND u.role = 'Service Provider'`;

    db.query(query, [userId], (err, results) => {
        if (err) {
            console.error("❌ Database query failed:", err);
            return res.status(500).json({ success: false, message: "Database query error", details: err.message });
        }

        if (results.length > 0) {
            res.json({ success: true, provider: results[0] }); // ✅ Return provider details
        } else {
            res.status(404).json({ success: false, message: "Service Provider not found" });
        }
    });
});

// Update Profile API
app.put("/update-profile/:user_id?", (req, res) => {
    const userId = req.params.user_id || req.body.user_id;

    if (!userId) {
        return res.status(400).json({ success: false, message: "User ID is required" });
    }

    const { username, email, phone, password } = req.body;
    let fields = [];
    let values = [];

    if (username) {
        fields.push("username = ?");
        values.push(username);
    }
    if (email) {
        fields.push("email = ?");
        values.push(email);
    }
    if (phone) {
        fields.push("phone = ?");
        values.push(phone);
    }

    // ✅ If password is provided, hash it before updating
    if (password) {
        bcrypt.hash(password, 10, (err, hashedPassword) => {
            if (err) {
                console.error("❌ Error hashing password:", err);
                return res.status(500).json({ success: false, message: "Error processing password" });
            }
            fields.push("password = ?");
            values.push(hashedPassword);
            updateUserProfile(fields, values, userId, res);
        });
    } else {
        updateUserProfile(fields, values, userId, res);
    }
});

function updateUserProfile(fields, values, userId, res) {
    if (fields.length === 0) {
        return res.status(400).json({ success: false, message: "No fields to update" });
    }

    const query = `UPDATE users SET ${fields.join(", ")} WHERE id = ?`;
    values.push(userId);

    db.query(query, values, (err, result) => {
        if (err) {
            console.error("❌ Database error:", err);
            return res.status(500).json({ success: false, message: "Database update error", error: err.message });
        }
        res.status(200).json({ success: true, message: "Profile updated successfully" });
    });
}

app.get('/providers/by-service/:serviceName', (req, res) => {
    const serviceName = req.params.serviceName;
    console.log("📥 Requested service:", serviceName); // For debugging

    const query = `
        SELECT sp.id, sp.provider_name, sp.phone, sp.experience, ps.service_name
        FROM service_providers sp
        JOIN provider_services ps ON sp.id = ps.provider_id
        WHERE ps.service_name = ?
    `;

    db.query(query, [serviceName], (err, results) => {
        if (err) {
            console.error("❌ Database error while fetching providers:", err);
            return res.status(500).json({ message: "Internal Server Error", error: err.message });
        }

        res.status(200).json(results);
    });
});


// Booking API
app.post('/create-booking', (req, res) => {
    const { user_id, provider_id } = req.body;

    // Fetch customer details from users table
    const getUserDetails = "SELECT id, username, phone, email FROM users WHERE role = 'Customer'";
    db.query(getUserDetails, [user_id], (err, userResult) => {
        if (err) {
            console.error('Error fetching user details:', err);
            return res.status(500).json({ message: 'Error fetching user details', error: err });
        }
        if (userResult.length === 0) {
            return res.status(404).json({ message: 'Customer not found' });
        }

        const customer = userResult[0];

        // Fetch service provider details from service_providers table
        const getProviderDetails = "SELECT provider_name, email, phone FROM service_providers WHERE id = ?";
        db.query(getProviderDetails, [provider_id], (err, providerResult) => {
            if (err) {
                console.error('Error fetching provider details:', err);
                return res.status(500).json({ message: 'Error fetching provider details', error: err });
            }
            if (providerResult.length === 0) {
                return res.status(404).json({ message: 'Service provider not found' });
            }

            const provider = providerResult[0];

            // Fetch service_name from provider_services table based on provider_id
            const getServiceName = "SELECT service_name FROM provider_services WHERE provider_id = ?";
            db.query(getServiceName, [provider_id], (err, serviceResult) => {
                if (err) {
                    console.error('Error fetching service name:', err);
                    return res.status(500).json({ message: 'Error fetching service name', error: err });
                }
                if (serviceResult.length === 0) {
                    return res.status(404).json({ message: 'No service found for this provider' });
                }

                const service_name = serviceResult[0].service_name;

                // Insert booking into bookings table with all necessary details
                const insertBooking = `
                    INSERT INTO bookings (
                        service_name, status, customer_name, customer_email, customer_phone,
                        provider_name, provider_email, provider_phone, customer_id, provider_id
                    ) 
                    VALUES (?, 'pending', ?, ?, ?, ?, ?, ?, ?, ?)
                `;
                db.query(insertBooking, [
                    service_name, customer.username, customer.email, customer.phone,
                    provider.provider_name, provider.email, provider.phone,
                    user_id, provider_id
                ], (err, bookingResult) => {
                    if (err) {
                        console.error('Error inserting booking:', err);
                        return res.status(500).json({ message: 'Error creating booking', error: err });
                    }

                    return res.status(200).json({
                        message: 'Booking created successfully',
                        booking_id: bookingResult.insertId,
                        customer: customer,
                        provider: provider,
                        service_name: service_name
                    });
                });
            });
        });
    });
});


// ✅ API to fetch pending service requests for a specific provider
app.get('/api/bookings/pending/:provider_id', (req, res) => {
    const providerId = req.params.provider_id; // Get provider_id from URL params

    // SQL query to fetch pending bookings for the specific provider
    const sql = `
        SELECT 
            service_id, service_name, status, provider_name, 
            customer_name, customer_email, customer_phone, 
            provider_email, provider_phone, booking_date, booking_time
        FROM bookings 
        WHERE provider_id = ? AND status = 'pending'
    `;

    // Execute the query
    db.query(sql, [providerId], (err, results) => {
        if (err) {
            // Log error for debugging purposes
            console.error("❌ SQL Error:", err);
            return res.status(500).json({ error: "Error fetching service requests", details: err });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: "No pending requests found for this provider." });
        }

        // Respond with the pending service requests
        return res.status(200).json({ message: "Pending service requests fetched successfully", data: results });
    });
});

// API route to fetch provider ID by user ID
app.get('/api/service-providers/by-user/:userId', (req, res) => {
    const userId = req.params.userId;
  
    // SQL query to fetch the providers_id from service_providers table using user_id
    const query = 'SELECT id FROM service_providers WHERE user_id = ?';
  
    db.query(query, [userId], (err, result) => {
      if (err) {
        console.error('Error querying the database:', err);
        return res.status(500).json({ error: 'Database error' });
      }
  
      if (result.length > 0) {
        // Return the providers_id of the corresponding user_id
        return res.json({ id: result[0].id });
      } else {
        // No matching provider found for the given user_id
        return res.status(404).json({ error: 'Provider not found' });
      }
    });
  });

// Reject Booking API
app.delete('/reject-booking/:serviceId', (req, res) => {
    const { serviceId } = req.params;

    // Fetch the booking details to get the provider and customer info
    const getBookingDetails = "SELECT * FROM bookings WHERE service_id = ?";
    db.query(getBookingDetails, [serviceId], (err, bookingResult) => {
        if (err) {
            console.error('Error fetching booking details:', err);
            return res.status(500).json({ message: 'Error fetching booking details', error: err });
        }
        if (bookingResult.length === 0) {
            return res.status(404).json({ message: 'Booking not found' });
        }

        // Delete the booking from the bookings table using service_id
        const deleteBooking = "DELETE FROM bookings WHERE service_id = ?";
        db.query(deleteBooking, [serviceId], (err, deleteResult) => {
            if (err) {
                console.error('Error deleting booking:', err);
                return res.status(500).json({ message: 'Error rejecting the booking', error: err });
            }

            // Respond with success message
            res.status(200).json({
                message: 'Service request rejected successfully',
                serviceId: serviceId
            });
        });
    });
});

// Accept Booking API
app.put('/accept-booking/:serviceId', (req, res) => {
    const { serviceId } = req.params;

    // Fetch the booking details to get the provider and customer info
    const getBookingDetails = "SELECT * FROM bookings WHERE service_id = ?";
    db.query(getBookingDetails, [serviceId], (err, bookingResult) => {
        if (err) {
            console.error('Error fetching booking details:', err);
            return res.status(500).json({ message: 'Error fetching booking details', error: err });
        }
        if (bookingResult.length === 0) {
            return res.status(404).json({ message: 'Booking not found' });
        }

        // Update the booking status to 'accepted'
        const updateBookingStatus = "UPDATE bookings SET status = 'accepted' WHERE service_id = ?";
        db.query(updateBookingStatus, [serviceId], (err, updateResult) => {
            if (err) {
                console.error('Error updating booking status:', err);
                return res.status(500).json({ message: 'Error accepting the booking', error: err });
            }

            // Respond with success message
            res.status(200).json({
                message: 'Service request accepted successfully',
                serviceId: serviceId
            });
        });
    });
});


// Simple API to Fetch Booking by user_id
app.get('/customer-booking-status/:userId', (req, res) => {
    const userId = req.params.userId;

    const query = `
        SELECT service_id, service_name, provider_name, status
        FROM bookings
        WHERE customer_id = ?
        ORDER BY service_id DESC
        LIMIT 1
    `;

    db.query(query, [userId], (error, results) => {
        if (error) {
            console.error('Error fetching booking:', error.message); // More detailed error log
            return res.status(500).json({ message: 'Error fetching booking details.', error: error.message });
        }

        if (results.length > 0) {
            const booking = results[0];
            console.log('Booking Details:', booking);
            return res.status(200).json({
                message: 'Booking details fetched successfully.',
                booking: booking
            });
        } else {
            console.log('No active bookings found for this customer');
            return res.status(404).json({ message: 'No active bookings found for this customer.' });
        }
    });
});

// ✅ Get active service by customer or provider name
app.get('/active-service-by-name/:name', (req, res) => {
    const name = req.params.name;

    const query = `
        SELECT service_id, service_name, provider_name, provider_phone,
               customer_name, customer_phone, status
        FROM bookings
        WHERE (customer_name = ? OR provider_name = ?)
          AND status IN ('accepted', 'in_progress')
        ORDER BY service_id DESC
        LIMIT 1
    `;

    db.query(query, [name, name], (error, results) => {
        if (error) {
            console.error('❌ Error fetching service:', error.message);
            return res.status(500).json({
                message: 'Error fetching service details.',
                error: error.message
            });
        }

        if (results.length > 0) {
            const service = results[0];
            console.log('✅ Active Service Found:', service);
            return res.status(200).json({
                message: 'Active service found.',
                service: service
            });
        } else {
            console.log('ℹ️ No active service found for:', name);
            return res.status(404).json({ message: 'No active service found for this name.' });
        }
    });
});

// API endpoint to fetch the active service for a provider
app.get('/get-active-service/:providerId', (req, res) => {
    const providerId = req.params.providerId;

    const query = `
        SELECT * FROM bookings WHERE provider_id = ? AND status IN ('accepted')
    `;

    db.query(query, [providerId], (error, results) => {
        if (error) {
            console.error('Error fetching active service:', error);
            return res.status(500).json({ message: 'Error fetching active service.' });
        }

        if (results.length > 0) {
            // Assuming the provider has an active service
            const service = results[0]; // Take the first active service
            return res.status(200).json({ service_id: service.service_id });
        } else {
            return res.status(404).json({ message: 'No active service found for this provider.' });
        }
    });
});

app.put('/end-service/:serviceId', (req, res) => {
    const serviceId = req.params.serviceId;

    // Fetch the service from the 'bookings' table using the serviceId
    const query = "SELECT * FROM bookings WHERE service_id = ?";
    db.query(query, [serviceId], (err, results) => {
        if (err) {
            console.error("Error fetching service:", err);
            return res.status(500).json({ message: "Error checking service status." });
        }

        if (results.length === 0) {
            return res.status(400).json({ message: "Service not found." });
        }

        const service = results[0]; // Assuming the query returns an array of results
        const status = service.status;  // Assuming the status is stored in the 'status' field

        if (status === "completed") {
            return res.status(400).json({ message: "Service has already been completed." });
        }

        // Update the service status to "completed"
        const updateQuery = "UPDATE bookings SET status = 'completed' WHERE service_id = ?";
        db.query(updateQuery, [serviceId], (err, result) => {
            if (err) {
                console.error("Error updating service status:", err);
                return res.status(500).json({ message: "Error completing service." });
            }

            // Successfully updated, send success message
            return res.status(200).json({ message: "Service successfully completed." });
        });
    });
});

// GET /api/completed-bookings/:username
app.get("/api/completed-bookings/:providerName", (req, res) => {
    const providerName = req.params.providerName;

    const query = `
        SELECT 
            service_id,
            service_name,
            customer_name,
            provider_name,
            booking_date,
            completed_at,
            status
        FROM bookings
        WHERE provider_name = ? AND status = 'completed'
        ORDER BY completed_at DESC
    `;

    db.query(query, [providerName], (err, results) => {
        if (err) {
            console.error("❌ Error fetching completed bookings:", err);
            return res.status(500).json({
                success: false,
                message: "Error fetching completed bookings",
                error: err.message
            });
        }

        res.status(200).json({
            success: true,
            completedBookings: results.length > 0 ? results : []
        });
    });
});

// ===================== SERVER CONFIG =====================
app.listen(PORT, () => {
    console.log(`🚀 Server running on http://localhost:${PORT}`);
});







 
