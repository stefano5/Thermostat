echo "Step 1) update & upgrade system. Press RETURN to continue"
read
sudo apt update
sudo apt upgrade

echo "Step 2) install apache2 and gcc. Press RETURN to continue"
read
sudo apt install apache2 gcc php libapache2-mod-php -y
sudo systemctl start apache2.service

echo "Step 3) load website"
sudo rm -r /var/www/html/*
cp Website/* /var/www/html/

sudo chown $USER:$USER /var/www/html
sudo chown $USER:$USER /var/www/html/*
sudo chmod 777 /var/www
sudo chmod 777 /var/www/html/*
chmod 777 /var/www/html/


echo "Step 4) init directory"
PATHTHERMOSTAT=$HOME/.thermostat

sudo rm -rf $PATHTHERMOSTAT >> /dev/null
mkdir $PATHTHERMOSTAT
echo "$PATHTHERMOSTAT created"

cp Thermostat_JAVA/dist/*.jar $PATHTHERMOSTAT/Thermostat.jar

echo "Step 5) initialize software"
gcc -Wall -Werror thermostat.c -o $PATHTHERMOSTAT/thermostat

sudo unlink /usr/bin/thermostat >> /dev/null
sudo ln -s $PATHTHERMOSTAT/thermostat /usr/bin/

cp thermostat.sh $PATHTHERMOSTAT/

echo "0" > /var/www/html/systemOn.txt
sudo chown $USER:$USER /var/www/html/systemOn.txt
sudo chmod 777 /var/www/html/systemOn.txt


echo "Step 6) create service with systemctl. Press RETURN to continue"
read
sudo rm /etc/systemd/system/thermostat.service >> /dev/null
sudo cp thermostat.service /etc/systemd/system/
sudo systemctl enable thermostat.service
sudo systemctl start thermostat.service
sudo systemctl status thermostat.service
