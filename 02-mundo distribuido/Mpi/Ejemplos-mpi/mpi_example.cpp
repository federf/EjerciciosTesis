/*
    Send and Receive Data in MPI
*/
#include<stdio.h>
#include<mpi.h>

//using namespace std;

int main(int argc, char ** argv){
    int ID=0;
    int SendData=100;
    int ReceiveData=0;
    MPI_Init(&argc,&argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &ID);


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
        MPI_Send(&SendData,1, MPI_INT,1,7,MPI_COMM_WORLD);
    }

    MPI_Status status;
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
    if(ID==1){
        printf ("Data before Receive %i\n",ReceiveData);
        MPI_Recv(&ReceiveData,1,MPI_INT,0,7,MPI_COMM_WORLD,&status);
        printf ("Data after Receive is %i\n",ReceiveData);
    }
    
    return 0;
}