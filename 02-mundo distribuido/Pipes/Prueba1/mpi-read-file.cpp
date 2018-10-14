//mpiCC mpi-read-file.cpp -o mpi-rlbl && mpirun -np 2 ./mpi-rlbl a
/*
    Send and Receive Data in MPI
*/
#include<stdio.h>
#include<mpi.h>
#include <iostream>
#include <fstream>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdlib.h>
#include <string>

#define SIZE 2541

int main(int argc, char ** argv){
    int ID=0;

    MPI_Init(&argc,&argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &ID);

    
    char SendData[SIZE];
    char ReceiveData[SIZE];
    char ch = tolower(argv[1][0]);

    printf("MPI %d - apariciones del caracter %c\n", ID, ch);

    int countChar     = 0;
    int sendCount     = 0;


    /*
    para enviar datos necesitamos:
    un buffer,
    la cantidad de datos a enviar,
    el tipo de datos a enviar,
    el destino,
    un tag
    y el grupo de comunicacion
    */
    if(ID==0){

        std::ifstream file("longFile.txt");
        std::string str; 
        while (std::getline(file, str)) {
            strcpy(SendData,str.c_str());
            sendCount++;
            MPI_Send(&SendData,SIZE, MPI_CHAR,1,7,MPI_COMM_WORLD);
            printf("envía: %s\n",SendData);
            Mpi_Wait();
        }
        printf("sendCount %d\n", sendCount);
        printf("cantidad de apariciones del caracter '%c' : %d\n", ch, countChar);
        // padre
        
    }else{
        /*
        para recibir datos necesitamos:
        un buffer,
        la cantidad de datos a recibir,
        el tipo de datos a recibir,
        el emisor de los datos,
        un tag,
        el grupo de comunicacion
        y un estado
        */
        MPI_Status status;
         //hijo
        MPI_Recv(&ReceiveData,SIZE,MPI_CHAR,MPI_ANY_SOURCE,7,MPI_COMM_WORLD,&status);
        for( unsigned int a = 0; a < SIZE; a = a + 1 ){
            if(ReceiveData[a] == '\n' || ReceiveData[a] == '\0'){
                break;
            }
            if(tolower(ReceiveData[a]) == ch){
                countChar++;
            }
        }
        printf("Ocurrencias del caracter %c : %d\n", ch, countChar);
    }
    return 0;
}