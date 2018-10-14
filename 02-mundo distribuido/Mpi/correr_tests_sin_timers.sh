#!/bin/bash
rm resultados/sinTimers/*.txt;
mpicc char_ocurrences_mpi.c -o char_occurrences;
for i in {1..10}
do
    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat shortFile.txt | mpirun -np 2 char_occurrences a >> resultados/sinTimers/timeMpiShortFile.txt') 2>> resultados/sinTimers/timeMpiShortFile.txt;

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longFile.txt | mpirun -np 2 char_occurrences a >> resultados/sinTimers/timeMpiLongFile.txt') 2>> resultados/sinTimers/timeMpiLongFile.txt;

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longloremipsum.txt | mpirun -np 2 char_occurrences a >> resultados/sinTimers/timeMpiLongIpsum.txt') 2>> resultados/sinTimers/timeMpiLongIpsum.txt;

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat 124mbloremipsum.txt | mpirun -np 2 char_occurrences a >> resultados/sinTimers/timeMpi124mbLoremIpsum.txt') 2>> resultados/sinTimers/timeMpi124mbLoremIpsum.txt;
done