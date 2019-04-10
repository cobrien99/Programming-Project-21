package com.company;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Cathal O'Brien
 * This is a simple program that allows the user to query our Watson models with either a piece of text or an image and
 * recieve a classification result
 * To classify an image type IC and enter the path to the filename (remember to add the file extension at the end)
 * for example to classify and jpg image called ChildLabour thats in the same folder as the program, type "ChildLabour.jpg"
 * To classify text type NLC and type in the text you want classified
 * Wait a second as the program connects to the model and dont worry about any red messages that show up first, that means its working
 *
 * */


public class Main {

    //if you retrain the IC you may need to update this key
    private static String IC_API_KEY = "bdrRokDJKBUhog2IS98y26cfFPU-GiCQdULQBU8CnXsR";

    //if you retrain the NLC you may need to update these keys
    private static String NLC_API_KEY = "uscypsckxqTExLQHqoU_pp84n4HE7hYeIogNZcycJ2Bb";
    private static String NLC_USER_ID = "830f99x515-nlc-698";

    private static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) {
        do {//keep on doing this forever until the program closes
            System.out.println("To classify an image enter: \"IC\" ");
            System.out.println("To classify text enter  \"NLC\"");
            String modelChoice = userInput.nextLine();

            if (modelChoice.matches(".*IC.*")) classifyImage();
            else if (modelChoice.matches(".*NLC.*")) classifyText();
            else System.out.println("Error");
            //if a correct input isn't entered print "Error" and ask again
        }
        while (true);
    }

    private static void classifyImage() {
        //get the name of the image file
        System.out.print("Enter the filename of the image you want to classify: ");
        String imageFilename = userInput.nextLine();

        //connect to IBM watson cloud with the appropriate settings
        IamOptions options = new IamOptions.Builder().apiKey(IC_API_KEY).build();
        VisualRecognition service = new VisualRecognition("2018-03-19", options);

        InputStream imagesStream;
        try {
            //try find the image file
            imagesStream = new FileInputStream(imageFilename);
        }
        catch (Exception e) {
            //if the file does not exist quit and start the program again
            System.out.println("Error. That file does not exist\n");
            return;
        }
        //classify the image
        ClassifyOptions classifyOptions =
                new ClassifyOptions.Builder()
                .imagesFile(imagesStream)
                .imagesFilename(imageFilename)
                .threshold((float) 0.0)
                .owners(Arrays.asList("me"))
                .build();

        //get the results
        ClassifiedImages result = service.classify(classifyOptions).execute();
        //print the results
        System.out.println(result);
    }

    private static void classifyText() {
        //setting the correct options to connect to IBM watson services
        IamOptions NLCoptions = new IamOptions.Builder().apiKey(NLC_API_KEY).build();

        //getting the text to be classified via user user input
        System.out.print("Enter the text you want to classify: ");
        String text = userInput.nextLine();

        //connecting to the NLC using the options from above
        NaturalLanguageClassifier naturalLanguageClassifier = new NaturalLanguageClassifier(NLCoptions);
        naturalLanguageClassifier.setEndPoint("https://gateway.watsonplatform.net/natural-language-classifier/api");

        //classifying the text
        //I had to use the really long object name bc if i didnt I got clashes with the ClassifyOptions object in the IC function
        com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifyOptions classifyOptions =
                new com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifyOptions.Builder()
                .classifierId(NLC_USER_ID)
                .text(text)
                .build();
        //get the result of the classification
        Classification classification = naturalLanguageClassifier.classify(classifyOptions)
                .execute();
        //print the result
        System.out.println(classification);
    }
}
