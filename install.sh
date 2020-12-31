echo "Step 1) update system. Press RETURN to continue"
read
sudo apt update
sudo apt upgrade

echo "Step 2) install apache2. Press RETURN to continue"
read
sudo apt install apache2 gcc
sudo chown $USER:$USER /var/www/html
sudo chmod u+w /var/www/html

echo "Step 3) load website"
sudo rm -r /var/www/html/*
sudo cp Website/* /var/www/html/

echo "Step 4) init directory"
$PATHTHERMOSTAT=$HOME/.thermostat

mkdir $PATHTHERMOSTAT
rm -rf /$PATHTHERMOSTAT >> /dev/zero
cp Thermostat_JAVA/dist/*.jar $PATHTHERMOSTAT/Thermostat.jar

echo "Step 5) compile runner"
gcc -Wall -Werror thermostat.c -o thermostat

cp thermostat $PATHTHERMOSTAT/
sudo ln -s $PATHTHERMOSTAT/thermostat /usr/bin/

echo "Step 6) create service with systemctl"
sudo rm /etc/systemd/system >> /dev/zero
sudo cp thermostat.service /etc/systemd/system/
sudo systemctl enable thermostat.service
sudo systemctl start thermostat.service
sudo systemctl status thermostat.service

