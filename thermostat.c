#include    <stdio.h>
#include    <stdlib.h>
#include    <string.h>
#include    <dirent.h>
#include    <pwd.h>
#include    <unistd.h>
#include    <errno.h>
#include    <stdint.h>
#include    <sys/types.h>
#include    <sys/stat.h>
#include    <unistd.h>

#define COMMAND "cd /home/%s/.thermostat && java -jar Thermostat.jar d"
#define TRUE    1

char user[32];


void initArray_str(char *array, int dim) {
    for (int i=0; i<dim; i++) {
        array[i] = '\0';
    }
}


/* 
 * If 'disableRoot == TRUE' if you run with sudo this function return uid 1000 by default
 * 
 * */
int getUserId(char *user, int disableRoot) {
    int uid = getuid();
    initArray_str(user, 32);
    if (uid == 0 && disableRoot == TRUE) {       //trattiamo l'user come semplice utente
        uid = 1000;
        //printf_d("[apiSO.c]->getUserId sei l'utente root ma sei stato disattivatto e sei stato trattato come user 1000\n");
        printf("[apiSO.c]->getUserId user root is disabled, now you are user 1000\n");
    }

    if (getpwuid(uid)->pw_name == NULL) {
        printf("getpwuid failed, what is your user id? [userID@namePC:~$]\n>");
        //readString(user, 32); useless
    } else {
        strcpy(user, getpwuid(uid)->pw_name);
    }
    return uid;
}

/////////////////////////


int main(int argc, char **argv) {
    char *command;
    getUserId(user, TRUE);
    command = (char*)malloc(strlen(COMMAND) + strlen(user) + 1);
    initArray_str(command, strlen(command)); 
    sprintf(command, COMMAND, user);

    //printf("[%s]\n", command);
	system(command);
    free(command);
	exit(EXIT_SUCCESS);
}
