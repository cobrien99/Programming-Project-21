This program is used to test our AI models and record the result to a csv, which can then be graphed
The program works by testing the models with text and images that we have already tagged so we know what the right answer is
We then record the models answer. By measuring the difference between these results we can determine the models accuracy

The program has two parts:
  1: the part where it tests our image classifier
  2: the part where it tests our natural language classifeir

  ## Image classifier
  The image classifier tests with the same 20 images all the time
  that way, we can look for performance increases over time
  These 20 images are contained in a zip file called test images
  They consist of 5 images containing child labour (named "cl" then a number),
  5 images of sweatshop conditions (named "ss" then a number)
  and 10 images with neither child labour nor sweatshops. (named "n" and then a number)    
  
  the program knows by the filename what the right answer is
  It then tests an image and writes the result line to a csv
  
  the result line consist of "date, filename, containsCL, containsSS, confidenceCL, confidenceSS"
  containsCL is 1 if the image file is named "cl". same for containsSS but for "ss" (i.e. the correct result)
  confidenceCL is the models prediction on whether the image contains CL ranging from 0.00 to 1.00. (i.e. the models prediciton)
 
  ## Natural language classifier
  the NLC has a csv file containing a lot of test sentences
  these test sentences consist of the sentecne itself then the labels for that sentence (the correct results)
  The program takes a random batch of 20 of those sentences and tests the model on those.
  
  I used a random batch of 20 sentences because it prevents overfitting and also because IBM only
  allows submissions of less then 30 sentences at once.
  
  The program tests each sentence and records the result line
  the format is date, sentenceText, sentencelabels*10, labelConfidence*10
  because we are testing for ten tags there are ten labels and confidence scores

  ## Graph
  The graph and all the calculations are in the excel file called "graphs". Whenever the program has run you have to press 
  "refresh all" to get the new data.
  You can chnage the threshold figure (highlighted in green) from 0 to 1 and see the graphs update in real time. The threshold figure represents how confident the model has to be before it will say certain tags are found in the item.
