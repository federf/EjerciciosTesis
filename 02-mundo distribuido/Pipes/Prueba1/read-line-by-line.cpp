//g++ read-line-by-line.cpp -o read-lbl && ./read-lbl
#include <iostream>
#include <fstream>
#include <string>

int main ()
{
  std::ifstream file("longFile.txt");
  std::string str;
  int count=0;
  int maxLineLength=0;
  while (std::getline(file, str)) {
    std::cout << str << "\n";
    std::cout << "\n";
    if(maxLineLength<=str.length()) maxLineLength=str.length();
    count++;
  }
  std::cout << count << std::endl; 
  std::cout << maxLineLength << std::endl; 
}
