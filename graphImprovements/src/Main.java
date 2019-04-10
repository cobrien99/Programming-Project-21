import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.*;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImage;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;

/**
 * @author Cathal O'Brien
 *
 * This program is used to test our AI models and record the result to a csv, which can then be graphed
 * The program works by testing the models with text and images that we have already tagged so we know what the right answer is
 * We then record the models answer. By measuring the difference between these results we can determine the models accuracy
 *
 * The program has two parts:
 *      1: the part where it tests our image classifier
 *      2: the part where it tests our natural language classifeir
 *
 * 1:Image classifier
 *      The image classifier tests with the same 20 images all the time
 *      that way, we can look for performance increases over time
 *      These 20 images are contained in a zip file called test images
 *      They consist of 5 images containing child labour (named "cl" then a number),
 *      5 images of sweatshop conditions (named "ss" then a number)
 *      and 10 images with neither child labour nor sweatshops. (named "n" and then a number)
 *
 *      the program knows by the filename what the right answer is
 *      It then tests an image and writes the result line to a csv
 *      the result line consist of "date, filename, containsCL, containsSS, confidenceCL, confidenceSS"
 *      containsCL is 1 if the image file is named "cl". same for containsSS but for "ss" (i.e. the correct result)
 *      confidenceCL is the models prediction on whether the image contains CL ranging from 0.00 to 1.00. (i.e. the models prediciton)
 *
 * 2:Natural language classifier
 *      the NLC has a csv file containing a lot of test sentences
 *      these test sentences consist of the sentecne itself then the labels for that sentence (the correct results)
 *      The program takes a random batch of 20 of those sentences and tests the model on those.
 *      I used a random batch of 20 sentences because it prevents overfitting and also because IBM only
 *      allows submissions of less then 30 sentences at once.
 *
 *      The program tests each sentence and records the result line
 *      the format is date, sentenceText, sentencelabels*10, labelConfidence*10
 *      because we are testing for ten tags there are ten labels and confidence scores
 *
 * */

public class Main {
    public static int NLCBATCHSIZE = 20;//does not work for batches > 30
    public static int ICBATCHSIZE = 20;//dont change from 20

    //if you retrain the IC you may need to update this key
    private static String IC_API_KEY = "bdrRokDJKBUhog2IS98y26cfFPU-GiCQdULQBU8CnXsR";

    //if you retrain the NLC you may need to update these keys
    private static String NLC_API_KEY = "uscypsckxqTExLQHqoU_pp84n4HE7hYeIogNZcycJ2Bb";
    private static String NLC_USER_ID = "830f99x515-nlc-698";


    public static void main(String[] args) throws IOException {
        testImageClassifier();
        testNaturalLanguageClassifier();
    }

    private static void testImageClassifier() throws IOException {
        //connect to IBM watson cloud with the appropriate settings
        IamOptions options = new IamOptions.Builder()
                .apiKey(IC_API_KEY)
                .build();

        VisualRecognition service = new VisualRecognition("2018-03-19", options);

        //classifies all the images
        InputStream imagesStream = new FileInputStream("test images.zip");
        ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                .imagesFile(imagesStream)
                .imagesFilename("test images.zip")
                .threshold((float) 0.0)
                .classifierIds(Arrays.asList("childLabourSweatshops_1778082159"))
                .build();

        //records the results
        ClassifiedImages result = service.classify(classifyOptions).execute();

        //Will save data as a csv in the form
        //date, name, containsCL, containsSS, confidenceCL, confidenceSS

        FileWriter fw = new FileWriter("results.csv", true);
        BufferedWriter bw = new BufferedWriter(fw);

        //gets the current date so we can record that in the csv
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        for (int i = 0; i < ICBATCHSIZE; i++) {
            //gets the nth image
            //builds up an output string for each image containing all the info about that image and the test results
            //writes that string to the csv at the end of the for loop
            ClassifiedImage image = result.getImages().get(i);
            StringBuilder outputString = new StringBuilder();

            //gets the date and image filename and appends that to the output string
            outputString.append(date).append(", ");
            outputString.append(image.getImage()).append(", ");

            int childLabourLabel = 0;
            int sweatshopLabel = 0;

            //reads the image filename to see if the image contains CL or SS
            //if it contains either CL or SS it sets the label to 1
            //otherwise it is 0
            if (image.getImage().matches(".*[^n]c.*")) {
                //System.out.println("child labour");
                childLabourLabel = 1;
            }
            else if (image.getImage().matches(".*[^ne]s.*")) {
                //System.out.println("sweatShop");
                sweatshopLabel = 1;
            }

            outputString.append(childLabourLabel).append(", ");
            outputString.append(sweatshopLabel).append(", ");

            //gets the models predictions and appends them to the output string
            float confidenceCL = image.getClassifiers().get(0).getClasses().get(0).getScore();
            float confidenceSS = image.getClassifiers().get(0).getClasses().get(1).getScore();

            outputString.append(confidenceCL).append(", ");
            outputString.append(confidenceSS).append("\n");

            //writes the output string to the net line of the file and repeats
            bw.append(outputString.toString());
        }

        bw.close();

    }

    public static void testNaturalLanguageClassifier() throws IOException {
        //array to hold the 10 labels for each of the images in the image batch
        String[][] labels = new String[NLCBATCHSIZE][];

        //gets the result of classifying the test sentences
        ClassificationCollection result = classifyTestSentences(labels, "NLC test sentences.csv");


        //this function records the results of the tests to the csv
        recordNlcResults(result, labels);


    }

    private static void recordNlcResults(ClassificationCollection result, String[][] labels) throws IOException {
        //opens the results csv file
        FileWriter fw = new FileWriter("NLCresults.csv", true);
        BufferedWriter bw = new BufferedWriter(fw);

        for (int i = 0; i < NLCBATCHSIZE; i++) {
            //gets the text of the sentence
            CollectionItem sentence = result.getCollection().get(i);
            System.out.println("\n" + "sentence " + (i+1) + ": " + sentence.getText());
            System.out.println("Sentence tags = " + Arrays.toString(labels[i]) + "\n");

            StringBuilder outputString = new StringBuilder();
            //each row in csv in the form
            //date, sentenceText, sentencelabels[], labelConfidence[i]*10

            //writes the current date and text to the csv
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            outputString.append(date).append(", ");
            outputString.append(sentence.getText());

            //writes the sentence labels to the output string
            outputString.append(getSentenceLabels(labels[i]));


            //uses this comparator to sort the list in alphabetical order because by
            //default the list is sorted by highest confidence
            Comparator<ClassifiedClass> comparator = Comparator.comparing(ClassifiedClass::getClassName);

            List<ClassifiedClass> types = sentence.getClasses();
            types.sort(comparator);

            for (int j = 0; j < types.size(); j++) {
                //gets the current class (e.g. child labour, sexual exploitation etc) and the confidence for that class
                //being in the text
                ClassifiedClass currentClass = types.get(j);
                System.out.printf("%s: %.2f\n", currentClass.getClassName(), currentClass.getConfidence());
                outputString.append(", ").append(String.format("%.2f", currentClass.getConfidence()));
            }

            outputString.append("\n");
            System.out.print(outputString.toString());
            //writes the output string to the csv
            bw.append(outputString);
            //System.out.println("Successfully written results to file : NLCresults.csv");
        }

        bw.close();
    }

    private static ClassificationCollection classifyTestSentences(String[][] labels, String testSentencesPath) throws IOException {
        //setting the correct options to connect to IBM watson services
        IamOptions options = new IamOptions.Builder()
                .apiKey(NLC_API_KEY)
                .build();

        //connecting to the NLC using the options from above
        NaturalLanguageClassifier naturalLanguageClassifier = new NaturalLanguageClassifier(options);
        naturalLanguageClassifier.setEndPoint("https://gateway.watsonplatform.net/natural-language-classifier/api");


        //this function returns a random subset of the larger test sentences csv
        //the subset is of size NLC_BATCH_SIZE
        //the function returns an array of sentences ready to be classified
        ClassifyInput[] input = getNlcTestBatch(labels, testSentencesPath);


        List<ClassifyInput> inputCollection = Arrays.asList(input);
        //classifies all the test sentences
        ClassifyCollectionOptions classifyOptions = new ClassifyCollectionOptions.Builder()
                .classifierId(NLC_USER_ID)
                .collection(inputCollection)
                .build();
        //returns the results of the tests
        return naturalLanguageClassifier
                .classifyCollection(classifyOptions).execute();
    }


    public static ClassifyInput[] getNlcTestBatch(String[][] batchLabels, String filePath) throws IOException {
        //converts the test csv to an arraylist
        String currentLine;
        FileInputStream fis = new FileInputStream(filePath);
        DataInputStream myInput = new DataInputStream(fis);
        ArrayList<String[]> testSentences = new ArrayList<String[]>();

        while ((currentLine = myInput.readLine()) != null) {
            testSentences.add(currentLine.split(","));
        }


        ClassifyInput[] input = new ClassifyInput[NLCBATCHSIZE];

        //gets NLCBATCHSIZE random inputs from our file containing loads of sentences
        Random random = new Random();
        for (int i = 0; i < NLCBATCHSIZE; i++) {
            int index = random.nextInt(testSentences.size());

            input[i] = new ClassifyInput();
            input[i].setText(testSentences.get(index)[0]);

            batchLabels[i] = Arrays.copyOfRange(testSentences.get(index), 1, testSentences.get(index).length);

            testSentences.remove(index);
        }
        return input;
    }


    public static String getSentenceLabels(String labels[]) {
        String[] results = new String[10];
        StringBuilder resultString = new StringBuilder();
        for (int i =0; i <results.length; i++) {results[i] = "0";}//initalise the array with zeros

        for (String label : labels) {
            if (label.matches(".*Child Labour")) results[0] = "1";
            else if (label.matches(".*Child Sexual Exploitation.*")) results[1] = "1";
            else if (label.matches(".*Child Trafficking.*")) results[2] = "1";
            else if (label.matches(".*Domestic Servitude.*")) results[3] = "1";
            else if (label.matches(".*Forced Criminality.*")) results[4] = "1";
            else if (label.matches(".*Forced Labour.*")) results[5] = "1";
            else if (label.matches(".*Forced Marriage.*")) results[6] = "1";
            else if (label.matches(".*Labour Exploitation.*")) results[7] = "1";
            else if (label.matches(".*[^cC].*Sexual Exploitation.*")) results[8] = "1";//"[^cC].*" is so "Child sexual exploitation" doesnt match "sexual exploitation"
            else if (label.matches(".*other.*")) results[9] = "1";
        }
        for (String result : results) {
            resultString.append(" ,").append(result);
        }
        return  resultString.toString();
    }
}
