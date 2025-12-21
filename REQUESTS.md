curl -X POST http://localhost:4000/auth/login \
-H "Content-Type: application/json" \
-d '{
"email": "user@example.com",
"password": "password123"
}'

