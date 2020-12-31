echo "Step 1) update system. Press RETURN to continue"
read
sudo apt update
sudo apt upgrade

echo "Step 2) install apache2. Press RETURN to continue"
read
sudo apt install apache2 
sudo chown $USER:$USER /var/www/html
sudo chmod u+w /var/www/html

echo "Step 3) load website"
sudo cp Website/* /var/www/html/

cp Thermostat_JAVA/dist/*.jar Thermostat

