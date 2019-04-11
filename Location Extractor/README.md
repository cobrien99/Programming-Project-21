# **Location Extractor**
This program extracts the location from a given text. The program takes a CSV file containing rows of text as an input. It reads each row and calls upon the Watson Natural Language Understanding API to analyse the text for locations. If locations are found, they are printed to output.csv with their corresponding text in which the location was found.

**Running program**
1. Make sure the input CSV file is specified on line **12** of NLU.py.
2. To run program type into terminal: **python NLU.py**.
3. The results of the program can be found in the newly created output.csv file.
