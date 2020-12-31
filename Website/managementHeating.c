/*
 *
 *      Author: stefano
 *
*/

#include <function.h>
#include <function-gpio.h>

#define TURN_ON         0
#define TURN_OFF        1
#define PROGRAM         2
#define START           3
#define STOP            4
#define RESTART         5
#define NONE            6
#define CLOSE           7

#define GPIO_RELAY      18


int action=NONE;

void managementCommand(int i) {
	if (!strcasecmp(command[i], "-h") || !strcasecmp(command[i], "--help")) {
        printf("\t%s\n", name_exe);
        printf("Commands:   -on     start heating\n");
        printf("            -off    stop heating\n");
        printf("            -p      create new program (else --program)\n");
        printf("            -start  start system\n");
        printf("            -stop   stop system\n");
        printf("            -rs     restart java software\n");
        action = CLOSE;
	} else if (!strcasecmp(command[i], "-on") || !strcmp(command[i], "--on")) {
        action = TURN_ON;
	} else if (!strcasecmp(command[i], "-off") || !strcmp(command[i], "--off")) {
        action = TURN_OFF;
	} else if (!strcasecmp(command[i], "-p") || !strcmp(command[i], "--program")) {
        action = PROGRAM;
	} else if (!strcasecmp(command[i], "-start") || !strcmp(command[i], "--start")) {
        action = START;
	} else if (!strcasecmp(command[i], "-stop") || !strcmp(command[i], "--stop")) {
        action = STOP;
	} else if (!strcasecmp(command[i], "-rs") || !strcmp(command[i], "--restart")) {
        action = RESTART;
	}
}

void managementParam(int i) {
	if (!strcasecmp(param[i], "param") || !strcasecmp(param[i], "parameter")) {

	} else if (!strcasecmp(param[i], "") || !strcmp(param[i], "")) {

	}
}

void managementArg() {
	if (countParam != 0) {
		for (PRECONDITION countParam POSTCONDITION) {
			managementParam(i);
		}
	}

	if (countCommand != 0) {
		for (PRECONDITION countCommand POSTCONDITION) {
			managementCommand(i);
		}
	}
}

void quitRoutine(int sig) {
	exit(EXIT_SUCCESS);
}

int main(int argc, char **argv) {
    signal(SIGINT, quitRoutine);
    analyzeArg(argc, argv);
    managementArg();
    int uid = getUserId(user, FALSE);
    printf("User:%s\n", user);

    if (uid == 0) { //root
        warningMessage(" You are super user then you act directly on the gpio. No program is allowed with this user\n");
        switch (action) {
            case TURN_ON:
                write_pin(GPIO_RELAY, 1);
                break;
            case TURN_OFF:
                write_pin(GPIO_RELAY, 0);
                break;
            case START:
                writeFile("/var/www/html/systemOn.txt", "1", "w");
                break;
            case STOP:
                writeFile("/var/www/html/systemOn.txt", "0", "w");
                break;
            case RESTART:
                //system("systemctl restart [nome service da creare]");
                break;
            case CLOSE: break;
            default:
                printf("Nothing to do\n");
        }
    } else {
        char hstart[6];
        char hend[6];
        char row[32];
        int dayWeek;
        switch (action) {
            case TURN_ON:
                writeFile("/home/pi/turnOnNow", "", "w");
                break;
            case TURN_OFF:
                writeFile("/home/pi/turnOffNow", "", "w");
                break;
            case START:
                writeFile("/home/pi/startSystem", "", "w");
                break;
            case STOP:
                writeFile("/home/pi/stopSystem", "", "w");
                break;
            case RESTART:
                //system("sudo systemctl restart [nome service da creare]");
                break;
            case PROGRAM:
                //row = hstart + " " + hend + " " + dayWeek + " 1";
                initArray_str(hstart, 6);
                initArray_str(hend, 6);
                initArray_str(row, 32);

                printf("Write hstart: ");
                readString(hstart, 6);
                printf("Write hend: ");
                readString(hend, 6);
                printf("Write day's week (0 -> monday, 1 -> tuesday ecc): ");
                scanf("%d", &dayWeek);

                sprintf(row, "%s %s %d 1\n", hstart, hend, dayWeek);
                printf("Generated: %s", row);
                writeFile("/var/www/program.txt", row, "a+");
                break;
            case CLOSE: break;
            default:
                printf("Nothing to do\n");

        } 
    }
    exit(EXIT_SUCCESS);
}
