#!/bin/bash
gcc -o divided_countchar divided_count_char_ocurrences_using_pipes.c
for i in {1..10}
do
    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat shortFile.txt | ./divided_countchar a >> resultados/sinTimers/timePipesShort.txt') 2>> resultados/sinTimers/timePipesShort.txt

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longFile.txt | ./divided_countchar a >> resultados/sinTimers/timePipesLong.txt') 2>> resultados/sinTimers/timePipesLong.txt

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longloremipsum.txt | ./divided_countchar a >> resultados/sinTimers/timePipesLoremIpsum.txt') 2>> resultados/sinTimers/timePipesLoremIpsum.txt

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat 124mbloremipsum.txt | ./divided_countchar a >> resultados/sinTimers/timePipes124mbLoremIpsum.txt') 2>> resultados/sinTimers/timePipes124mbLoremIpsum.txt
done