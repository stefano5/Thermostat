echo "Step 1) update & upgrade system. Press RETURN to continue"
read
sudo apt update
sudo apt upgrade

echo "Step 2) install apache2 and gcc. Press RETURN to continue"
read
sudo apt install apache2 gcc
sudo systemctl start apache2.service
sudo chown $USER:$USER /var/www/html
sudo chmod u+w /var/www/html

echo "Step 3) load website"
sudo rm -r /var/www/html/*
sudo cp Website/* /var/www/html/

echo "Step 4) init directory"
PATHTHERMOSTAT=$HOME/.thermostat

sudo rm -rf $PATHTHERMOSTAT >> /dev/zero
mkdir $PATHTHERMOSTAT
echo "$PATHTHERMOSTAT created"

cp Thermostat_JAVA/dist/*.jar $PATHTHERMOSTAT/Thermostat.jar

echo "Step 5) initialize software"
gcc -Wall -Werror thermostat.c -o $PATHTHERMOSTAT/thermostat

sudo unlink /usr/bin/thermostat >> /dev/zero
sudo ln -s $PATHTHERMOSTAT/thermostat /usr/bin/

cp thermostat.sh $PATHTHERMOSTAT/

echo "Step 6) create service with systemctl"
sudo rm /etc/systemd/system/thermostat.service >> /dev/zero
sudo cp thermostat.service /etc/systemd/system/
sudo systemctl enable thermostat.service
sudo systemctl start thermostat.service
sudo systemctl status thermostat.service
