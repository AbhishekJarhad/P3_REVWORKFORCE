-- Initialize databases for RevWorkforce
CREATE DATABASE IF NOT EXISTS revworkforce_auth;
CREATE DATABASE IF NOT EXISTS revworkforce_employees;

-- Grant permissions
GRANT ALL PRIVILEGES ON revworkforce_auth.* TO 'Abhishek'@'%';
GRANT ALL PRIVILEGES ON revworkforce_employees.* TO 'Abhishek'@'%';
FLUSH PRIVILEGES;
